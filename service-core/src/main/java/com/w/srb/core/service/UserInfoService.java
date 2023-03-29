package com.w.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.w.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.w.srb.core.pojo.query.UserInfoQuery;
import com.w.srb.core.pojo.vo.LoginVO;
import com.w.srb.core.pojo.vo.RegisterVO;
import com.w.srb.core.pojo.vo.UserIndexVO;
import com.w.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO registerVO);

    UserInfoVO login(LoginVO loginVO, String ip);

    IPage<UserInfo> listPage(Page<UserInfo> userInfoPage, UserInfoQuery userInfoQuery);

    void lock(Long id,Integer status);

    boolean checkMobile(Long mobile);

    /**
     * 获取个人空间信息
     * @param userId
     * @return
     */
    UserIndexVO getIndexUserInfo(Long userId);

    /**
     * 根据bindCode获取手机号
     * @param bindCode
     * @return
     */
    String getMobileByBindCode(String bindCode);
}
