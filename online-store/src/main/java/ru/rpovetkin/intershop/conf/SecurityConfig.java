package ru.rpovetkin.intershop.conf;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.server.ServerWebExchange;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveAuthenticationManager authenticationManager) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login", "/register", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .pathMatchers("/main/**", "/").authenticated()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                )
                .logout(logout -> logout
                        .requiresLogout(ServerWebExchangeMatchers.pathMatchers("/logout"))
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .authenticationManager(authenticationManager)
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((exchange, denied) ->
                                Mono.error(new AccessDeniedException("Access Denied")))
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        manager.setPasswordEncoder(passwordEncoder);
        return manager;
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        return (webFilterExchange, authentication) -> {
            ServerWebExchange exchange = webFilterExchange.getExchange();
            return exchange.getSession()
                    .flatMap(webSession -> webSession.invalidate())
                    .then(Mono.fromRunnable(() -> {
                        exchange.getRequest().getCookies().forEach((name, cookies) -> {
                            exchange.getResponse().addCookie(ResponseCookie.from(name, "")
                                    .path("/")
                                    .maxAge(0)
                                    .httpOnly(true)
                                    .build());
                        });
                    }))
                    .then(Mono.defer(() -> {
                        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                        exchange.getResponse().getHeaders().setLocation(java.net.URI.create("/login?logout"));
                        return exchange.getResponse().setComplete();
                    }));
        };
    }
}
