package com.itcjy.base64;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/****
 * @Author:cjy
 * @Description: com.itcjy.base64
 * @Date
 * Base64加密解密
 *****/
public class Base64Test {
    /**
     * 加密测试
     *
     * @throws Exception
     */
    @Test
    public void testEncode() throws Exception {
        byte[] encode = Base64.getEncoder().encode("ascdefg".getBytes());
        String encodeStr = new String(encode,"UTF-8");
        System.out.println("加密后的密文：" + encodeStr);
    }

    /**
     * 解密测试
     */
    @Test
    public void testDecode() throws UnsupportedEncodingException {
        String encodeStr = "YXNjZGVmZw==";
        byte[] decode = Base64.getDecoder().decode(encodeStr);
        String s = new String(decode, "utf-8");
        System.out.println(s);
    }
}
