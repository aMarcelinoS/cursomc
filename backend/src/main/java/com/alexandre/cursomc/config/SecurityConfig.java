package com.alexandre.cursomc.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.alexandre.cursomc.security.JWTAuthenticationFilter;
import com.alexandre.cursomc.security.JWTAuthorizationFilter;
import com.alexandre.cursomc.security.jwt.JWTUtil;	

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;	
	
	
	//Lista de endpoints que serão permitidos acessar sem autenticaçao
	private static final String[] PUBLIC_MATCHERS = {
			"/h2-console/**",
	};
	
	//Lista de endpoints que serão permitidos acessar sem autenticação apenas para recuperar dados
	private static final String[] PUBLIC_MATCHERS_GET = {
			"/produtos/**",
			"/categorias/**"
	};
	
	private static final String[] PUBLIC_MATCHERS_POST = {
			"/clientes/**",
			"/auth/forgot/**"
	};
	
	//Configura as permissões de acesso aos endpoint´s
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		if(Arrays.asList(environment.getActiveProfiles()).contains("test")) {   /*Testa qual profile do projeto está ativo*/
			http.headers().frameOptions().disable(); /*Libera o acesso ao BD H2 se o profile ativo for "test" */
		}
		http.cors().and().csrf().disable();
		
		http
		.authorizeHttpRequests()
			.antMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
			.antMatchers(HttpMethod.GET, PUBLIC_MATCHERS_GET).permitAll()
			.antMatchers(PUBLIC_MATCHERS).permitAll()
			.anyRequest().authenticated();
			
		http.apply(custom()); /* <- Aplica o registro do filtro de autenticação customizado*/
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); /*Assegura que o back-end não crie sessão de usuário*/
		return http.build();				
	}
	
	//Acessa o método authenticationManager sem a dependência do WebSecurityConfigurerAdapter e registra o filtro de autenticação
	public class MyCustom extends AbstractHttpConfigurer<MyCustom, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
			http.addFilter(new JWTAuthenticationFilter(authenticationManager, jwtUtil));
			http.addFilter(new JWTAuthorizationFilter(authenticationManager, jwtUtil, userDetailsService));
		}		
	}
	
	//Retorna a customização com o registro do filtro de autenticação 
	public MyCustom custom() {
		return new MyCustom();
	}
	

	//Gera um encode da senha original (PasswordEncoder)
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}	
	
	//Permite requisições de múltiplas fontes, libera acesso aos endpoints com configurações básicas
	@Bean
	CorsConfigurationSource corsConfigurationSource() {		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}	
}
