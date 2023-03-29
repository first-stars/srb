package com.w.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.w.common.exception.Assert;
import com.w.common.result.ResponseEnum;
import com.w.common.util.MD5;
import com.w.srb.base.util.JwtUtils;
import com.w.srb.core.mapper.UserAccountMapper;
import com.w.srb.core.mapper.UserLoginRecordMapper;
import com.w.srb.core.pojo.entity.UserAccount;
import com.w.srb.core.pojo.entity.UserInfo;
import com.w.srb.core.mapper.UserInfoMapper;
import com.w.srb.core.pojo.entity.UserLoginRecord;
import com.w.srb.core.pojo.query.UserInfoQuery;
import com.w.srb.core.pojo.vo.LoginVO;
import com.w.srb.core.pojo.vo.RegisterVO;
import com.w.srb.core.pojo.vo.UserIndexVO;
import com.w.srb.core.pojo.vo.UserInfoVO;
import com.w.srb.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;





    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void register(RegisterVO registerVO) {

        //判断用户是否被注册
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", registerVO.getMobile());
        Integer count = baseMapper.selectCount(queryWrapper);
        //MOBILE_EXIST_ERROR(-207, "手机号已被注册"),
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        //插入用户基本信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setStatus(UserInfo.STATUS_NORMAL); //正常
        //设置一张静态资源服务器上的头像图片
        userInfo.setHeadImg("https://srb-w-2.oss-cn-hangzhou.aliyuncs.com/qqqq/2022/10/11/9c4b1d2f-e689-476d-8e8c-90fbd646273d.jpg");
        baseMapper.insert(userInfo);

        //创建会员账户
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);
    }

    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        Integer userType = loginVO.getUserType();
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();

        //用户是否存在
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile)
                .eq("user_type",userType);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);
        //用户不存在
        //LOGIN_MOBILE_ERROR(-208, "用户不存在"),
        Assert.notNull(userInfo,ResponseEnum.LOGIN_MOBILE_ERROR);

        //校验密码
        //LOGIN_PASSWORD_ERROR(-209, "密码不正确"),
        Assert.equals(MD5.encrypt(password),userInfo.getPassword(),ResponseEnum.LOGIN_PASSWORD_ERROR);

        //用户是否被禁用
        //LOGIN_DISABLED_ERROR(-210, "用户已被禁用"),
        Assert.equals(userInfo.getStatus(),UserInfo.STATUS_NORMAL,ResponseEnum.LOGIN_LOKED_ERROR);

        //记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();

        //对象拷贝
        BeanUtils.copyProperties(userInfo,userLoginRecord);
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);

        //记录登录日志
        int insert = userLoginRecordMapper.insert(userLoginRecord);

        //生成token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());

        UserInfoVO userInfoVO = new UserInfoVO();
        //对象拷贝
        BeanUtils.copyProperties(userInfo,userInfoVO);
        userInfoVO.setToken(token);

        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> userInfoPage, UserInfoQuery userInfoQuery) {
        if (userInfoQuery==null){
            return baseMapper.selectPage(userInfoPage,null);
        }

        String mobile = userInfoQuery.getMobile();
        Integer status = userInfoQuery.getStatus();
        Integer userType = userInfoQuery.getUserType();

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq(StringUtils.isNotBlank(mobile),"moble",mobile)
                .eq(status!=null,"status",status)
                .eq(userType!=null,"user_type",userType);


        return baseMapper.selectPage(userInfoPage,userInfoQueryWrapper);


    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public boolean checkMobile(Long mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile);
        Integer integer = baseMapper.selectCount(userInfoQueryWrapper);
        return integer>0;
    }

    @Override
    public UserIndexVO getIndexUserInfo(Long userId) {
        UserIndexVO userIndexVO = new UserIndexVO();
        userIndexVO.setUserId(userId);

        UserInfo userInfo = baseMapper.selectById(userId);

        BeanUtils.copyProperties(userInfo,userIndexVO);

        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id",userId);
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);
        userIndexVO.setAmount(userAccount.getAmount());//账户余额
        userIndexVO.setFreezeAmount(userAccount.getFreezeAmount());//冻结金额


        QueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new QueryWrapper<>();
        userLoginRecordQueryWrapper.eq("user_id",userId).orderByDesc("id").last("limit 1");
        UserLoginRecord userLoginRecord = userLoginRecordMapper.selectOne(userLoginRecordQueryWrapper);
        userIndexVO.setLastLoginTime(userLoginRecord.getCreateTime());//最后登陆时间

        return userIndexVO;
    }

    @Override
    public String getMobileByBindCode(String bindCode) {

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("bind_code",bindCode);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);

        return userInfo.getMobile();
    }
}
