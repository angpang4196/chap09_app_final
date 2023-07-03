package org.edupoll.config;

import org.edupoll.config.support.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JWTAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	SecurityFilterChain finalAppSecurityChain(HttpSecurity http) throws Exception {

		http.csrf(t -> t.disable());

		http.sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(t -> t //
				.requestMatchers("/api/v1/user/private/**").authenticated() //
				.requestMatchers(HttpMethod.POST, "/api/v1/feed/storage/**").authenticated() //
				.anyRequest().permitAll());

		http.anonymous(t -> t.disable());

		http.logout(t -> t.disable());

		http.addFilterBefore(jwtAuthenticationFilter, AuthorizationFilter.class);
		
		http.cors();

		return http.build();
	}
}
