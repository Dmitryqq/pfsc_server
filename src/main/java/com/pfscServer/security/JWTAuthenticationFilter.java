package com.pfscServer.security;

import com.pfscServer.domain.ApplicationUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfscServer.repo.ApplicationUserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.pfscServer.security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private ApplicationUserRepository applicationUserRepository;
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationUserRepository applicationUserRepository) {
        this.authenticationManager = authenticationManager;
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            ApplicationUser creds = new ObjectMapper()
                    .readValue(req.getInputStream(), ApplicationUser.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        ApplicationUser user;
        user = applicationUserRepository.findByUsername(((User) auth.getPrincipal()).getUsername());
        String token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                .claim("id", user.getId())  //Добавление кастомного поля в токен
                .claim("name", user.getName())  //Добавление кастомного поля в токен
                .claim("role", user.getRole())  //Добавление кастомного поля в токен
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}