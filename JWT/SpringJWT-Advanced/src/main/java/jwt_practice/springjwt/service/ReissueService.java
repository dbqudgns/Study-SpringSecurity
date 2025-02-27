package jwt_practice.springjwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jwt_practice.springjwt.entity.RefreshEntity;
import jwt_practice.springjwt.jwt.JWTUtil;
import jwt_practice.springjwt.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

//Refresh 토큰을 인증하여 Access 토큰 재발급 하는 서비스
@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ResponseEntity<?> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        //cookie에 Refresh 토큰이 없을 경우
        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);

        }

        //Refresh 토큰 만료되었는지 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        //Refresh 토큰의 category가 refresh인지 확인
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

        }

        //Refresh 토큰이 DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //Refresh 토큰 검증을 마친 후 새로운 Access 토큰 생성
        String newAccess = jwtUtil.createJWT("access", username, role, 600000L);
        //Refresh Rotate : Refresh 토큰도 함께 갱신한다
        String newRefresh = jwtUtil.createJWT("refresh", username, role, 86400000L);


        // DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);


        //response
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);

    }

    //쿠키 생성 메서드
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);

        cookie.setMaxAge(24*60*60); //쿠키 유효 기간을 24시간으로 설정

        //cookie.setSecure(true); HTTPS에서만 쿠키를 전송하도록 설정

        cookie.setPath("/"); //해당 도메인의 모든 경로("/")에서 쿠키를 사용 가능
        //기본적으로 setPath("/")를 설정하지 않아도 쿠키는 해당 도메인의 모든 경로에서 유효함

        cookie.setHttpOnly(true); //자바스크립트에서 쿠키에 접근할 수 없음, XSS 공격을 방지하는 중요한 보안 설정

        return cookie;
    }

    //Refresh 토큰을 저장할 객체인 RefreshEntity 생성 메서드
    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setRefresh(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

}
