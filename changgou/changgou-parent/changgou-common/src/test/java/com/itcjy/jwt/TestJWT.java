package com.itcjy.jwt;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itcjy.com
 * @version 1.0
 * @package com.changgou.test *
 * @since 1.0
 */
public class TestJWT {

    /**
     * 创建令牌
     */
    @Test
    public void createJwt(){
        long currentTimeMillis = System.currentTimeMillis();
        long l = currentTimeMillis + 30000;
        //生成令牌     每次生成都不一样
        //创建jwtbuilder
        JwtBuilder builder = Jwts.builder();
        //1.头(不设置,默认) 2 载荷(数据) 3. 签名
        builder.setId("唯一的标识")
                .setIssuer("颁发者")//颁发者，生成令牌的一方
                .setSubject("主题")//主题信息就是描述 jwt的信息
                .setExpiration(new Date(l))//设置过期时间
                .signWith(SignatureAlgorithm.HS256,"itcjy");//设置签名   参数1签名算法，2密钥(盐)

        //测试成功
        /*String token = builder.compact();
        System.out.println("令牌信息密文："+token);*/




        //3.可以自定义载荷
        Map<String, Object> map = new HashMap<>();
        map.put("myaddress","cn");
        map.put("mycity","sz");
        builder.addClaims(map);


        //生成令牌
        String compact = builder.compact();
        System.out.println(compact);

    }

    @Test
    public void parseJwt(){
        //给compactJwt赋值上面生成的令牌对象
        String compactJwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiLllK_kuIDnmoTmoIfor4YiLCJpc3MiOiLpooHlj5HogIUiLCJzdWIiOiLkuLvpopgiLCJleHAiOjE2NjU5MDI4OTcsIm15Y2l0eSI6InN6IiwibXlhZGRyZXNzIjoiY24ifQ.YQWuS23KUmLyb51zLcu4-TW_L15LlELn5NctxOJWIzo";






        //测试成功
       /* Claims claims = Jwts.parser().
                setSigningKey("itcjy").
                parseClaimsJws(compactJwt).
                getBody();
        System.out.println(claims);*/


        //解析令牌
        Jws<Claims> itcjy = Jwts.parser()
                .setSigningKey("itcjy")  //密钥(盐)
                .parseClaimsJws(compactJwt);
        System.out.println(itcjy.getBody());  //.getBody()获取解析后的数据



    }
}
