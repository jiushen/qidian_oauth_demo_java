package com.qidian.demo.sdk;

import com.alibaba.fastjson.JSONObject;
import com.qidian.demo.util.Sha1;
import com.qidian.demo.util.WxBizMsgCrypt;
import com.qidian.demo.util.XmlParse;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 通用父类
 * <p>
 * 通用加密,通用解密,解密ticket,解密event,验证url，应用供应商token
 * <p>
 * 加密解密文档: https://api.qidian.qq.com/wiki/doc/open/epko939s7aq8br19gz0i
 * <p>
 * 应用供应商文档: https://api.qidian.qq.com/wiki/doc/open/enudsepks7pq90r54frh
 *
 * @author Jun
 */
public class CommonUtil {

    /**
     * 加密
     *
     * @param encodingAesKey 消息加密解密密钥
     * @param token          令牌（Token）
     * @param timestamp      时间戳 1629093603
     * @param nonce          随机数
     * @param appId          开发者ID（AppID）
     * @param replyMsg       要加密的内容
     * @return 返回的map中 signature是签名,xmlText是加密后的xml内容(包含appId,Encrypt的xml字符串)
     * @throws Exception 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public Map<String, Object> encryptionXml(String encodingAesKey, String token, String timestamp, String nonce,
                                             String appId, String replyMsg) throws Exception {
        WxBizMsgCrypt pc = new WxBizMsgCrypt(token, encodingAesKey, appId);
        String cipherText = pc.encryptMsg(replyMsg);
        // 生成签名
        String signature = Sha1.getSha1(token, timestamp, nonce, cipherText);
        // 拼装报文
        String xmlText = XmlParse.generate(appId, cipherText);
        Map<String, Object> map = new HashMap<>(2);
        map.put("signature", signature);
        map.put("xmlText", xmlText);
        return map;
    }

    /**
     * 解密
     *
     * @param encodingAesKey 消息加密解密密钥
     * @param token          令牌（Token）
     * @param msgSignature   签名
     * @param timeStamp      时间戳
     * @param nonce          随机数
     * @param xmlTexts       包含AppId的xml字符串密文
     * @return 解密后的xml明文
     * @throws Exception 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decryptXml(String encodingAesKey, String token, String msgSignature, String timeStamp, String nonce,
                             String xmlTexts) throws Exception {
        Object[] result = XmlParse.extract(xmlTexts);
        // 拿到消息所属appId，可以区分出消息所属企业
        String appId = result[0].toString();
        // 拿到密文
        String encrypt = result[1].toString();
        WxBizMsgCrypt pc = new WxBizMsgCrypt(token, encodingAesKey, appId);
        return pc.decryptMsg(msgSignature, timeStamp, nonce, encrypt);
    }

    /**
     * 解密ticket
     *
     * @param encodingAesKey 消息加密解密密钥
     * @param token          令牌（Token）
     * @param msgSignature   签名
     * @param timeStamp      时间戳
     * @param nonce          随机数
     * @param xmlTexts       企点服务器会向开发者地址推送的包含AppId的xml字符串密文
     * @return AppId应用appid CreateTime时间戳 InfoType component_verify_ticket
     * ComponentVerifyTicket ticket内容
     * @throws Exception 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public Map<String, Object> getVerifyTicket(String encodingAesKey, String token, String msgSignature,
                                               String timeStamp, String nonce, String xmlTexts) throws Exception {
        String decryptXml = new CommonUtil().decryptXml(encodingAesKey, token, msgSignature, timeStamp, nonce,
                xmlTexts);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultTickets = (Map<String, Object>) JSONObject
                .parseObject(XmlParse.xml2json(decryptXml), Map.class).get("xml");
        return resultTickets;
    }

    /**
     * 解密event
     *
     * @param encodingAesKey 消息加密解密密钥
     * @param token          令牌（Token）
     * @param msgSignature   签名
     * @param timeStamp      时间戳
     * @param nonce          随机数
     * @param xmlTexts       企点服务器会向开发者地址推送的包含AppId的xml字符串密文
     * @return Appid MsgType ApplicationId AuthorizeTime
     * @throws Exception 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public Map<String, Object> getEvents(String encodingAesKey, String token, String msgSignature, String timeStamp,
                                         String nonce, String xmlTexts) throws Exception {
        String decryptXml = new CommonUtil().decryptXml(encodingAesKey, token, msgSignature, timeStamp, nonce,
                xmlTexts);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultEvents = (Map<String, Object>) JSONObject
                .parseObject(XmlParse.xml2json(decryptXml), Map.class).get("Msg");
        return resultEvents;
    }

    /**
     * 验证url
     *
     * @param token     令牌（Token）
     * @param signature 签名，用于验证Token令牌的正确性
     * @param timeStamp 时间戳，用于验证服务器地址的有效性
     * @param nonce     随机数，用于验证服务器地址的有效性
     * @param echoStr   回复字段，用于验证服务器地址的有效性
     * @return echoStr 返回echoStr
     * @throws Exception 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String verifyUrl(String token, String signature, String timeStamp, String nonce, String echoStr)
            throws Exception {
        String msgSignature = Sha1.getSha1(token, timeStamp, nonce, null);
        if (!signature.equals(msgSignature)) {
            throw new Exception("签名验证错误");
        }
        return echoStr;
    }
}
