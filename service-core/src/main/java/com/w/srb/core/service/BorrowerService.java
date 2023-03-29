package com.w.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.w.srb.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.w.srb.core.pojo.vo.BorrowerApprovalVO;
import com.w.srb.core.pojo.vo.BorrowerDetailVO;
import com.w.srb.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface BorrowerService extends IService<Borrower> {


    void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId);

    Integer getBorrowerStatus(Long userId);

    IPage<Borrower> listPage(Page<Borrower> borrowerPage, String keyword);

    BorrowerDetailVO getBorrowerDetailVOById(Long id);

    void approval(BorrowerApprovalVO borrowerApprovalVO);
}
