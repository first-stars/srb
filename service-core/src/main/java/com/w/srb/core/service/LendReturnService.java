package com.w.srb.core.service;

import com.w.srb.core.pojo.entity.LendReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface LendReturnService extends IService<LendReturn> {

    List<LendReturn> selectByLendId(Long lendId);

    /**
     * 用户还款
     * @param lendReturnId
     * @param userId
     * @return
     */
    String commitReturn(Long lendReturnId, Long userId);

    void notify(Map<String, Object> paramMap);
}
