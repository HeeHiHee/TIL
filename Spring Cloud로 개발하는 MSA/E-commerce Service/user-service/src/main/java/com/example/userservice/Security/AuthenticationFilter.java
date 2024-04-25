package com.example.userservice.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.userservice.Dto.UserDto;
import com.example.userservice.Service.UserService;
import com.example.userservice.vo.RequestLogin;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Slf4j // 이 필터는 /login 엔드포인트에 자동으로 매핑됨
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService userService;
    private final Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(),
                    RequestLogin.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // 인증을 성공하였을때 처리작업
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        String userName = ((User)auth.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(userName); // userName = email

        byte[] secretKeyBytes = Base64.getEncoder().encode(env.getProperty("token.secret").getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        // JWT 토큰 생성
        String token = Jwts.builder()
                .subject(userDetails.getUserId()) // userId로 토큰 생성
                // 만료 기한 설정
                .expiration(
                        Date.from(now.plusMillis(Long.parseLong(env.getProperty("token.expiration_time")))))
                // 암호화 설정
                .issuedAt(Date.from(now))
                .signWith(secretKey)
                .compact();

        res.addHeader("token", token); // 헤더에 토큰을 넣기
        res.addHeader("userId", userDetails.getUserId()); // 헤더에 유저아이디 넣기
    }
}