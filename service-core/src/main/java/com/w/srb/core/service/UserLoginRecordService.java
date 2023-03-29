package com.w.srb.core.service;

import com.w.srb.core.pojo.entity.UserLoginRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {


    List<UserLoginRecord> listTop50(Long userId);
}
