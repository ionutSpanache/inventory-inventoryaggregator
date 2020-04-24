package net.metrosystems.inventoryaggregator.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2AccessTokenAPI {
	private String token_type;
	private String access_token;
	private Integer expires_in;
	private String refresh_token;

}
