package com.qidian.demo.sdk;

import com.alibaba.fastjson.JSONObject;
import com.qidian.demo.constant.QiDianConstant;
import com.qidian.demo.util.HttpClientUtil;

/**
 * <p>
 * 自建应用
 * <p>
 * 获取自建应用token
 * <p>
 * 文档地址 : https://api.qidian.qq.com/wiki/doc/open/e6c4dpmm53tq68e27h80
 *
 * @author Jun
 */
public class SelfBuildUtil extends CommonUtil {

    /**
     * 获取自建应用token
     *
     * @param sid    应用ID
     * @param appId  开发者appId
     * @param secret 应用secret
     * @return access_token string 自建应用token ; expires_in int 过期时间 ; errCode int
     * 返回码 ,非0表示失败,成功不会返回errCode ; errMsg string 错误信息
     * @throws Exception 异常抛出外层处理
     */
    public static JSONObject getSelfBuildToken(String sid, String appId, String secret) throws Exception {
        String result = new HttpClientUtil().sendHttpsRequest(
                QiDianConstant.SELF_BUILD_URL + "appid=" + appId + "&sid=" + sid + "&secret=" + secret, "", "get");
        return JSONObject.parseObject(result);
    }
}
