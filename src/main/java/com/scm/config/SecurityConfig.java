package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.scm.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

//	@Bean
//	public UserDetailsService userDetailsService() {
//		
//		UserDetails user1 =User
//				.withDefaultPasswordEncoder()
//				.username("yash1234").password("yash1234")
//				.roles("ADMIN","USER")
//				.build();
//		
//		var inMemoryUserDeatailManager =  new InMemoryUserDetailsManager(user1);
//		return inMemoryUserDeatailManager;
//		
//	}
	
	@Autowired
	private OauthAuthenticationSuccessHandler handler;
	
	@Autowired
	private SecurityCustomUserDetailService userDetailService;
	
	@Autowired
	private AuthFailureHandler authFailureHandler;

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

		// userdetails object
		daoAuthenticationProvider.setUserDetailsService(userDetailService);

		// password encoder object
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.authorizeHttpRequests(authorize->{
			authorize.requestMatchers("/user/**").authenticated();
			authorize.anyRequest().permitAll();
		});
		
		httpSecurity.formLogin(formLogin->{
			formLogin.loginPage("/login");
			formLogin.loginProcessingUrl("/authenticate");
			formLogin.defaultSuccessUrl("/user/profile");
//			formLogin.successForwardUrl("/user/dashboard");
//			formLogin.failureForwardUrl("/login?error=true");
			formLogin.usernameParameter("email");
			formLogin.passwordParameter("password");
			
			formLogin.failureHandler(authFailureHandler);
		});
		
		httpSecurity.csrf(AbstractHttpConfigurer::disable);
		
		httpSecurity.logout(logoutForm->{
			logoutForm.logoutUrl("/logout");
			logoutForm.logoutSuccessUrl("/login?logout=true");
		});
		
		
		// oauth2 login GOOGLE, GITHUB
		
		httpSecurity.oauth2Login(oauth->{
			oauth.loginPage("/login");
			oauth.successHandler(handler);
		});
		
		return httpSecurity.build();
	}
	
	
}
