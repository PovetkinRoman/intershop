package ru.rpovetkin.intershop.conf;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientRequest;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final PaymentServiceProperties paymentServiceProperties;

    @Bean
    public WebClient paymentServiceWebClient() {
        WebClient tokenClient = WebClient.builder().baseUrl(paymentServiceProperties.getAuthServerTokenUrl()).build();

        return WebClient.builder()
                .baseUrl(paymentServiceProperties.getPaymentUrl())
                .filter((request, next) -> getClientCredentialsToken(tokenClient)
                        .flatMap(token -> next.exchange(
                                ClientRequest.from(request)
                                        .headers(h -> h.setBearerAuth(token))
                                        .build()
                        )))
                .build();
    }

    private Mono<String> getClientCredentialsToken(WebClient tokenClient) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", paymentServiceProperties.getClientId());
        form.add("client_secret", paymentServiceProperties.getClientSecret());

        record TokenResponse(String access_token) {}

        return tokenClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::access_token);
    }
} 