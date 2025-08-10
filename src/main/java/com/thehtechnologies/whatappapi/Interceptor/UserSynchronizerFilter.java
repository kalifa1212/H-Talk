package com.thehtechnologies.whatappapi.Interceptor;

import com.thehtechnologies.whatappapi.Security.JwtUtil;
import com.thehtechnologies.whatappapi.Security.MyUserDetailsService;
import com.thehtechnologies.whatappapi.User.UserSynchronizer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizerFilter extends OncePerRequestFilter {

    private final UserSynchronizer userSynchronizer;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

//        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
//            JwtAuthenticationToken token = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
//
//            //userSynchronizer.synchronizeWithIdp(token.getToken());
//        }

        final  String authHeader=request.getHeader("Authorization");
        String username=null;
        String jwt=null;
        if (StringUtils.hasLength(authHeader) && authHeader.startsWith("Bearer ") ){
            jwt=authHeader.substring(7);
            username= jwtUtil.extractUserName(jwt);
        }
        if(StringUtils.hasLength(username) && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=userDetailsService.loadUserByUsername(username);
            if(jwtUtil.isTokenExpired(jwt)) {
                log.info("Token expirer  {}",userDetails);
            }
            if (jwtUtil.validToken(jwt,userDetails)){
                //log.info("Validation du Token par  {}",userDetails);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );

                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
