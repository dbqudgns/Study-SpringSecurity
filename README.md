# Spring Security : Session, JWT, OAuth2(Session, JWT)

## Session : 복습 후 업로드 예정 

## JWT : 2025/01/27 ~ 2025/02/05 

- SpringJWT-Basic : 단일 토큰(Header)으로 진행

- SpringJWT-Advanced : Access(Header) / Refresh(Cookie) 토큰으로 진행

- 의문점 :

  Access Token은 왜 Header로 응답하는가 ?

  = Access Token을 Cookie에 저장해서 응답하면, 클라이언트가 모든 요청에 자동으로 포함하기 때문에 CSRF 공격에 취약할 수 있다. 

  = 따라서, Header로 응답을 하게 될 시 클라이언트가 자동으로 토큰을 포함하지 않아 CSRF 공격을 차단할 수 있다.

  = 권한이 필요한 모든 경로에 사용되는 Access 토큰은 CSRF 공격의 위험보단 XSS 공격을 받는게 더 나은 선택일 수 있다 !

  ( 추가적으로, XSS 보안 조치도 가능 )

  Refresh Token은 왜 Cookie로 응답하는가 ?

  = 쿠키는 XSS 공격을 받을 수 있지만, HttpOnly를 설정하면 JavaScript가 쿠키에 접근할 수 없어 완벽히 방어할 수 있다.

  = 하지만, CSRF 공격을 방어하지 못하는데 Refresh Token의 사용처는 Token 재발급 api에서만 사용하므로 크게 피해를 입힐 만한 로직이 없다.

- XSS, CSRF 공격 정의

  = XSS : 공격자가 웹 페이지에 악의적인 스크립트를 삽입하여 사용자의 정보를 탈튀하거나 조작하는 공격

  = CSRF : 사용자가 자신의 의지와는 무관하게 공격자가 의도한 행위를 웹 어플리케이션에 요청하게 만드는 공격 

## OAuth2-Session : 2025/02/17 ~ 2025/03/07
### OAuth2-Session 남은 과제 : 

1. 제공되는 서비스의 인증 서버에서 발급 받은 Access 토큰을 저장 및 관리해주는 JdbcOAuth2AuthorizedClientService의 문제점
 
=> Pull requests : [ Refactor : OAuth2-Session 과제 1번 구현 ]

- oauth2_authorized_client 테이블 내 속성 중 cleint_registration_id, principal_name이 겹칠 경우 기존 데이터 위에 덮어쓰임

- 특히, 해당 계정의 소유자 이름(principal_name 데이터 값)이 같을 경우 덮어씌는 문제 발생 

- 작업 : JdbcOAuth2AuthorizedClientService를 커스텀하여 진행 => CustomCustomJdbcOAuth2AuthorizedClientService

- 해결 방법 : oauth2_authorized_client 테이블 필드 중 principal_name 데이터 값을 해당 계정의 소유자 이름이 아닌 인증 서버에서 보내주는 사용자의 고유한 id 값인 providerId로 변경

## OAuth2-JWT : 2025/02/24 ~ 2025/02/28

### OAuth2-JWT 남은 과제  

1. Access 토큰과 Refresh Token 응답 처리 => Pull requests : [ Refactor : OAuth2-JWT 남은 과제 1번, 2번 구현 ]

- 현재 기본 코드에서는 Access 토큰만 쿠키로 응답하고 있다.

- 리팩토링 목표: Access 토큰은 Header로 Refresh 토큰은 쿠키로 응답하도록 수정

- 추가 작업: Refresh 토큰은 데이터베이스(DB)에 저장해야 한다. 

2. Access 토큰을 Header로 응답하는 API 구현 및 리디렉션 설정 => Pull requests : [ Refactor : OAuth2-JWT 남은 과제 1번, 2번 구현 ]

- 1번 과정에서 Access 토큰을 쿠키로 응답한 후, 클라이언트가 Access 토큰을 Header로 받을 수 있도록 별도의 API를 호출할 수 있게 리디렉션을 설정.

- 의문점 : 
  
  1번 과정에서 Access 토큰을 Header로 보내면 되지 왜 추후에 api를 만들어서 Header로 보내는가 ?

  = OAuth2는 인증 코드를 리디렉션을 통해 전달하는 방식이기 때문에, 하이퍼링크 없이 소셜 로그인을 진행할 수 없다. 

  = OAuth2 로그인 후 백엔드가 JWT를 발급해야 하지만, OAuth2의 리디렉션 방식에서는 프론트가 서버의 응답 헤더를 직접 읽을 수 없다.

  = 따라서, JWT를 프론트에게 전달하기 위해서는 Cookie를 통해 응답해야 한다.

3. Refresh Token Rotate 및 Access Token 재발급 => Pull requests : [ Refactor : OAuth2-JWT 남은 과제 3번 구현 ]

- Refresh Token을 인증하여 Access Token 재발급 구현

- Refresh Token Rotate 구현 
   
4. 로그아웃 기능 구현 => Pull requests : [ Refactor : OAuth2-JWT 남은 과제 4번 구현 및 JWTFilter 수정 ]

- 로그아웃 시 DB에 저장된 Refresh 토큰을 삭제하여 Token 재발급을 방지 
