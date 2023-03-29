package com.w.srb.core.service;

import com.w.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface UserAccountService extends IService<UserAccount> {

    String commitCharge(BigDecimal chargeAmt, Long userId);

    String notify(Map<String, Object> paramMap);

    /**
     * 获取用户当前金额
     * @param userId
     * @return
     */
    BigDecimal getAccount(Long userId);

    String commitWithdraw(BigDecimal fetchAmt, Long userId);

    void notifyWithdraw(Map<String, Object> paramMap);
}