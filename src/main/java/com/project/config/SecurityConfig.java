package com.project.config;

import com.project.exceptions.auth.CustomAccessDeniedHandler;
import com.project.exceptions.auth.CustomUnauthorizedHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	//private final String[] WHITE_LIST_URL = {"/api/v1/**"};
	private final CustomUnauthorizedHandler customUnauthorizedHandler;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final AuthenticationProvider authenticationProvider;



	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 1. CORS es obligatorio para enviar Cookies al front
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable) // Habilítalo en prod con CookieCsrfTokenRepository
				.authorizeHttpRequests(request -> {
					request.anyRequest().permitAll();
				});
				// 2. Manejo de errores
				/*.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(customUnauthorizedHandler)
						.accessDeniedHandler(customAccessDeniedHandler)
				)

				// 3. Gestión de sesión Stateful (crea JSESSIONID)
				.sessionManagement(sess -> sess
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
				)

				.authorizeHttpRequests(request -> {
					//request.requestMatchers("/api/v1/auth/**").permitAll(); // Endpoints públicos
					request.anyRequest().permitAll();
				})

				// 4. Login "clásico" adaptado para SPA (Sin redirects)
				.formLogin(form -> form
						.loginProcessingUrl("/login") // Endpoint POST donde React envía credenciales
						.successHandler((req, resp, auth) -> resp.setStatus(HttpServletResponse.SC_OK)) // 200 OK
						.failureHandler((req, resp, ex) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED)) // 401
				)

				// 5. Logout limpio
				.logout(logout -> logout
						.logoutUrl("/logout")
						.deleteCookies("JSESSIONID")
						.logoutSuccessHandler((req, resp, auth) -> resp.setStatus(HttpServletResponse.SC_OK))
				)

				.authenticationProvider(authenticationProvider);*/

		return http.build();
	}


		@Bean
		public CorsConfigurationSource corsConfigurationSource() {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Tu URL de React
			configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			configuration.setAllowedHeaders(List.of("*"));
			configuration.setAllowCredentials(true); // ¡Importante! Permite cookies (JSESSIONID)

			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", configuration);
			return source;
		}


}
