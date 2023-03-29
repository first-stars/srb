package com.w.srb.core.service;

import com.w.srb.core.pojo.bo.TransFlowBO;
import com.w.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface TransFlowService extends IService<TransFlow> {
    void saveTransFlow(TransFlowBO transFlowBO);
    boolean isSaveTransFlow(String agentBillNo);

    /**
     * 获取资金记录列表
     * @param userId
     * @return
     */
    List<TransFlow> selectByUserId(Long userId);
}
