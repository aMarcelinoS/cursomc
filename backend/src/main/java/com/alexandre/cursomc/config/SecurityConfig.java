package com.alexandre.cursomc.config;

import java.util.Arrays;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
	
	@Autowired
	private Environment environment;
	
	//Lista de endpoints que serão permitidos acessar sem autenticaçao
	private static final String[] PUBLIC_MATCHERS = {
			"/h2-console/**",
	};
	
	//Lista de endpoints que serão permitidos acessar sem autenticação apenas recuperar dados
	private static final String[] PUBLIC_MATCHERS_GET = {
			"/produtos/**",
			"/categorias/**"
	};
	
	
	
	//Configura as permissões de acesso aos endpoint´s
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		if(Arrays.asList(environment.getActiveProfiles()).contains("test")) {   /*Testa qual profile do projeto está ativo*/
			http.headers().frameOptions().disable(); /*Libera o acesso ao BD H2 se o profile ativo for "test" */
		}
		
		http.cors().and().csrf().disable();
		http
			.authorizeHttpRequests((authz)-> authz
				.antMatchers(HttpMethod.GET, PUBLIC_MATCHERS_GET).permitAll()
				.antMatchers(PUBLIC_MATCHERS).permitAll()
				.anyRequest().authenticated());		
				
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); /*Assegura que o back-end não crie sessão de usuário*/
		return http.build();				
	}	
	
	//Permite requisições de múltiplas fontes, libera acesso aos endpoints com configurações básicas
	@Bean
	CorsConfigurationSource corsConfigurationSource() {		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}
	
}
