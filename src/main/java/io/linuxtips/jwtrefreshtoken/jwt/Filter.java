package io.linuxtips.jwtrefreshtoken.jwt;

import io.linuxtips.jwtrefreshtoken.security.UserAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class Filter extends OncePerRequestFilter {

    private final UserAuthenticationService authenticationService;

    private final Util jwtUtil;

    public Filter(UserAuthenticationService authenticationService, Util jwtUtil) {
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       final String authorization = request.getHeader("Authorization");

       String username = null,jwtToken= null;

       if(Objects.nonNull(authorization) && authorization.startsWith("Bearer ")){
           jwtToken = authorization.substring(7);
           username=jwtUtil.getUsername(jwtToken);
       }

       if(Objects.nonNull(username) &&
               Objects.isNull(SecurityContextHolder.getContext().getAuthentication())){
           UserDetails userDetails = this.authenticationService.loadUserByUsername(username);

           if(jwtUtil.verifyToken(jwtToken,userDetails)){
               UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                       new UsernamePasswordAuthenticationToken(
                               userDetails,null,userDetails.getAuthorities()
                       );
               usernamePasswordAuthenticationToken.setDetails(
                       new WebAuthenticationDetailsSource().buildDetails(
                               request
                       )
               );
               SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
           }

       }
       filterChain.doFilter(request,response);
    }
}
