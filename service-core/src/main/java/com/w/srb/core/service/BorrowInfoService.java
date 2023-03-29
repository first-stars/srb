package com.w.srb.core.service;

import com.w.srb.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.w.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.w.srb.core.pojo.vo.BorrowerApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    BigDecimal getBorrowAmount(Long userId);

    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    Integer getBorrowInfoStatus(Long userId);

    List<BorrowInfo> selectList();

    Map<String, Object> getBorrowInfoDetail(Long id);

    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
