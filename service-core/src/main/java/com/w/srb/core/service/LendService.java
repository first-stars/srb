package com.w.srb.core.service;

import com.w.srb.core.pojo.entity.BorrowInfo;
import com.w.srb.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.w.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface LendService extends IService<Lend> {

    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    List<Lend> selectList();

    Map<String, Object> show(Long id);

    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod);

    /**
     *  满标放款
     * @param id
     */
    void makeLoan(Long lendId);
}
