package com.w.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.w.srb.core.enums.BorrowerStatusEnum;
import com.w.srb.core.enums.IntegralEnum;
import com.w.srb.core.mapper.BorrowerAttachMapper;
import com.w.srb.core.mapper.UserInfoMapper;
import com.w.srb.core.mapper.UserIntegralMapper;
import com.w.srb.core.pojo.entity.Borrower;
import com.w.srb.core.mapper.BorrowerMapper;
import com.w.srb.core.pojo.entity.BorrowerAttach;
import com.w.srb.core.pojo.entity.UserInfo;
import com.w.srb.core.pojo.entity.UserIntegral;
import com.w.srb.core.pojo.vo.BorrowerApprovalVO;
import com.w.srb.core.pojo.vo.BorrowerAttachVO;
import com.w.srb.core.pojo.vo.BorrowerDetailVO;
import com.w.srb.core.pojo.vo.BorrowerVO;
import com.w.srb.core.service.BorrowerAttachService;
import com.w.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.w.srb.core.service.DictService;
import com.w.srb.core.service.UserInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerAttachService borrowerAttachService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);

        //保存借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO,borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        baseMapper.insert(borrower);


        //保存附件
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        //更新会员装态，更新为认证中
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getBorrowerStatus(Long userId) {
        QueryWrapper<Borrower> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.select("status").eq("user_id",userId);
        List<Object> list = baseMapper.selectObjs(userInfoQueryWrapper);
        if (list.size()==0){
            //借款人信息尚未提交
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        Integer status = (Integer) list.get(0);
        return status;
    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> borrowerPage, String keyword) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isEmpty(keyword)){
            return baseMapper.selectPage(borrowerPage,null);
        }
        borrowerQueryWrapper.like("name",keyword)
                .or().like("id_card",keyword)
                .or().like("mobile",keyword)
                .orderByDesc("id");
        return baseMapper.selectPage(borrowerPage,borrowerQueryWrapper);
    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {
        //获取借款人信息
        Borrower borrower = baseMapper.selectById(id);

        //填充基本借款人信息
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        BeanUtils.copyProperties(borrower,borrowerDetailVO);

        borrowerDetailVO.setSex(borrower.getSex()==1?"男":"女");
        borrowerDetailVO.setMarry(borrower.getMarry()?"是":"否");

        //计算下拉列表选中内容
        String industry = dictService.getNameByParentDictCodeAndValue("industry", borrower.getIndustry());
        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String relation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());


        //设置下拉列表选中内容
        borrowerDetailVO.setIndustry(industry);
        borrowerDetailVO.setEducation(education);
        borrowerDetailVO.setIncome(income);
        borrowerDetailVO.setReturnSource(returnSource);
        borrowerDetailVO.setContactsRelation(relation);

        //审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVO.setStatus(status);

        //获取附件VO列表
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.selectBorrowerAttachVOList(id);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);
        return borrowerDetailVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        //借款人认证状态
        Long borrowerId = borrowerApprovalVO.getBorrowerId();
        Borrower borrower = baseMapper.selectById(borrowerId);
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);

        Long userId = borrower.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //添加积分基本信息
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);

        //总积分
        Integer curIntegral = userInfo.getIntegral()+borrowerApprovalVO.getInfoIntegral();

        //身份证信息
        if (borrowerApprovalVO.getIsIdCardOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            curIntegral+=IntegralEnum.BORROWER_IDCARD.getIntegral();
        }
        //车辆信息
        if (borrowerApprovalVO.getIsCarOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            curIntegral+=IntegralEnum.BORROWER_CAR.getIntegral();
        }
        //房产信息
        if (borrowerApprovalVO.getIsHouseOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            curIntegral+=IntegralEnum.BORROWER_HOUSE.getIntegral();
        }

        userInfo.setIntegral(curIntegral);
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfoMapper.updateById(userInfo);


    }
}
