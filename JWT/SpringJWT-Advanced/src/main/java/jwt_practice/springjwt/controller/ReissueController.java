package jwt_practice.springjwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jwt_practice.springjwt.jwt.JWTUtil;
import jwt_practice.springjwt.service.ReissueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

//Refresh 토큰을 인증하여 Access 토큰 재발급 하는 컨트롤러
@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

       return reissueService.accessTokenReissue(request, response);

    }



}
