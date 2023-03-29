package com.w.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.w.common.exception.Assert;
import com.w.common.result.ResponseEnum;
import com.w.srb.core.enums.BorrowInfoStatusEnum;
import com.w.srb.core.enums.BorrowerStatusEnum;
import com.w.srb.core.enums.UserBindEnum;
import com.w.srb.core.mapper.BorrowerMapper;
import com.w.srb.core.mapper.IntegralGradeMapper;
import com.w.srb.core.mapper.UserInfoMapper;
import com.w.srb.core.pojo.entity.BorrowInfo;
import com.w.srb.core.mapper.BorrowInfoMapper;
import com.w.srb.core.pojo.entity.Borrower;
import com.w.srb.core.pojo.entity.IntegralGrade;
import com.w.srb.core.pojo.entity.UserInfo;
import com.w.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.w.srb.core.pojo.vo.BorrowerApprovalVO;
import com.w.srb.core.pojo.vo.BorrowerDetailVO;
import com.w.srb.core.service.BorrowInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.w.srb.core.service.BorrowerService;
import com.w.srb.core.service.DictService;
import com.w.srb.core.service.LendService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private LendService lendService;

    @Override
    public BigDecimal getBorrowAmount(Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        Integer integral = userInfo.getIntegral();

        //根据积分查询借款额度
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper.le("integral_start",integral).ge("integral_end",integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);

        if (integralGrade==null){
            return new BigDecimal("0");
        }

        return integralGrade.getBorrowAmount();
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        //获取userinfo的用户数据
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //判断用户绑定装态
        Assert.isTrue(
                userInfo.getBindStatus().intValue()== UserBindEnum.BIND_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_BIND_ERROR);

        //判断用户信息是否审核通过
        Assert.isTrue(userInfo.getBorrowAuthStatus().intValue()== BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_AMOUNT_ERROR);

        //判断借款金额是否小于借款额度
        Assert.isTrue(borrowInfo.getAmount().doubleValue() <= this.getBorrowAmount(userId).doubleValue(),
                ResponseEnum.USER_AMOUNT_LESS_ERROR);

        //存储数据
        borrowInfo.setUserId(userId);
        //百分比转换成小数
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);

    }

    @Override
    public Integer getBorrowInfoStatus(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.select("status").eq("user_id",userId);
        List<Object> objects = baseMapper.selectObjs(borrowInfoQueryWrapper);
        if (objects.size() == 0){
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        Integer status = (Integer) objects.get(0);
        return status;
    }

    @Override
    public List<BorrowInfo> selectList() {
        List<BorrowInfo> borrowInfoList = baseMapper.selectBorrowInfoList();
        borrowInfoList.forEach(borrowInfo -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
            borrowInfo.getParam().put("returnMethod",returnMethod);
            borrowInfo.getParam().put("moneyUse",moneyUse);
            borrowInfo.getParam().put("status",status);
        });

        return borrowInfoList;
    }

    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {

        //查询借款对象
        BorrowInfo borrowInfo = baseMapper.selectById(id);
        //组装数据
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("returnMethod",returnMethod);
        borrowInfo.getParam().put("moneyUse",moneyUse);
        borrowInfo.getParam().put("status",status);

        //根据user_id获取借款人对象
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id",borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        //组装借款人对象
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());
        //组装数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("borrowInfo",borrowInfo);
        map.put("borrower",borrowerDetailVO);
        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        //修改借款信息状态
        Long borrowInfoid = borrowInfoApprovalVO.getId();
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoid);
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(borrowInfo);
        //审核通过则创建标的
        if (borrowInfoApprovalVO.getStatus().intValue()==BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()){
            lendService.createLend(borrowInfoApprovalVO,borrowInfo);
        }

    }
}
