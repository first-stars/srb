package com.w.srb.core.service;

import com.w.srb.core.pojo.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.w.srb.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface UserBindService extends IService<UserBind> {

    String commitBindUser(UserBindVO userBindVO, Long userId);

    void notify(Map<String, Object> map);


    /**
     * 获取bindCode
     * @param userId
     * @return
     */
    String getBindCodeByUserId(Long userId);
}
