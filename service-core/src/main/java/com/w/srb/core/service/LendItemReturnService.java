package com.w.srb.core.service;

import com.w.srb.core.pojo.entity.LendItemReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface LendItemReturnService extends IService<LendItemReturn> {

    List<LendItemReturn> selectByLendId(Long lendId, Long userId);

    /**
     * 添加还款明细
     * @param lendReturnId
     * @return
     */
    List<Map<String, Object>> addReturnDetail(Long lendReturnId);

    /**
     * 根据还款计划id获取对应的回款计划列表
     * @param lendReturnId
     * @return
     */
    List<LendItemReturn> selectLendItemReturnList(Long lendReturnId);
}
