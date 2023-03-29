package com.w.srb.core.service;

import com.w.srb.core.pojo.entity.LendItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.w.srb.core.pojo.vo.InvestVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface LendItemService extends IService<LendItem> {

    String commitInvest(InvestVO investVO);

    void notify(Map<String, Object> paramMap);

    /**
     * 根据lendId获取投资记录
     * @param lendId
     * @param i
     * @return List<LendItem>
     */
    List<LendItem> selectByLendId(Long lendId, Integer status);

    List<LendItem> selectByLendId(Long lendId);

}
