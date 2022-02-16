package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;

@Slf4j
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors() // WebMvcConfig에서 이미 설정했으므로 기본 cors 설정
                .and()
                .csrf().disable() // crsf는 사용하지 않으므로 disable
                .httpBasic().disable() // token 사용하므로 disable
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 기반이 아님을 선언
                .and()
                .authorizeRequests()
                .antMatchers("/", "/auth/**").permitAll() // "/","/auth/이하경로"는 인증을 받아야 접근 가능
                .anyRequest().authenticated();

        // CorsFilter이후에 jwtAuthenticationFilter실행
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class); // CorsFilter는 프론트와 연결시 필터작동하고 처리한다.
    }
}
