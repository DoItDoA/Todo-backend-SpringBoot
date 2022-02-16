package com.example.demo.security;

import com.example.demo.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    private static final String SECRET_KEY = "5gpPgP4SJtGHB3bZ5QQvoURgwrRRF";

    // 토큰 생성
    public String create(UserEntity userEntity) {
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));  // 현재 시간에서 1일 더하여 저장
        // from은 Instant를 파라미터로 받아서, Date 객체로 변환하여 리턴
        // Instant는 타임라인의 특정 시점을 나타낸다. 사건이 발생한 시점을 기록하는 데 사용
        // plus는 1을 더하되 날짜로 설정하여 더한다

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) //해시 알고리듬
                .setSubject(userEntity.getId()) // 토큰의 주인(식별자) 설정
                .setIssuer("demo app") // 토큰 발행주체 설정
                .setIssuedAt(new Date()) // 발생된 시간
                .setExpiration(expiryDate) //토큰 만료기간
                .compact();
    }

    // 토큰 유효성 확인
    public String validateAndGetUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token) // 요청헤더로부터 가져온 토큰을 Jws로 파싱, jws(서버에서 인증을 증거로 인증 정보를 token화 한것)
                .getBody();

        return claims.getSubject(); //토큰의 식별자를 반환하거나 없으면 null 반환
    }
}
