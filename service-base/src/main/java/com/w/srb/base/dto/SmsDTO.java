package com.w.srb.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xin
 * @date 2022-10-27-16:45
 */
@Data
@ApiModel(description = "短信")
public class SmsDTO implements Serializable {

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "消息内容")
    private String message;
}