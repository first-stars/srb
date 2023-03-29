package com.w.srb.core.service;

import com.w.srb.core.pojo.entity.BorrowerAttach;
import com.baomidou.mybatisplus.extension.service.IService;
import com.w.srb.core.pojo.vo.BorrowerAttachVO;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    List<BorrowerAttachVO> selectBorrowerAttachVOList(Long borrowerId);
}
