package com.qidian.demo.constant;

/**
 * 静态常量类
 *
 * @author Jun
 */
public class QiDianConstant {

    /**
     * 应用开发商token
     */
    public static final String COMPONENT_ACCESS_TOKEN_URL = "https://api.qidian.qq.com/cgi-bin/component/api_component_token";

    /**
     * 自建应用 token获取
     */
    public static final String SELF_BUILD_URL = "https://api.qidian.qq.com/cgi-bin/token/getSelfBuildToken?";
    /**
     * 第三方个人应用 code换取token
     */
    public static final String AUTHORIZER_PERSONAL_ACCESS_TOKEN_URL = "https://api.qidian.qq.com/cgi-bin/component/oauth_personal_app_token?component_access_token=";
    /**
     * 第三方个人应用 token刷新
     */
    public static final String REFRESH_AUTHORIZER_PERSONAL_ACCESS_TOKEN_URL = "https://api.qidian.qq.com/cgi-bin/component/api_personal_authorizer_token?component_access_token=";
    /**
     * 第三方企点应用 code获取token
     */
    public static final String AUTHORIZER_ACCESS_TOKEN_URL = "https://api.qidian.qq.com/cgi-bin/component/oauth_app_token?component_access_token=";
    /**
     * 第三方企点应用 token刷新
     */
    public static final String REFRESH_AUTHORIZER_ACCESS_TOKEN_URL = "https://api.qidian.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=";

}
