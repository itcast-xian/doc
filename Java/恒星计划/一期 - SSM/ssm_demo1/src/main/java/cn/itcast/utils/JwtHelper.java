package cn.itcast.utils;

import com.google.common.collect.Maps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class JwtHelper {

    private static final String key = "458c9f902d0f4d94b12b4bd55a7816c7" ;

    public static void main(String[] args) {
        Map paramMap = new HashMap();
        paramMap.put("name","itcast");
        paramMap.put("age","13");
        paramMap.put("gender","1");
        System.out.println(createJwt(paramMap));;

    }

    public static String createJwt(Map paramMap) {
        String compact = Jwts.builder().setClaims(paramMap).signWith(SignatureAlgorithm.HS256, key).compact();
        return compact;
    }

    public static Claims parseJwt(String jwt) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
        Claims body = claimsJws.getBody();
        return body ;
    }

}
