package com.example.demo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 한번의 요청에 한번만 필터링이 된다
    @Autowired
    TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseBearerToken(request); // http Header에 있는 토큰정보를 가져온다
            log.info("Filter is running...");

            if (token != null && !token.equalsIgnoreCase("null")) {
                String userId = tokenProvider.validateAndGetUserId(token);
                log.info("Authenticated user ID : " + userId);

                /*
                * 사용자의 정보를 authentication에 넣고 authentication은 SecurityContext에 넣고 SecurityContext는 SecurityContextHolder에 넣는다
                * SecurityContextHolder는 ThreadLocal에 저장되는데 같은 쓰레드라면 어디서든 공유가 가능하다. 쓰레드가 달라지면 인증 정보를 가져올 수 없다. @AuthenticationPrincipal 이용
                * */
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, AuthorityUtils.NO_AUTHORITIES); // 사용자의 인증정보를 저장
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // authentication에 HttpServletRequest 정보를 넣는데 스프링 클래스로 변환(WebAuthenticationDetailsSource)하여 저장
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext(); // SecurityContextHolder에 빈 context 생성
                securityContext.setAuthentication(authentication); // SecurityContext에 authentication 저장
                SecurityContextHolder.setContext(securityContext); // SecurityContextHolder에 SecurityContext 저장
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
