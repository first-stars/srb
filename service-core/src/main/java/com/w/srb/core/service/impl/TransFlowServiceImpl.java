package com.w.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.w.srb.core.enums.TransTypeEnum;
import com.w.srb.core.mapper.UserInfoMapper;
import com.w.srb.core.pojo.bo.TransFlowBO;
import com.w.srb.core.pojo.entity.TransFlow;
import com.w.srb.core.mapper.TransFlowMapper;
import com.w.srb.core.pojo.entity.UserInfo;
import com.w.srb.core.service.TransFlowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private TransFlowMapper transFlowMapper;

    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {
        //获取用户基本信息 user_info
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("bind_code",transFlowBO.getBindCode());
        UserInfo userInfo = userInfoMapper.selectOne(userInfoQueryWrapper);
        //存储交易流水数据
        TransFlow transFlow = new TransFlow();
        transFlow.setUserId(userInfo.getId());
        transFlow.setUserName(userInfo.getName());
        transFlow.setTransNo(transFlowBO.getAgentBillNo());
        transFlow.setTransType(transFlowBO.getTransTypeEnum().getTransType());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setMemo(transFlowBO.getMemo());
        baseMapper.insert(transFlow);

    }

    @Override
    public boolean isSaveTransFlow(String agentBillNo) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("trans_no",agentBillNo);
        Integer count = baseMapper.selectCount(transFlowQueryWrapper);
        return count>0;
    }

    @Override
    public List<TransFlow> selectByUserId(Long userId) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("user_id",userId);
        List<TransFlow> transFlowList = transFlowMapper.selectList(transFlowQueryWrapper);
        return transFlowList;
    }
}
