package jwt_practice.springjwt.jwt;


import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//JWT 발급 및 토큰 내 정보 전달, 검증(isExpired)을 수행하는 클래스
@Component
public class JWTUtil {

    private SecretKey secretKey; //JWT를 생성(서명, Signature)하고 검증하는데 필요한 암호화 키

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        //HS256 : JWT 토큰 암호화 알고리즘 , 단방향 대칭키 ( secret 키를 통해 암호화를 진행하고 검증을 하기 때문에 대칭키의 의미를 가짐 )
        //secret을 통해 암호화 키인 secretKey 초기화
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

    }

    //token(JWT)를 파싱하여 JWT 내부에 존재하는 username을 꺼내오는 메서드
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    //token(JWT)를 파싱하여 JWT 내부에 존재하는 role을 꺼내오는 메서드
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    //token(JWT)를 파싱하여 JWT이 만료되었는지 확인하는 메서드 -> 토큰의 만료 날짜를 현재 날짜와 비교한다.
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    //token(JWT) 생성 메서드
    public String createJWT(String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) //토큰이 발급된 시간 설정
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) //토큰의 만료 시간을 설정
                .signWith(secretKey) //secretKey를 사용하여 토큰에 서명한다.
                .compact(); //URL에서 안전하게 사용될 수 있도록 인코딩된 문자열 형식의 토큰으로 만듦
    }
}
