package com.w.srb.core.controller.admin;

import com.w.common.result.R;
import com.w.srb.core.pojo.entity.BorrowInfo;
import com.w.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.w.srb.core.pojo.vo.BorrowerApprovalVO;
import com.w.srb.core.service.BorrowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xin
 * @date 2022-10-20-17:23
 */
@Api(tags = "借款管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/borrowInfo")
public class AdminBorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @GetMapping("/list")
    public R list(){
        List<BorrowInfo> borrowInfoList = borrowInfoService.selectList();
        return R.ok().data("list",borrowInfoList);
    }

    @ApiOperation("获取借款信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "借款id" , required = true)
            @PathVariable("id") Long id
    ){
        Map<String,Object> borrowInfoDetail = borrowInfoService.getBorrowInfoDetail(id);
        return R.ok().data("borrowInfoDetail",borrowInfoDetail);
    }

    @ApiOperation("审批借款信息")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO){
        borrowInfoService.approval(borrowInfoApprovalVO);

        return R.ok().message("审批完成");
    }
}
