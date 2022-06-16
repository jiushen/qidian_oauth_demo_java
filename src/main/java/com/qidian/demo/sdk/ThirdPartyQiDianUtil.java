package com.qidian.demo.sdk;

import com.alibaba.fastjson.JSONObject;
import com.qidian.demo.constant.QiDianConstant;
import com.qidian.demo.util.HttpClientUtil;

/**
 * <p>
 * 第三方企点应用
 * <p>
 * 应用授权token,应用授权刷新token
 * <p>
 * 文档地址: https://api.qidian.qq.com/wiki/doc/open/enudsepks7pq90r54frh
 *
 * @author Jun
 */
public class ThirdPartyQiDianUtil extends CommonUtil {

    /**
     * 获取应用开发商token
     *
     * @param componentAppId        应用开发商appId
     * @param componentAppSecret    应用开发商appSecret
     * @param componentVerifyTicket 企点API后台推送的ticket，此ticket会定时推送
     * @return component_access_token 应用开发商token expires_in 有效时长(s)
     * @throws Exception 异常抛出外层处理
     */
    public JSONObject getComponentAccessToken(String componentAppId, String componentAppSecret,
                                              String componentVerifyTicket) throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("component_appid", componentAppId);
        jsonParam.put("component_appsecret", componentAppSecret);
        jsonParam.put("component_verify_ticket", componentVerifyTicket);
        String result = new HttpClientUtil().sendHttpsRequest(QiDianConstant.COMPONENT_ACCESS_TOKEN_URL,
                jsonParam.toJSONString(), "post");
        return JSONObject.parseObject(result);
    }

    /**
     * 使用应用授权code换取应用授权token
     *
     * @param componentAppId       应用开发商appId
     * @param authorizationCode    应用授权code,会在授权成功时返回给应用开发者
     * @param componentAccessToken 应用开发商token
     * @return <p style='font-weight: bold'>
     * authorizer_appId 应用授权方 appId <BR>
     * <p style='font-weight: bold'>
     * authorizer_access_token 应用授权方的接口调用凭证（应用授权token） (长度最长为196) <BR>
     * <p style='font-weight: bold'>
     * expires_in 过期时间，单位秒（s） <BR>
     * <p style='font-weight: bold'>
     * authorizer_refresh_token
     * 授权方的刷新令牌（应用授权刷新token),该令牌的有效期跟随应用授权关系，如果应用授权关系不终止则永久有效 (长度最长为128)
     * <BR>
     * <p style='font-weight: bold'>
     * applicationId 应用id
     * @throws Exception 异常抛出外层处理
     */
    public JSONObject getAuthorizerAccessToken(String componentAppId, String authorizationCode,
                                               String componentAccessToken) throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("component_appid", componentAppId);
        jsonParam.put("authorization_code", authorizationCode);
        String result = new HttpClientUtil().sendHttpsRequest(
                QiDianConstant.AUTHORIZER_ACCESS_TOKEN_URL + componentAccessToken, jsonParam.toJSONString(), "post");
        return JSONObject.parseObject(result);

    }

    /**
     * 应用授权刷新token
     *
     * @param componentAppId         应用开发商appId
     * @param authorizerAppId        应用授权方appId（应用授权code换取应用授权token中返回）
     * @param authorizerRefreshToken 授权方的刷新令牌（应用授权code换取应用授权token中返回）
     *                               该令牌的有效期跟随应用授权关系，如果应用授权关系不终止则永久有效 (长度最长为128),应用没有被卸载将保持不变
     * @param applicationId          应用id（应用授权code换取应用授权token中返回）
     * @param componentAccessToken   应用开发商token
     * @return <p style='font-weight: bold'>
     * authorizer_access_token 授权方的接口调用凭证（应用授权token） <BR>
     * <p style='font-weight: bold'>
     * expires_in 过期时间，单位秒（s） <BR>
     * <p style='font-weight: bold'>
     * authorizer_refresh_token
     * 授权方的刷新令牌（应用授权刷新token），刷新令牌主要用于公众号第三方平台获取和刷新已授权用户的access_token，只会在授权时刻提供，请妥善保存。一旦丢失，只能让用户重新授权，才能再次拿到新的刷新令牌<BR>
     * @throws Exception 异常抛出外层处理
     */
    public JSONObject getNewAuthorizerAccessToken(String componentAppId, String authorizerAppId,
                                                  String authorizerRefreshToken, String applicationId, String componentAccessToken) throws Exception {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("component_appid", componentAppId);
        jsonParam.put("authorizer_appid", authorizerAppId);
        jsonParam.put("authorizer_refresh_token", authorizerRefreshToken);
        jsonParam.put("sid", applicationId);
        String result = new HttpClientUtil().sendHttpsRequest(
                QiDianConstant.REFRESH_AUTHORIZER_ACCESS_TOKEN_URL + componentAccessToken, jsonParam.toJSONString(),
                "post");
        return JSONObject.parseObject(result);

    }

}
