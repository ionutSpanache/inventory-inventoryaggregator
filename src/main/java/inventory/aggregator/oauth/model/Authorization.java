
package inventory.aggregator.oauth.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
/*
 * @JsonPropertyOrder({ "IDAM_REALM_VIEWER" })
 */
public class Authorization {
	public enum IdamEntity {
		client, position, realm, country, salesLine, store
	}

	private String roleName;

	@JsonIgnore
	private List<RoleAttributes> rolesWithAttributes = new ArrayList<>();

	@JsonAnyGetter
	public List<RoleAttributes> getRolesWithAttributes() {
		return this.rolesWithAttributes;
	}

	@JsonAnySetter
	public void setAdditionalPropertyObject(String roleName, Object roleAttributesObject) {
		@SuppressWarnings("unchecked")
		List<Map<String, List<String>>> roleAttributesCombination = (ArrayList<Map<String, List<String>>>) roleAttributesObject;
		roleAttributesCombination.stream().forEach(roleAttributesMap -> {
			RoleAttributes roleWithAttributes = new RoleAttributes()
					.withClient(roleAttributesMap.get(IdamEntity.client.name()))
					.withPosition(roleAttributesMap.get(IdamEntity.position.name()))
					.withRealm(roleAttributesMap.get(IdamEntity.realm.name()))
					.withCountry(roleAttributesMap.get(IdamEntity.country.name()))
					.withSalesLine(roleAttributesMap.get(IdamEntity.salesLine.name()))
					.withStore(roleAttributesMap.get(IdamEntity.store.name()));
			this.setRoleName(roleName);
			this.rolesWithAttributes.add(roleWithAttributes);
		});
	}

	public Authorization withAdditionalPropertyObject(String roleName, RoleAttributes roleAttributesObject) {
		this.setAdditionalPropertyObject(roleName, roleAttributesObject);
		return this;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
