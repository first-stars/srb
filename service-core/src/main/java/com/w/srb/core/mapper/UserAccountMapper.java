package com.w.srb.core.mapper;

import com.w.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    /**
     * 修改金额
     * @param bindCode
     * @param amount
     * @param freezeAmount
     */
    void updateAccount(
            @Param("bindCode") String bindCode,
            @Param("amount") BigDecimal amount,
            @Param("freezeAmount") BigDecimal freezeAmount);
}
