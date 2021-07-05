package com.chong.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.chong.msm.MainApplicationMsm;
import com.chong.msm.service.MsmService;
import com.chong.msm.utils.ConstantPropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/5 11:38 下午
 */
@Slf4j
@Service
public class MsmServiceImpl implements MsmService {
    @Override
    public boolean send(String phone, String code) {
        // 判断手机号是否为空
        if (StringUtils.isEmpty(phone))
            return false;

        // 整合阿里云短信服务，设置相关参数
        DefaultProfile profile = DefaultProfile.getProfile(ConstantPropertiesUtils.REGION_ID,
                ConstantPropertiesUtils.ACCESS_KEY_ID,
                ConstantPropertiesUtils.SECRET);

        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        // 手机号
        request.putQueryParameter("PhoneNumbers", phone);
        // 签名名称
        request.putQueryParameter("SignName", "我的在线医院挂号网站");
        // 模版code
        request.putQueryParameter("TemplateCode", "SMS_218891563");
        // 验证码使用json格式
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));

        // 短信发送
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }
}
