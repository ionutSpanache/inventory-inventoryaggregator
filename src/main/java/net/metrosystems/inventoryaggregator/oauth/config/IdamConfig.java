package net.metrosystems.inventoryaggregator.oauth.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class IdamConfig {

//    @Value(value = "${idam.gc.serviceinternal.grant.type}")
//    private String serviceInternalGrantType;
//    @Value(value = "${idam.url.jwk.provider}")
//    private String idamJwkUrl;

	@Value(value = "${idam.client.id}")
	private String clientId;

	@Value(value = "${idam.realm.id}")
	private String realmId;

	@Value(value = "${idam.grant.type}")
	private String grantType;


    @Value(value = "${idam.client.secret}")
	private String clientSecret;

	@Value(value = "${idam.url}")
	private String idamUrl;

	@Value(value = "${idam.get.token}")
	private String idamGetToken;

	@Value(value = "${idam.url.jwk.provider}")
	private String idamJwkUrl;

	@Value(value = "${idam.refresh.public.key}")
	private String cronPurgePublicKeyCache;

	@Value("${idam.AUTHORIZATION_HEADER_STRING}")
	public String authorizationHeaderString;

	@Value("${idam.SIGN_UP_URL}")
	public String signUpURL;

	@Value("${idam.AUTHORIZATION_COOKIE_NAME}")
	public String authorizationCookieName;

	@Value("${idam.USER_LOGIN_URL}")
	public String userLoginUrl;

	@Value("${idam.USER_INFO_COOKIE_NAME}")
	public String userInfoCookieName;

	@Bean
	public String getCronPurgePublicKeyCache() {
		return cronPurgePublicKeyCache;
	}

}
