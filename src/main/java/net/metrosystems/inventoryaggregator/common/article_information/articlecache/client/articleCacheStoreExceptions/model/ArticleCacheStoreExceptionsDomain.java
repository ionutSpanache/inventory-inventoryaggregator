package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
