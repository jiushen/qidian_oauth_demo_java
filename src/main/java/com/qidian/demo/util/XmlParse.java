/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 *
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.qidian.demo.util;

import com.alibaba.fastjson.JSONObject;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * XMLParse class
 * <p>
 * 提供提取消息格式中的密文及生成回复消息格式的接口.
 * @author Jun
 */
public class XmlParse {

    /**
     * 提取出xml数据包中的加密消息
     *
     * @param xmlText 待提取的xml字符串
     * @return 提取出的加密消息字符串
     * @throws AesException xml解析失败
     */
    public static Object[] extract(String xmlText) throws AesException {
        Object[] result = new Object[3];
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xmlText);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);
            Element root = document.getDocumentElement();
            NodeList nodeList1 = root.getElementsByTagName("AppId");
            NodeList nodeList2 = root.getElementsByTagName("Encrypt");
            result[0] = nodeList1.item(0).getTextContent();
            result[1] = nodeList2.item(0).getTextContent();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.PARSE_XML_ERROR);
        }
    }


    /**
     * 生成xml消息
     *
     * @param appId   企业appId
     * @param encrypt 加密后的消息密文
     * @return 生成的xml字符串
     */
    public static String generate(String appId, String encrypt) {
        String format = "<xml>\n" + "<AppId><![CDATA[%1$s]]></AppId>\n" + "<Encrypt><![CDATA[%2$s]]></Encrypt>\n" + "</xml>";
        return String.format(format, appId, encrypt);
    }

    /**
     *
     * @param xmlString  字符串
     * @return 返回JSONObject
     * @throws Exception  抛出外层处理
     */
    public static String xml2json(String xmlString) throws Exception {
        JSONObject obj = new JSONObject();
        InputStream is = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
        SAXBuilder sb = new SAXBuilder();
        org.jdom.Document doc = sb.build(is);
        org.jdom.Element root = doc.getRootElement();
        obj.put(root.getName(), iterateElement(root));
        return obj.toString();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map iterateElement(org.jdom.Element element) {
        List node = element.getChildren();
        org.jdom.Element et;
        Map obj = new HashMap();
        List list;
        for (Object o : node) {
            list = new LinkedList();
            et = (org.jdom.Element) o;
            if ("".equals(et.getTextTrim())) {
                if (et.getChildren().size() == 0){
                    continue;}
                if (obj.containsKey(et.getName())) {
                    list = (List) obj.get(et.getName());
                }
                list.add(iterateElement(et));
                obj.put(et.getName(), list);
            } else {

                obj.put(et.getName(), et.getValue());
            }
        }
        return obj;
    }
}
