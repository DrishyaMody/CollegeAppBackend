package com.nighthawk.spring_portfolio.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {	
        
        http
            // JWT related configuration
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use STATELESS for JWT
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST,"/authenticate").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/person/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/person/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/people/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/person/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/person/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/mvc/person/**").authenticated()
                .requestMatchers(HttpMethod.GET,"/login").permitAll()
                .requestMatchers(HttpMethod.POST,"/authenticateForm").permitAll()
                .requestMatchers("/api/mining/**").authenticated()
                .requestMatchers("/**").permitAll()
            )
            .cors(Customizer.withDefaults())
            .headers(headers -> headers
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Credentials", "true"))
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Expose-Headers", "*", "Authorization")) // Fixed typo for Expose Headers
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "Content-Type", "Authorization", "x-csrf-token"))
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Max-Age", "600")) // Fixed typo for Max Age
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Methods", "POST", "GET", "DELETE", "OPTIONS", "HEAD"))
            )
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			)
			.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            
            .formLogin(form -> form 
                .loginPage("/login")
                .defaultSuccessUrl("/mvc/person/read")
            )
            .logout(logout -> logout
                .deleteCookies("sess_java_spring")
                .logoutSuccessUrl("/")
            );

        return http.build();
    }
}
