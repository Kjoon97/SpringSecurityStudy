package com.kang.security.config;

import com.kang.security.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity  //스프링 시큐리티 필터가 스프링 필터 체인에 등록 됨.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)   //  @Secured 활성화, @PreAuthorize 어노테이션 활성화.
public class SecurityConfig{

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") //login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인 진행
                .defaultSuccessUrl("/")     //로그인 완료되면 '/'로 이동 (만약 로그인 전 특정 페이지로 이동 시도했다면 로그인 후 그 특정 페이지로 이동)
                .and()
                .oauth2Login()
                .loginPage("/loginForm")
                .userInfoEndpoint()
                .userService(principalOauth2UserService); //구글 로그인 완료 후 후처리 필요. - 엑세스 토큰, 사용자 프로필 정보를 한번에 받는다.

        return http.build();
    }
}

// 1. 코드 받기(인증), 2. 엑세스 토큰(구글에 사용자 정보 접근 권한)
// 3. 사용자 프로필 정보 가져오고, 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
// 4-2. (이메일, 전화번호, 이름, 아이디) 외에 추가 정보 필요시 따로 정보 받음.
