package com.qidian.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@Slf4j
public class WpaQidianService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ThirdQidianService thirdQidianService;

    @Value("${wpa.qidian.users}")
    private String wpaUsers;

    /**
     * 获取分配的接待人ID
     *
     * @return
     */
    public JSONObject getWpaOpenId() {
        String[] userArr = wpaUsers.split(",");
        return staffIdByAccount(reBalance(userArr));
    }

    /**
     * 选择负责接待的用户--负载算法（目前只展示随机）
     *
     * @param userArr
     * @return
     */
    public String reBalance(String[] userArr) {
        int len = userArr.length;
        return userArr[new Random().nextInt(len - 1)];
    }

    /**
     * 根据账号名获取openId
     * <p>
     * 接口地址：https://api.qidian.qq.com/wiki/doc/open/e1lvod8ftkgs44hupuu3
     *
     * @param userName
     * @return
     */
    public JSONObject staffIdByAccount(String userName) {
        JSONObject body = new JSONObject();
        String[] arr = new String[]{userName};
        body.put("accounts", arr);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(body);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                    "https://api.qidian.qq.com/cgi-bin/v1/org/basic/StaffIdByAccountBatch?access_token=" + thirdQidianService.getSelfBuildToken(),
                    HttpMethod.POST, httpEntity, JSONObject.class);
            if (200 == responseEntity.getStatusCodeValue()) {
                JSONObject response = responseEntity.getBody();
                if ("0".equalsIgnoreCase(response.getString("errcode"))) {
                    JSONObject data = response.getJSONObject("data");
                    if (!ObjectUtils.isEmpty(data)) {
                        JSONArray info = data.getJSONArray("info");
                        if (!ObjectUtils.isEmpty(info)) {
                            Object o = info.get(0);
                            return JSONObject.parseObject(JSON.toJSONString(o));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取接待分组信息
     * <p>
     * 接口地址：https://api.qidian.qq.com/wiki/doc/open/elazs9clex4rin914kz4
     *
     * @param offset
     * @param count
     * @return
     */
    public JSONObject getImGroupId(int offset, int count) {
        JSONObject body = new JSONObject();
        body.put("offset", offset);
        body.put("count", count);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(body);

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                    "https://api.qidian.qq.com/cgi-bin/imaccessset/batchget_imaccessset?access_token=" + thirdQidianService.getSelfBuildToken(),
                    HttpMethod.POST, httpEntity, JSONObject.class);
            if (200 == responseEntity.getStatusCodeValue()) {
                JSONObject response = responseEntity.getBody();
                if ("0".equalsIgnoreCase(response.getString("errcode"))) {
                    JSONArray item = response.getJSONArray("item");
                    int totalCount = response.getInteger("total_count");
                    if (totalCount > 0) {
                        Object o = item.get(new Random().nextInt(totalCount));
                        return JSONObject.parseObject(JSON.toJSONString(o));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
