package com.qidian.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.qidian.demo.sdk.SelfBuildUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThirdQidianService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${third.qidian.url}")
    private String qidianUrl;

    @Value("${third.qidian.appid}")
    private String appid;

    @Value("${third.qidian.sid}")
    private String sid;

    @Value("${third.qidian.appSecret}")
    private String appSecret;

    /**
     * 独立开发者鉴权获取token
     * 接口地址：https://api.qidian.qq.com/wiki/doc/open/ecx0pn95gkss43023h2i
     *
     * @return
     */
    public String getAccessToken() {
        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(qidianUrl
                    + "/cgi-bin/token?grant_type=client_credential&appid=" + appid
                    + "&secret=" + appSecret, JSONObject.class);
            if (200 == responseEntity.getStatusCodeValue()) {
                JSONObject response = responseEntity.getBody();
                return response.getString("access_token");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取第三方自建应用token
     *
     * @return
     */
    public String getSelfBuildToken() {
        try {
            JSONObject response = SelfBuildUtil.getSelfBuildToken(sid, appid, appSecret);
            return response.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 根据社交账号获取cust_id
     * <p>
     * 接口地址：https://api.qidian.qq.com/wiki/doc/open/eal6s336lsl1czg5h569
     *
     * @param cid
     * @param type
     * @return
     */
    public String getCuinBySocialAccount(String cid, String type) {
        JSONObject body = new JSONObject();
        body.put("cid", cid);
        body.put("type", type);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(body);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                    qidianUrl + "/cgi-bin/cust/cust_info/getCuinBySocialAccount?access_token=" + getSelfBuildToken(),
                    HttpMethod.POST, httpEntity, JSONObject.class);
            if (200 == responseEntity.getStatusCodeValue()) {
                JSONObject response = responseEntity.getBody();
                if ("0".equalsIgnoreCase(response.getString("r"))) {
                    JSONObject data = response.getJSONObject("data");
                    return data.getString("cust_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    /**
     * 根据custid 获取第三方openid
     *
     * @param custId
     * @return
     */
    public String getOpenIdByCustId(String custId) {
        JSONObject customerInfo = getSingCustBaseInfo(custId);
        return ObjectUtils.isEmpty(customerInfo) ? "" : customerInfo.getJSONObject("identity").getString("third_party_id");
    }

    /**
     * 拉取客户基本信息
     *
     * @param custId
     * @return
     */
    public JSONObject getSingCustBaseInfo(String custId) {
        JSONObject body = new JSONObject();
        body.put("cust_id", custId);
        List<String> fields = new ArrayList<>();
        fields.add("identity");
        fields.add("contact");
        fields.add("socialAccount");
        fields.add("controlInfo");
        fields.add("udfInfo");
        body.put("data", fields);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(body);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                    qidianUrl + "/cgi-bin/cust/cust_info/getSingCustBaseInfo?access_token=" + getSelfBuildToken(),
                    HttpMethod.POST, httpEntity, JSONObject.class);
            if (200 == responseEntity.getStatusCodeValue()) {
                return responseEntity.getBody().getJSONObject("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 发送webIM消息
     * 接口地址：https://api.qidian.qq.com/wiki/doc/open/em0ak81sklckg912oh1m
     *
     * @param params
     */
    public void sendToC(JSONObject params) {
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(params);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(qidianUrl + "/cgi-bin/message/webim/sendToC?access_token="
                            + getSelfBuildToken(),
                    HttpMethod.POST, httpEntity, JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
