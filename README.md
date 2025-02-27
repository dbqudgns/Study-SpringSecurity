# Spring Security : Session, JWT, OAuth2(Session, JWT)

## Session : 복습 후 업로드 예정 

## JWT : 2025/01/27 ~ 2025/02/05 

- SpringJWT-Basic : 단일 토큰(Header)으로 진행

- SpringJWT-Advanced : Access(Header) /Refresh(Cookie) 토큰으로 진행 

## OAuth2-Session : 2025/02/17 ~ 남은 과제 마감 후 업데이트

### OAuth2-Session 남은 과제 : 

1. 제공되는 서비스의 인증 서버에서 발급 받은 Access 토큰을 저장 및 관리해주는 JdbcOAuth2AuthorizedClientService의 문제점 해결

- oauth2_authorized_client 테이블 내 속성 중 cleint_registration_id, principal_name이 겹칠 경우 기존 데이터 위에 덮어쓰임

- 작업 : JdbcOAuth2AuthorizedClientService를 커스텀하여 진행 

## OAuth2-JWT : 2025/02/24 ~ 남은 과제 마감 후 업데이트

### OAuth2-JWT 남은 과제  

1. Access 토큰과 Refresh 토큰 응답 처리

- 현재 기본 코드에서는 Access 토큰만 쿠키로 응답하고 있다.

- 리팩토링 목표: Access 토큰은 Header로 Refresh 토큰은 쿠키로 응답하도록 수정

- 추가 작업: Refresh 토큰은 데이터베이스(DB)에 저장해야 한다. 

2. Access 토큰을 Header로 응답하는 API 구현 및 리다이렉션 설정

- 1번 과정에서 Access 토큰을 쿠키로 응답한 후, 클라이언트가 Access 토큰을 Header로 받을 수 있도록 별도의 API를 호출할 수 있게 리다이렉션을 설정.

- 의문점 : 
  
	왜 Access 토큰을 Header로 설정해서 응답해야 하는가 ?

	1번 과정에서 Access 토큰을 Header로 보내면 되지 왜 추후에 api를 만들어서 Header로 보내는가 ? 

3. 로그아웃 기능 구현 

- 로그아웃 시 DB에 저장된 Refresh 토큰을 삭제하여 토큰 재발급을 방지 
