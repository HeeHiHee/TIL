package com.example.userservice.Security;

import com.example.userservice.Service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // AuthenticationManagerBuilder 가져오기
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        // 사용자 정보 조회할 서비스, 비밀번호 인코딩 설정
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // csrf 비활성화
        http.csrf( (csrf) -> csrf.disable());

        // http 요청에 대한 권한 설정
        http.authorizeHttpRequests((authz) -> authz
                        // requestMatchers : 특정 요청 경로에 대한 접근 설정, access : 접근 권한 지정
                        .requestMatchers("/actuator/**").access(
                                // 특정 ip주소 접근 허용
                                new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1')"))
                        .requestMatchers("/**").access(
                                // 특정 ip주소 접근 허용
                                new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1')"))
                        .anyRequest().authenticated()
                )
                // 인증 관리자 설정
                .authenticationManager(authenticationManager)
                // 세션 관리 정책 설정
                .sessionManagement((session) -> session
                        // 세션을 생성하지 않는 STATELESS 정책을 설정
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // addFilter : 사용자 정의 인증 필터 추가
        http.addFilter(getAuthenticationFilter(authenticationManager));
        // headers : 응답 헤더 설정 지정
        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));

        return http.build();
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        return new AuthenticationFilter(authenticationManager, userService, env);
    }
}
