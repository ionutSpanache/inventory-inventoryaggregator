package inventory.aggregator.common.article_information.articlecache.client.article_cache_model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="IhdsTree")	
public class ArticleCacheIhdsTree implements Serializable {
	
	private static final long serialVersionUID = -5615815893816543061L;
		
	ArticleCacheTransaction transaction;
		
	public ArticleCacheTransaction getTransaction() {
		return transaction;
	}		
	
	@XmlElement(name="Transaction")
	public void setTransaction(ArticleCacheTransaction transaction) {
		this.transaction = transaction;
	}
		
	@Override
	public String toString() {
		return "ArticleCacheIhdsTree [transaction=" + transaction + "]";
	}		

}
