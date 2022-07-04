package com.qidian.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ThirdDemoService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${third.demo.url}")
    private String demoUrl;

    @Value("${third.demo.admin.username}")
    private String username;

    @Value("${third.demo.admin.password}")
    private String password;

    @Value("${third.demo.applySecret}")
    private String applySecret;

    /**
     * 获取demo电商系统token信息
     *
     * @param username
     * @param password
     * @return
     */
    public String getBearerTokenForClient(String username, String password) {
        JSONObject body = new JSONObject();
        body.put("cellphone", username);
        body.put("password", password);
        body.put("remember", false);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("apply-secret", applySecret);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(body, requestHeaders);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(demoUrl + "/api/v1/app/login",
                    HttpMethod.POST, httpEntity, JSONObject.class);
            JSONObject response = responseEntity.getBody();
            if ("ok".equalsIgnoreCase(response.getString("result"))) {
                JSONObject message = response.getJSONObject("message");
                return message.getString("token_type") + " " + message.getString("access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    /**
     * 获取管理员token
     *
     * @return
     */
    public String getBearerTokenForAdmin() {
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        body.put("remember", false);
        body.put("type", 1);
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(body, requestHeaders);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(demoUrl + "/api/v1/admin/login",
                    HttpMethod.POST, httpEntity, JSONObject.class);
            JSONObject response = responseEntity.getBody();
            if ("ok".equalsIgnoreCase(response.getString("result"))) {
                JSONObject message = response.getJSONObject("message");
                return message.getString("token_type") + " " + message.getString("access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }


    /**
     * 获取用户信息
     *
     * @param uid
     * @return
     */
    public JSONObject getUserInfo(String uid) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", getBearerTokenForAdmin());
        HttpEntity httpEntity = new HttpEntity(requestHeaders);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(demoUrl + "/api/v1/admin/member/findMemberByUserId?uid=" + uid,
                    HttpMethod.GET, httpEntity, JSONObject.class);
            JSONObject response = responseEntity.getBody();
            if ("ok".equalsIgnoreCase(response.getString("result"))) {
                JSONObject message = response.getJSONObject("message");
                JSONObject orderList = getOrderList(1000, 1, 0, "-created_at", uid, "");
                message.put("buy_count", getOrderCount(orderList));
                message.put("buy_last_time", getLastOrderTime(orderList));
                message.put("avr_order_price", getAvrOrderPrice(orderList));
                message.put("coupon_count", 0);
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取用户信息
     *
     * @param openId
     * @return
     */
    public int getUserUidByOpenId(String openId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", getBearerTokenForAdmin());
        HttpEntity httpEntity = new HttpEntity(requestHeaders);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(demoUrl + "/api/v1/admin/member/findMemberByUserId?uid=" + openId,
                    HttpMethod.GET, httpEntity, JSONObject.class);
            JSONObject response = responseEntity.getBody();
            if ("ok".equalsIgnoreCase(response.getString("result"))) {
                JSONObject message = response.getJSONObject("message");
                if (!ObjectUtils.isEmpty(message)) {
                    JSONArray data = message.getJSONArray("data");
                    if (!ObjectUtils.isEmpty(data)) {
                        JSONObject user = data.getObject(0, JSONObject.class);
                        return user.getInteger("id");
                    }
                }
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    /**
     * 获取商品列表
     *
     * @param limit
     * @param page
     * @param index
     * @param sort
     * @param goodsId
     * @return
     */
    public JSONObject getGoodsList(Integer limit, Integer page, Integer index, String sort, String goodsId, String type, String keywords) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", getBearerTokenForAdmin());
        HttpEntity httpEntity = new HttpEntity(requestHeaders);
        try {
            StringBuffer url = new StringBuffer();

            if ("recommend".equalsIgnoreCase(type)) {
                url = url.append(demoUrl + "/api/v1/admin/good?limit="
                        + limit + "&page=" + page + "&activeIndex=" + index + "&sort=" + sort + "&recommend=1");
            } else if ("history".equalsIgnoreCase(type)) {
                url = url.append(demoUrl + "/api/v1/admin/good?limit="
                        + limit + "&page=" + page + "&activeIndex=" + 3 + "&sort=" + sort);
            } else {
                url = url.append(demoUrl + "/api/v1/admin/good?limit="
                        + limit + "&page=" + page + "&activeIndex=" + index + "&sort=" + sort);
            }

            if (!StringUtils.isEmpty(goodsId)) {
                url = url.append("&goodsId=" + goodsId);
            }
            if (!StringUtils.isEmpty(keywords)) {
                url = url.append("&title=" + keywords);
            }
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url.toString(),
                    HttpMethod.GET, httpEntity, JSONObject.class);
            JSONObject response = responseEntity.getBody();
            if ("ok".equalsIgnoreCase(response.getString("result"))) {
                return response.getJSONObject("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取订单列表
     *
     * @param limit
     * @param page
     * @param index
     * @param sort
     * @param openId
     * @param keywords
     * @return
     */
    public JSONObject getOrderList(Integer limit, Integer page, Integer index, String sort, String openId, String keywords) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", getBearerTokenForAdmin());
        HttpEntity httpEntity = new HttpEntity(requestHeaders);
        try {

            StringBuffer url = new StringBuffer(demoUrl + "/api/v1/admin/indent/listByUid?limit="
                    + limit + "&page=" + page + "&activeIndex=" + index + "&sort=" + sort + "&uid=" + getUserUidByOpenId(openId));

            if (!StringUtils.isEmpty(keywords)) {
                url = url.append("&title=" + keywords);
            }

            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url.toString(),
                    HttpMethod.GET, httpEntity, JSONObject.class);
            JSONObject response = responseEntity.getBody();
            if ("ok".equalsIgnoreCase(response.getString("result"))) {
                return response.getJSONObject("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public int getOrderCount(JSONObject jsonObject) {
        if (ObjectUtils.isEmpty(jsonObject)) {
            return 0;
        }
        return jsonObject.getInteger("total");
    }

    public String getLastOrderTime(JSONObject jsonObject) {
        if (ObjectUtils.isEmpty(jsonObject)) {
            return "";
        }
        JSONArray data = jsonObject.getJSONArray("data");
        DateTime min = null;
        if (!ObjectUtils.isEmpty(data)) {
            for (Object o : data) {
                JSONObject value = JSON.parseObject(JSON.toJSONString(o));
                String createTime = value.getString("created_time");
                if (StringUtils.isEmpty(min)) {
                    min = new DateTime(createTime);
                } else if (min.isBefore(new DateTime(createTime))) {
                    min = new DateTime(createTime);
                }
            }
        }
        return min == null ? "" : min.toString("yyyy-MM-dd hh:mm:ss");
    }

    public double getAvrOrderPrice(JSONObject jsonObject) {
        if (ObjectUtils.isEmpty(jsonObject)) {
            return 0;
        }
        JSONArray data = jsonObject.getJSONArray("data");
        double sum = 0;
        if (!ObjectUtils.isEmpty(data)) {
            for (Object o : data) {
                JSONObject value = JSON.parseObject(JSON.toJSONString(o));
                double total = value.getDouble("total");
                sum += total;
            }
        } else {
            return 0;
        }
        return sum / data.size();
    }
}
