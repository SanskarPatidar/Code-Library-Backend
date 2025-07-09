package com.sanskar.Code.Library.Backend.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // for method-level annotations
public class SecurityConfig {


    @Autowired
    private JWTFilter jwtFilter;

    @Autowired
    private LogoutHandler logoutHandler; // custom logout handler

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf(AbstractHttpConfigurer::disable) // enough for both rest and ws
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // don’t use server-side sessions. The stateless policy ensures Spring won’t store authentication state between requests. // In a stateless JWT REST API, CSRF tokens are not needed because the server does not maintain user sessions. As one guide explains, for stateless JWT authentication, we should “enable CORS and disable CSRF”
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/auth/**",
                                "/oauth/google/callback",
                                "/docs",
                                "/v3/api-docs/**",        // <-- OpenAPI JSON
                                "/swagger-ui.html",
                                "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable) // disable basic authentication, as we are using JWT for authentication
                .formLogin(AbstractHttpConfigurer::disable) // disable form login, as we are using JWT for authentication
                // disable form login, as we are using JWT for authentication
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // ensures our JWTFilter runs before Spring’s built-in UsernamePasswordAuthenticationFilter
                .logout(logout ->
                        logout.logoutUrl("/auth/logout") // no need for controller customization
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
                .build();
                // This way, any valid JWT in the request is parsed and the user’s identity is set before form-login or basic-auth filters run


    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); // there are other providers, // provide our service class to DaoAuthProvider
        provider.setPasswordEncoder(passwordEncoder()); // encoder on password
        return provider;
    }

    @Bean //Spring Security 6 allows exposing the AuthenticationManager as a bean using the AuthenticationConfiguration
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    // Naturally, bean means getting hold of that object yourself

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }



}