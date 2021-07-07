package com.chong.user.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.chong.hospital.common.exception.UserException;
import com.chong.hospital.common.result.Result;
import com.chong.hospital.common.result.ResultCodeEnum;
import com.chong.hospital.common.util.JwtHelper;
import com.chong.hospital.model.user.UserInfo;
import com.chong.user.service.UserInfoService;
import com.chong.user.utils.ConstantPropertiesUtil;
import com.chong.user.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: chong
 * @Data: 2021/7/6 11:05 下午
 */
@Controller
@Slf4j
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisTemplate redisTemplate;


    // 获取微信登录参数
    @GetMapping("/getLoginParam")
    @ResponseBody
    public Result getQrConnect(HttpSession session) throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("redirectUri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis() + "");
        return Result.ok(map);
    }


    // 微信登录回调
    @RequestMapping("/callback")
    public String callback(String code, String state){
        log.info("微信授权服务器回调......");
        log.info("state={}", state);
        log.info("code={}", code);

        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(code)){
            log.error("非法回调请求！！！！");
            throw new UserException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        //使用code和appid以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);

        String result = null;

        try {
            result = HttpClientUtils.get(accessTokenUrl);
        } catch (Exception e){
            throw new UserException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        log.info("使用code换取调access_token结果：{}", result);

        JSONObject resultJson = JSONObject.parseObject(result);

        if (resultJson.getString("errcode") != null){
            log.error("获取access_token失败：{}{}", resultJson.getString("errcode"), resultJson.getString("errmsg"));
            throw new UserException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        String accessToken = resultJson.getString("access_token");
        String openId = resultJson.getString("openid");
        log.info("accessToken:{}", accessToken);
        log.info("openId:{}", openId);

        //根据access_token获取微信用户的基本信息
        //先根据openid进行数据库查询
        UserInfo userInfo = userInfoService.getByOpenid(openId);


        // 如果没有查到用户信息,那么调用微信个人信息获取的接口
        if (userInfo == null) {
            // if(null == userInfo){

            //使用access_token换取受保护的资源：微信的个人信息

            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";

            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openId);
            String resultUserInfo = null;

            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
            } catch (Exception e) {
                throw new UserException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            log.info("使用access_token获取用户信息的结果：{}", resultUserInfo);

            JSONObject resultUserInfoJson = JSONObject.parseObject(resultUserInfo);

            if (resultUserInfoJson.getString("errcode") != null) {
                log.error("获取用户信息失败！！！:{}{}", resultUserInfoJson.getString("errcode"), resultUserInfoJson.getString("errmsg"));
                throw new UserException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            // 解析用户信息
            String nickname = resultUserInfoJson.getString("nickname");
            String headimgurl = resultUserInfoJson.getString("headimgurl");

            userInfo = new UserInfo();
            userInfo.setNickName(nickname);
            userInfo.setOpenid(openId);
            userInfo.setStatus(1);
            userInfoService.save(userInfo);
        }

        //如果查询到个人信息，那么直接进行登录
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name))
            name = userInfo.getNickName();

        if (StringUtils.isEmpty(name))
            name = userInfo.getPhone();
        map.put("name", name);

        // 判断userinfo是否有手机号，如果手机号为空，返回openid
        // 如果不为空，返回openid为空字符串
        // 在前端进行校验，如果openid不为空，绑定手机号，如果为空不需要绑定
        if (StringUtils.isEmpty(userInfo.getPhone()))
            map.put("openid", userInfo.getOpenid());
        else
            map.put("openid", "");

        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);

        try {
            String returnUrl = "redirect:"+ ConstantPropertiesUtil.YYGH_BASE_URL +
                    "/weixin/callback?token="+map.get("token")+"&openid="+
                    map.get("openid")+"&name=" + URLEncoder.encode((String) map.get("name"), "utf-8");

            log.info("{}", returnUrl);
            return returnUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }


    }
}
