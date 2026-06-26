package net.metrosystems.inventoryaggregator.config.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class WebClientConfig {
  private static final String ACCEPT_ENCODING = "Accept-Encoding";

  @Bean
  WebClient webClientMstoreSecured(ReactiveClientRegistrationRepository clientRegistrationsMstore) {
    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
        getServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationsMstore);
    oauth2.setDefaultClientRegistrationId("idam");
    return WebClient.builder()
        .filter(oauth2)
        .exchangeStrategies(
            ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build())
        .defaultHeaders(
            httpHeaders -> httpHeaders.put(HttpHeaders.ACCEPT_ENCODING, List.of("gzip")))
        .build();
  }

  @Bean
  ReactiveClientRegistrationRepository clientRegistrationsMstore(
      @Value("${spring.security.oauth2.client.provider.idam.token-uri}") String tokenUri,
      @Value("${spring.security.oauth2.client.registration.idam.client-id}") String clientId,
      @Value("${spring.security.oauth2.client.registration.idam.client-secret}")
          String clientSecret,
      @Value("${spring.security.oauth2.client.registration.idam.scope}") String scope,
      @Value("${spring.security.oauth2.client.registration.idam.authorization-grant-type}")
          String grantType) {

    ClientRegistration registration =
        ClientRegistration.withRegistrationId("idam")
            .tokenUri(tokenUri)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .scope(scope)
            .authorizationGrantType(new AuthorizationGrantType(grantType))
            .build();

    return new InMemoryReactiveClientRegistrationRepository(registration);
  }

  @Bean
  WebClient webClientForAcl(ReactiveClientRegistrationRepository clientRegistrationsAcl) {
    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
        getServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationsAcl);
    oauth2.setDefaultClientRegistrationId("idamacl");
    return WebClient.builder()
        .filter(oauth2)
        .exchangeStrategies(
            ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build())
        .defaultHeaders(httpHeaders -> httpHeaders.put(ACCEPT_ENCODING, List.of("gzip")))
        .build();
  }

  @Bean
  ReactiveClientRegistrationRepository clientRegistrationsAcl(
      @Value("${spring.security.oauth2.client.provider.idamacl.token-uri}") String tokenUri,
      @Value("${spring.security.oauth2.client.registration.idamacl.client-id}") String clientId,
      @Value("${spring.security.oauth2.client.registration.idamacl.client-secret}")
          String clientSecret,
      @Value("${spring.security.oauth2.client.registration.idamacl.scope}") String scope,
      @Value("${spring.security.oauth2.client.registration.idamacl.authorization-grant-type}")
          String grantType) {

    ClientRegistration registration =
        ClientRegistration.withRegistrationId("idamacl")
            .tokenUri(tokenUri)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .scope(scope)
            .authorizationGrantType(new AuthorizationGrantType(grantType))
            .build();

    return new InMemoryReactiveClientRegistrationRepository(registration);
  }

  private static ServerOAuth2AuthorizedClientExchangeFilterFunction
      getServerOAuth2AuthorizedClientExchangeFilterFunction(
          ReactiveClientRegistrationRepository clientRegistrationsCache) {
    InMemoryReactiveOAuth2AuthorizedClientService clientService =
        new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationsCache);
    AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
        new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
            clientRegistrationsCache, clientService);
    return new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
  }
}
