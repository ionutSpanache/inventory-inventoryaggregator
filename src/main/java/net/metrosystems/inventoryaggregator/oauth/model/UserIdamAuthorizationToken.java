
package net.metrosystems.inventoryaggregator.oauth.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "email", "country", "exp", "authorization", "locale", "iat", "sub", "upn", "realm", "aud",
		"userType", "iss" })
public class UserIdamAuthorizationToken {

	@JsonProperty("email")
	private String email;
	@JsonProperty("country")
	private String country;
	@JsonProperty("exp")
	private Integer exp;
	@JsonProperty("authorization")
	private List<Authorization> authorization = null;
	@JsonProperty("locale")
	private String locale;
	@JsonProperty("iat")
	private Integer iat;
	@JsonProperty("sub")
	private String sub;
	@JsonProperty("upn")
	private String upn;
	@JsonProperty("realm")
	private String realm;
	@JsonProperty("aud")
	private String aud;
	@JsonProperty("userType")
	private String userType;
	@JsonProperty("iss")
	private String iss;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	public UserIdamAuthorizationToken withEmail(String email) {
		this.email = email;
		return this;
	}

	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	@JsonProperty("country")
	public void setCountry(String country) {
		this.country = country;
	}

	public UserIdamAuthorizationToken withCountry(String country) {
		this.country = country;
		return this;
	}

	@JsonProperty("exp")
	public Integer getExp() {
		return exp;
	}

	@JsonProperty("exp")
	public void setExp(Integer exp) {
		this.exp = exp;
	}

	public UserIdamAuthorizationToken withExp(Integer exp) {
		this.exp = exp;
		return this;
	}

	@JsonProperty("authorization")
	public List<Authorization> getAuthorization() {
		return authorization;
	}

	@JsonProperty("authorization")
	public void setAuthorization(List<Authorization> authorization) {
		this.authorization = authorization;
	}

	public UserIdamAuthorizationToken withAuthorization(List<Authorization> authorization) {
		this.authorization = authorization;
		return this;
	}

	@JsonProperty("locale")
	public String getLocale() {
		return locale;
	}

	@JsonProperty("locale")
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public UserIdamAuthorizationToken withLocale(String locale) {
		this.locale = locale;
		return this;
	}

	@JsonProperty("iat")
	public Integer getIat() {
		return iat;
	}

	@JsonProperty("iat")
	public void setIat(Integer iat) {
		this.iat = iat;
	}

	public UserIdamAuthorizationToken withIat(Integer iat) {
		this.iat = iat;
		return this;
	}

	@JsonProperty("sub")
	public String getSub() {
		return sub;
	}

	@JsonProperty("sub")
	public void setSub(String sub) {
		this.sub = sub;
	}

	public UserIdamAuthorizationToken withSub(String sub) {
		this.sub = sub;
		return this;
	}

	@JsonProperty("upn")
	public String getUpn() {
		return upn;
	}

	@JsonProperty("upn")
	public void setUpn(String upn) {
		this.upn = upn;
	}

	public UserIdamAuthorizationToken withUpn(String upn) {
		this.upn = upn;
		return this;
	}

	@JsonProperty("realm")
	public String getRealm() {
		return realm;
	}

	@JsonProperty("realm")
	public void setRealm(String realm) {
		this.realm = realm;
	}

	public UserIdamAuthorizationToken withRealm(String realm) {
		this.realm = realm;
		return this;
	}

	@JsonProperty("aud")
	public String getAud() {
		return aud;
	}

	@JsonProperty("aud")
	public void setAud(String aud) {
		this.aud = aud;
	}

	public UserIdamAuthorizationToken withAud(String aud) {
		this.aud = aud;
		return this;
	}

	@JsonProperty("userType")
	public String getUserType() {
		return userType;
	}

	@JsonProperty("userType")
	public void setUserType(String userType) {
		this.userType = userType;
	}

	public UserIdamAuthorizationToken withUserType(String userType) {
		this.userType = userType;
		return this;
	}

	@JsonProperty("iss")
	public String getIss() {
		return iss;
	}

	@JsonProperty("iss")
	public void setIss(String iss) {
		this.iss = iss;
	}

	public UserIdamAuthorizationToken withIss(String iss) {
		this.iss = iss;
		return this;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public UserIdamAuthorizationToken withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	/*
	 * @Override public String toString() { return new
	 * ToStringBuilder(this).append("email", email).append("country",
	 * country).append("exp", exp).append("authorization",
	 * authorization).append("locale", locale).append("iat", iat).append("sub",
	 * sub).append("upn", upn).append("realm", realm).append("aud",
	 * aud).append("userType", userType).append("iss",
	 * iss).append("additionalProperties", additionalProperties).toString(); }
	 */

}
