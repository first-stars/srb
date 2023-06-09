package com.w.srb.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.w.common.exception.Assert;
import com.w.common.result.ResponseEnum;
import com.w.srb.base.dto.SmsDTO;
import com.w.srb.core.enums.TransTypeEnum;
import com.w.srb.core.huf.FormHelper;
import com.w.srb.core.huf.HfbConst;
import com.w.srb.core.huf.RequestHelper;
import com.w.srb.core.mapper.UserInfoMapper;
import com.w.srb.core.pojo.bo.TransFlowBO;
import com.w.srb.core.pojo.entity.UserAccount;
import com.w.srb.core.mapper.UserAccountMapper;
import com.w.srb.core.pojo.entity.UserInfo;
import com.w.srb.core.service.TransFlowService;
import com.w.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.w.srb.core.service.UserBindService;
import com.w.srb.core.service.UserInfoService;
import com.w.srb.core.util.LendNoUtils;
import com.w.srb.rabbitutil.constant.MQConst;
import com.w.srb.rabbitutil.service.MQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Resource
    private UserInfoMapper userInfoMapper;


    @Resource
    private TransFlowService transFlowService;

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private UserBindService userBindService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private MQService mqService;


    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        String fromStr = "";
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        //判断账户绑定状态
        Assert.notNull(bindCode, ResponseEnum.USER_NO_BIND_ERROR);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.USERBIND_URL);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode",bindCode);
        paramMap.put("chargeAmt",chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);//检查常量是否正确
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {
        log.info("充值成功: "+ JSON.toJSONString(paramMap));
        //判断交易流水是否存在
        String agentBillNo = (String) paramMap.get("agentBillNo");//商户充值订单号
        boolean isSave = transFlowService.isSaveTransFlow(agentBillNo);
        if (isSave){
            log.warn("幂等性返回");
            return "success";
        }

        //充值人绑定协议号
        String bindCode = (String) paramMap.get("bindCode");
        //充值金额
        String chargeAmt = (String) paramMap.get("chargeAmt");

        baseMapper.updateAccount(bindCode,new BigDecimal(chargeAmt),new BigDecimal(0));

        //记录账户流水

        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值");
        transFlowService.saveTransFlow(transFlowBO);

        //发消息
        String mobile = userInfoService.getMobileByBindCode(bindCode);
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setMobile(mobile);
        smsDTO.setMessage("充值成功");
        mqService.sendMessage(
                MQConst.EXCHANGE_TOPIC_SMS,
                MQConst.ROUTING_SMS_ITEM,
                smsDTO
        );

        return "success";
    }


    @Override
    public BigDecimal getAccount(Long userId) {
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id" ,userId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);
        BigDecimal amount = userAccount.getAmount();
        return amount;
    }

    @Override
    public String commitWithdraw(BigDecimal fetchAmt, Long userId) {
        //账户可用余额充足：当前用户的余额 >= 当前用户的提现金额
        BigDecimal account = userAccountService.getAccount(userId);
        Assert.isTrue(account.intValue() >= fetchAmt.intValue(),
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);
        Map<String, Object> paramMap = new HashMap<>();

        String bindCode = userBindService.getBindCodeByUserId(userId);
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("fetchAmt", fetchAmt);
        paramMap.put("feeAmt", new BigDecimal(0));
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        String formStr = FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);
        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notifyWithdraw(Map<String, Object> paramMap) {
        log.info("提现成功");
        String agentBillNo = (String) paramMap.get("agentBillNo");
        boolean result = transFlowService.isSaveTransFlow(agentBillNo);
        if (result){
            log.info("幂等性返回");
            return;
        }
        String bindCode = (String)paramMap.get("bindCode");
        String fetchAmt = (String)paramMap.get("fetchAmt");

        //根据用户修改账户金额
        baseMapper.updateAccount(bindCode,new BigDecimal("-"+fetchAmt),new BigDecimal(0));

        //增加交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(fetchAmt),
                TransTypeEnum.WITHDRAW,
                "提现");
        transFlowService.saveTransFlow(transFlowBO);
    }
}
