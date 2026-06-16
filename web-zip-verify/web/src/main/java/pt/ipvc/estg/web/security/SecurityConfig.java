package pt.ipvc.estg.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final boolean securityDisabled;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          @org.springframework.beans.factory.annotation.Value("${security.disable:false}") boolean securityDisabled) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.securityDisabled = securityDisabled;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (securityDisabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/swagger/**", "/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers("/auth/**").authenticated()
                .requestMatchers("/bo/**").hasRole("ADMIN")
                .requestMatchers("/fo/**").hasRole("STUDENT")
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
