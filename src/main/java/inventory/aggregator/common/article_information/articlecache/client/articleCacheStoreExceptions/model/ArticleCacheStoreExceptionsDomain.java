package inventory.aggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="Domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleCacheStoreExceptionsDomain {
	@XmlElement(name="KEY")
	private String key;
	@XmlElement(name="VALID_FROM")
	private String validFrom;
	@XmlElement(name="CURRENCY")
	private String currency;
	@XmlElement(name="COUNTRY")
	private String country;
	@XmlElement(name="COMPANY")
	private String company;
	@XmlElement(name="SALESLINE_KEY")
	private String salesLineKey;
	@XmlElement(name="STORE")
	private String store;
	@XmlElement(name="ART_NO")
	private String articleNumber;
	@XmlElement(name="VAR_NO")
	private String variantNumber;
	@XmlElement(name="BUNDLE_NO")
	private String bundleNumber;
	@XmlElement(name="SCOPE")
	private String scope;
	@XmlElement(name="ENV")
	private String environment;
}
