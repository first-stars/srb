package com.w.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.w.common.exception.Assert;
import com.w.common.result.ResponseEnum;
import com.w.srb.core.enums.UserBindEnum;
import com.w.srb.core.huf.FormHelper;
import com.w.srb.core.huf.HfbConst;
import com.w.srb.core.huf.RequestHelper;
import com.w.srb.core.mapper.UserInfoMapper;
import com.w.srb.core.pojo.entity.UserBind;
import com.w.srb.core.mapper.UserBindMapper;
import com.w.srb.core.pojo.entity.UserInfo;
import com.w.srb.core.pojo.vo.UserBindVO;
import com.w.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author xin
 * @since 2022-10-06
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {


    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {
        //查询身份证是否绑定
        QueryWrapper<UserBind> userBindVOQueryWrapper = new QueryWrapper<>();
        userBindVOQueryWrapper.eq("id_card",userBindVO.getIdCard())
                .eq("user_id",userId);
        UserBind userBind = baseMapper.selectOne(userBindVOQueryWrapper);
        //USER_BIND_IDCARD_EXIST_ERROR(-301, "身份证号码已绑定"),
        Assert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        //查询用户绑定信息
        userBindVOQueryWrapper = new QueryWrapper<>();
        userBindVOQueryWrapper.eq("user_id",userId);
        userBind = baseMapper.selectOne(userBindVOQueryWrapper);

        //判断是否有绑定记录

        if (userBind == null){
            //如果未创建绑定记录，则创建一条绑定记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO,userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
            try {
                System.out.println("********************");
                System.out.println(userBind.getId());
            } catch (Exception e) {
                e.printStackTrace();

                System.out.println("********************");
            }

        }
        //组装自动提交表单的参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard",userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

//构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);
        return formStr;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> map) {
        String bindCode = (String) map.get("bindCode");
        String agentUserId = (String) map.get("agentUserId");
        //根据user_id查询user_bind记录
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",agentUserId);

        //更新用户绑定表
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        userBind.setBindCode(bindCode);
        baseMapper.updateById(userBind);

        //更新用户表
        UserInfo userInfo = userInfoMapper.selectById(agentUserId);
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 获取bindCode
     * @param userId
     * @return
     */
    @Override
    public String getBindCodeByUserId(Long userId) {
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        String bindCode = userBind.getBindCode();
        return bindCode;
    }
}
