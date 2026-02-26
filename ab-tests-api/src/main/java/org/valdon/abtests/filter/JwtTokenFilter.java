package org.valdon.abtests.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.valdon.abtests.security.JwtTokenProvider;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final static String AUTHORIZATION = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String header = request.getHeader(AUTHORIZATION);
        log.info(header);
        if (header != null && !header.isBlank()) {
            String token = header.substring(7); // Bearer
            log.info(token);
            try{
                 Authentication authentication = jwtTokenProvider.getAuthentication(token);
                 log.info(String.valueOf(authentication != null));
                 if (authentication != null) {
                     SecurityContextHolder.getContext().setAuthentication(authentication);
                 }
            } catch (Exception e) {
                 log.error("error: {}", e.getMessage());
                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                 response.setContentType("application/json");
                 response.getWriter().write("{\"message\":\"Unauthorized\"}");
                 return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
