package inventory.aggregator.common.article_information.articlecache.client.article_cache_model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Domain")
public class ArticleCacheDomain {
	
	private String plkz;
	public String getPlkz() {
		return plkz;
	}
	@XmlElement(name="PLKZ")
	public void setPlkz(String plkz) {
		this.plkz = plkz;
	}
	
	private String key;
	public String getKey() {
		return key;
	}
	@XmlElement(name="KEY")
	public void setKey(String key) {
		this.key = key;
	}
	
	public ArticleCacheDomain() {
		
	}
	
	public ArticleCacheDomain(String plkz, String key) {
		this.plkz = plkz;
		this.key = key;
	}

	@Override
	public String toString() {
		return "ArticleCacheDomain [plkz=" + plkz + ", key=" + key + "]";
	}

}
