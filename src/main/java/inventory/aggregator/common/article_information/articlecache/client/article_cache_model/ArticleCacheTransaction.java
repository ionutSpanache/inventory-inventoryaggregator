package inventory.aggregator.common.article_information.articlecache.client.article_cache_model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="Transaction")
public class ArticleCacheTransaction implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7435185807928567107L;
	
	private String time;
	public String getTime() {
		return time;
	}
	
	@XmlAttribute(name="time")
	public void setTime(String time) {
		this.time = time;
	}
	
	private String id;
	public String getId() {
		return id;
	}
	
	@XmlAttribute(name="id")
	public void setId(String id) {
		this.id = id;
	}
	
	public ArticleCacheTransaction() {
		
	}
	
	public ArticleCacheTransaction(String time, String id, ArticleCacheIncident articleCacheIncident) {
		super();
		this.time = time;
		this.id = id;
		this.articleCacheIncident = articleCacheIncident;
	}

	private ArticleCacheIncident articleCacheIncident;
	public ArticleCacheIncident getArticleCacheIncident() {
		return articleCacheIncident;
	}
	
	@XmlElement(name="Incident",namespace = "http://cpim.metrosystems.net/ihds-tree/1.0/")
	public void setArticleCacheIncidents(ArticleCacheIncident articleCacheIncident) {
		this.articleCacheIncident = articleCacheIncident;
	}
	
	@Override
	public String toString() {
		return "ArticleCacheTransaction [time=" + time + ", id=" + id + ", articleCacheIncident=" + articleCacheIncident + "]";
	}

}