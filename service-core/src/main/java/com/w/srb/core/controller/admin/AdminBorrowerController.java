package com.w.srb.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.w.common.result.R;
import com.w.srb.core.pojo.entity.Borrower;
import com.w.srb.core.pojo.vo.BorrowerApprovalVO;
import com.w.srb.core.pojo.vo.BorrowerDetailVO;
import com.w.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xin
 * @date 2022-10-18-17:11
 */
@Api(tags = "借款人管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable("page") Long page,

            @ApiParam(value = "每页记录数", required = true)
            @PathVariable("limit") Long limit,

            @ApiParam(value = "查询关键字" , required = false)
            @RequestParam String keyword){

        Page<Borrower> borrowerPage = new Page<>(page,limit);
        IPage<Borrower> borrowerIPage = borrowerService.listPage(borrowerPage, keyword);
        return R.ok().data("pageModel",borrowerIPage);
    }

    @ApiOperation("借款人信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "借款人id" , required = true)
            @PathVariable("id") Long id){

        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);

        return R.ok().data("borrowerDetailVO",borrowerDetailVO);
    }

    @ApiOperation("借款人审批")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO){
        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("审批完成");
    }
}
