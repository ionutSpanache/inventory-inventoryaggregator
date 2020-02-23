
package inventory.aggregator.oauth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
//import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "client", "position", "realm", "country", "salesLine", "store" })
public class RoleAttributes {

	public RoleAttributes(List<String> client, List<String> position, List<String> realm, List<String> country,
			List<String> salesLine, List<String> store) {
		super();
		this.client = client;
		this.position = position;
		this.realm = realm;
		this.country = country;
		this.salesLine = salesLine;
		this.store = store;

	}

	public RoleAttributes() {
	}

	@JsonProperty("client")
	private List<String> client = null;
	@JsonProperty("position")
	private List<String> position = null;
	@JsonProperty("realm")
	private List<String> realm = null;
	@JsonProperty("country")
	private List<String> country = null;
	@JsonProperty("salesline")
	private List<String> salesLine = null;
	@JsonProperty("store")
	private List<String> store = null;

	@JsonProperty("client")
	public List<String> getClient() {
		return client;
	}

	@JsonProperty("client")
	public void setClient(List<String> client) {
		this.client = client;
	}

	public RoleAttributes withClient(List<String> client) {
		this.client = client;
		return this;
	}

	@JsonProperty("position")
	public List<String> getPosition() {
		return position;
	}

	@JsonProperty("position")
	public void setPosition(List<String> position) {
		this.position = position;
	}

	public RoleAttributes withPosition(List<String> position) {
		this.position = position;
		return this;
	}

	@JsonProperty("realm")
	public List<String> getRealm() {
		return realm;
	}

	@JsonProperty("realm")
	public void setRealm(List<String> realm) {
		this.realm = realm;
	}

	public RoleAttributes withRealm(List<String> realm) {
		this.realm = realm;
		return this;
	}

	@JsonProperty("country")
	public List<String> getCountry() {
		return country;
	}

	@JsonProperty("country")
	public void setCountry(List<String> country) {
		this.country = country;
	}

	public RoleAttributes withCountry(List<String> country) {
		this.country = country;
		return this;
	}

	@JsonProperty("salesLine")
	public List<String> getSalesLine() {
		return salesLine;
	}

	@JsonProperty("salesLine")
	public void setSalesLine(List<String> salesLine) {
		this.salesLine = salesLine;
	}

	public RoleAttributes withSalesLine(List<String> salesLine) {
		this.salesLine = salesLine;
		return this;
	}

	@JsonProperty("store")
	public List<String> getStore() {
		return store;
	}

	@JsonProperty("store")
	public void setStore(List<String> store) {
		this.store = store;
	}

	public RoleAttributes withStore(List<String> store) {
		this.store = store;
		return this;
	}
	/*
	 * @Override public String toString() { return new
	 * ToStringBuilder(this).append("client", client).append("position",
	 * position).append("realm", realm).append("additionalProperties",
	 * additionalProperties).toString(); }
	 */

}
