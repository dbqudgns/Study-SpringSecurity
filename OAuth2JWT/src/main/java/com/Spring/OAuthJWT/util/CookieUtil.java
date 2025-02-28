package com.Spring.OAuthJWT.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie createCookie(String key, String value, Integer expireS) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expireS); //쿠키 유효 기간을 24시간으로 설정
        //cookie.setSecure(true); //HTTPS에서만 쿠키를 전송하도록 설정 (개발환경에서만 주석 처리)
        cookie.setPath("/"); //해당 도메인의 모든 경로("/")에서 쿠키를 사용 가능
        cookie.setHttpOnly(true); //자바스크립트에서 쿠키에 접근할 수 없음, xss 공격을 방지하는 중요한 보안 설정 !

        return cookie;

    }

}
