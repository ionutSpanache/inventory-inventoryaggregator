package net.metrosystems.inventoryaggregator.oauth.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.metrosystems.inventoryaggregator.oauth.config.IdamConfig;
import net.metrosystems.inventoryaggregator.oauth.model.OAuth2AccessTokenAPI;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

//import org.apache.tomcat.util.codec.binary.Base64;

@Service
@CacheConfig(cacheNames = { "security" })
public class OAuth2ClientTokenSevice {

	// private final RestTemplate restTemplate;
	private final IdamConfig idamConfig;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public OAuth2ClientTokenSevice(/* RestTemplate restTemplate, */ IdamConfig idamConfig) {
		// this.restTemplate = restTemplate;
		this.idamConfig = idamConfig;
	}

	@Cacheable(value = "access_token", sync = true)
	@Retryable(maxAttempts = 3, value = { HttpClientErrorException.class,
			IOException.class }, backoff = @Backoff(delay = 2000), exclude = {})
	public OAuth2AccessTokenAPI getOAuth2AccessTokenFromIDAM() throws IOException {

		String url = idamConfig.getIdamUrl() + idamConfig.getIdamGetToken();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		String auth = idamConfig.getClientId() + ":" + idamConfig.getClientSecret();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String(encodedAuth);

		httpHeaders.set("Authorization", authHeader);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", idamConfig.getGrantType());
		map.add("client_id", idamConfig.getClientId());
		map.add("realm_id", idamConfig.getRealmId());

		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		converters.add(new FormHttpMessageConverter());
		converters.add(new StringHttpMessageConverter());
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(converters);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

		return new ObjectMapper().readValue(response.getBody(), OAuth2AccessTokenAPI.class);
	}

	public Claim getKidFromToken(String token) {
		return JWT.decode(token).getHeaderClaim("kid");
	}

	@Cacheable(value = "jwtPublicKey", sync = true)
	@Retryable(maxAttempts = 3, value = { HttpClientErrorException.class,
			IOException.class }, backoff = @Backoff(delay = 2000), exclude = { MalformedURLException.class })
	public RSAPublicKey getPublicKeyFromUrlJwkProvider(Claim kid) throws MalformedURLException, JwkException {

		JwkProvider provider = new UrlJwkProvider(new URL(idamConfig.getIdamUrl() + idamConfig.getIdamJwkUrl()), 5000,
				5000);
		Jwk jwk = provider.get(kid.asString());

		return (RSAPublicKey) jwk.getPublicKey();
	}

	public DecodedJWT verifyToken(String token) throws MalformedURLException, JwkException {

		Claim kid = getKidFromToken(token);
		RSAPublicKey rsaPublicKey = getPublicKeyFromUrlJwkProvider(kid);

		Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, null);
		JWTVerifier verifier = JWT.require(algorithm)
				// .withIssuer(issuer.asString())
				// .acceptLeeway(1)
				.build();

		return verifier.verify(token);
		//return JWT.decode(token);


	}

	public String getAccessToken() {
		DecodedJWT jwt = null;
		String accessToken = "";
		OAuth2AccessTokenAPI oauth2AccesToken = new OAuth2AccessTokenAPI();
		try {
			oauth2AccesToken = getOAuth2AccessTokenFromIDAM();
			accessToken = oauth2AccesToken.getAccess_token();
			jwt = verifyToken(accessToken);
		} catch (TokenExpiredException e) {
			clearCache();
			try {
				oauth2AccesToken = getOAuth2AccessTokenFromIDAM();
				accessToken = oauth2AccesToken.getAccess_token();
				jwt = verifyToken(accessToken);
			} catch (IOException | JwkException ex) {
				throw new RuntimeException(ex);
			}
		} catch (IOException | JwkException e) {
			throw new RuntimeException(e);
		}

		// Integer expireIn = oauth2AccesToken.getExpires_in();

		Date expireDate = jwt.getExpiresAt();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -3);
		Date currentDate = calendar.getTime();
		if (expireDate.before(currentDate)) {
			clearCache();
			try {
				oauth2AccesToken = getOAuth2AccessTokenFromIDAM();
				accessToken = oauth2AccesToken.getAccess_token();
				jwt = verifyToken(accessToken);
			} catch (IOException | JwkException ex) {
				throw new RuntimeException(ex);
			}

		}

		return accessToken;
	}

	public Optional<DecodedJWT> getJWTTokenFromIDAM() {

		DecodedJWT jwt;

		try {
			jwt = verifyToken(getOAuth2AccessTokenFromIDAM().getAccess_token());
		} catch (TokenExpiredException e) {
			clearCache();
			try {
				jwt = verifyToken(getOAuth2AccessTokenFromIDAM().getAccess_token());
			} catch (IOException | JwkException ex) {
				throw new RuntimeException(ex);
			}
		} catch (IOException | JwkException e) {
			throw new RuntimeException(e);
		}

		Date expireDate = jwt.getExpiresAt();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -3);
		Date currentDate = calendar.getTime();
		if (expireDate.before(currentDate)) {
			clearCache();
			try {
				jwt = verifyToken(getOAuth2AccessTokenFromIDAM().getAccess_token());
			} catch (IOException | JwkException ex) {
				throw new RuntimeException(ex);
			}

		}

		return Optional.ofNullable(jwt);
	}

	@Scheduled(cron = "#{@getCronPurgePublicKeyCache}")
	@CacheEvict(cacheNames = { "access_token", "jwtPublicKey" }, allEntries = true)
	public void clearCache() {
		log.warn("IDAM ACCES TOKEN Caches cleared.");
	}

}
