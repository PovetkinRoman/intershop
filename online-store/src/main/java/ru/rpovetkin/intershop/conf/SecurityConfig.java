package ru.rpovetkin.intershop.conf;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/login", "/register", "/static/**", "/css/**", "/js/**", "/images/**", "/image/**"
    };

    private static final String[] GET_FOR_USER_OR_ANON = {
            "/main/**", "/", "/items/**"
    };

    private static final String[] POST_FOR_USER_OR_ADMIN = {
            "/items/**", "/main/**"
    };

    private static final String[] AUTH_ONLY_SECTIONS = {
            "/cart/**", "/orders/**"
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveAuthenticationManager authenticationManager) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_PATHS).permitAll()
                        .pathMatchers(HttpMethod.GET, GET_FOR_USER_OR_ANON).hasAnyRole("USER", "ADMIN", "ANONYMOUS")
                        .pathMatchers(HttpMethod.POST, POST_FOR_USER_OR_ADMIN).hasAnyRole("USER", "ADMIN")
                        .pathMatchers(AUTH_ONLY_SECTIONS).hasAnyRole("USER", "ADMIN")
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                )
                .logout(logout -> logout
                        .requiresLogout(ServerWebExchangeMatchers.pathMatchers("/logout"))
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .anonymous(anon -> anon
                        .principal("anonymousUser")
                        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")))
                )
                .authenticationManager(authenticationManager)
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                            exchange.getResponse().getHeaders().setLocation(java.net.URI.create("/403"));
                            return exchange.getResponse().setComplete();
                        })
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
                        // Удаляем все пришедшие куки
                        exchange.getRequest().getCookies().forEach((name, cookies) -> {
                            exchange.getResponse().addCookie(ResponseCookie.from(name, "")
                                    .path("/")
                                    .maxAge(0)
                                    .httpOnly(true)
                                    .sameSite("Lax")
                                    .secure(false)
                                    .build());
                        });
                        // Явно чистим стандартные служебные куки, если они были
                        for (String cookieName : new String[]{"SESSION", "JSESSIONID", "XSRF-TOKEN", "SPRING_SECURITY_SAVED_REQUEST"}) {
                            exchange.getResponse().addCookie(ResponseCookie.from(cookieName, "")
                                    .path("/")
                                    .maxAge(0)
                                    .httpOnly(true)
                                    .sameSite("Lax")
                                    .secure(false)
                                    .build());
                        }
                    }))
                    .then(Mono.defer(() -> {
                        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                        exchange.getResponse().getHeaders().setLocation(java.net.URI.create("/login?logout"));
                        return exchange.getResponse().setComplete();
                    }));
        };
    }

}
