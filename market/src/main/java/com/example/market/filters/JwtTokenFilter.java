package com.example.market.filters;

import com.example.market.jwt.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    // 내가 만든 JPAUserDetailsService 구현체가 주입
    private final UserDetailsService service;



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("try jwt filter");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.split(" ")[1];
            if(jwtTokenUtils.validate(token)){
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                
                String username = jwtTokenUtils.parseClaims(token).getSubject();
                UserDetails userDetails = service.loadUserByUsername(username);
                userDetails.getAuthorities().forEach(auth -> log.info(String.valueOf(auth)));

                AbstractAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                token,
                                userDetails.getAuthorities()
                        );
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                log.info("set security context with jwt");
                
            }
            else{
                log.warn("jwt validation failed");
            }
        }
        filterChain.doFilter(request, response);
        
    }
}
