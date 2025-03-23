// src/main/java/com/example/pointsofinterest/security/TokenAuthenticationFilter.java
package com.example.pointsofinterest.security;

import com.example.pointsofinterest.model.ApiToken;
import com.example.pointsofinterest.repository.ApiTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final ApiTokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                ApiToken apiToken = tokenRepository.findByToken(token)
                        .orElse(null);
                
                if (apiToken != null && apiToken.isActive() && 
                        (apiToken.getExpiresAt() == null || apiToken.getExpiresAt().isAfter(LocalDateTime.now()))) {
                    
                    // Create authentication
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            "API_USER", null, 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_USER")));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("Could not authenticate token", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("X-API-Token");
        if (StringUtils.hasText(authHeader)) {
            return authHeader;
        }
        return null;
    }
}