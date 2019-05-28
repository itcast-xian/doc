package cn.itcast.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.jsonwebtoken.JwtBuilder;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import sun.misc.BASE64Decoder;
import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;




public class JwtUtil {

    //创建jwt令牌  -------> 私钥
    public static String createJwt(Map bodyMap) throws Exception {
        //密钥库文件
        String keystore = "itcast.keystore";
        //密钥库的密码
        String keystore_password = "itcast";
        //密钥库文件路径
        ClassPathResource classPathResource = new ClassPathResource(keystore);
        //密钥别名
        String alias  = "itcastkey";
        //密钥的访问密码
        String key_password = "itcast";




        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource,keystore_password.toCharArray());
        //密钥对（公钥和私钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, key_password.toCharArray());
        //获取私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();


        //jwt令牌的内容
        String bodyString = new ObjectMapper().writeValueAsString(bodyMap); // 自定义的内容


        //生成jwt令牌
        org.springframework.security.jwt.Jwt jwt = JwtHelper.encode(bodyString, new RsaSigner(aPrivate));


        //生成jwt令牌编码
        String jwtToken = jwt.getEncoded();

        return jwtToken;
    }








    //校验jwt令牌
    public static String verify(String jwtString){
        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw9MbPMkfRgaLTqGRUhxufW/nhiRs4bMAmah2K1Inv+IanqsOn3gYITS99BNjpRRdA2NKQeoqkW7FO0oeI60K+5DhYJwU/PnV8dA5BT7C3S+KAfk8IQqv/o4R0dEY5Dn65DAlHF6gpB14bJ42uA5XZGffD08BNMjjjfYBzG8WT2z6TlNeEijhOn70KoCsU5NbAIK1JXEZ7KytWZts/tao3vGekc/HLzDvPNprh+3BSyyzJ/7nihLr1PLdeSFH8dwdMXappCjdySEQQEqGDS0B+RReeR1nVm3/Gzd+5okh0iRt6wD/+IbBMsK1fzpDzFf+8GQyxWx/pKC3Z7m578c9PwIDAQAB-----END PUBLIC KEY-----";

        //校验jwt令牌
        Jwt jwt = JwtHelper.decodeAndVerify(jwtString, new RsaVerifier(publickey));

        //拿到jwt令牌中自定义的内容
        String claims = jwt.getClaims();

        return claims;
    }





    public static void main(String[] args) throws Exception {
//        Map map = new HashMap();
//        map.put("name","itcast");
//        map.put("gender","1");
//        map.put("age","13");
//        String jwt = createJwt(map);
//        System.out.println(jwt);


        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJnZW5kZXIiOiIxIiwibmFtZSI6Iml0Y2FzdCIsImFnZSI6IjEzIn0.s7wfnoZUSsN8zoV90Bh_ZSqkpDzcM3Ysnlw8OH9NVvLdem8TcnS8JdQ_v8yJpzPDSMXSjnkzAOe1cYdVx6eu2UhEqJsjNSukfQMiAYqBi2ejLEsBm59iFbMZ6Ha0N00i0SUXugPp1sunv3p0sRUc6460cg7gTIm5JIz0SXKT4mAzOMoPVuX_3XtyGohvDcVverdI_L8XfqidW-uTfO5LTa_SZ9zbIU2GX7TMOnbI46-kq3jf9-So1h6tDUMyS270wcNVu2b4FuFhxYBrJfhBvHHNPkq03L14DeSaBfg4uDCF03VVAnRc8feF1PN5lMr6YhHRmzrRTZ5NiBDa58ojPw";
        String verify = verify(token);
        System.out.println(verify);

    }

}
