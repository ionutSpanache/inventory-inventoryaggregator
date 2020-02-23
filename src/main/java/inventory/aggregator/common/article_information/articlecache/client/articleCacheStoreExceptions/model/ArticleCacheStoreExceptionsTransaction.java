package inventory.aggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="Transaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleCacheStoreExceptionsTransaction {
	@XmlAttribute(name="time")
	private String time;
	@XmlAttribute(name="id")
	private String id;
	@XmlElement(name="Incident")
	private ArticleCacheStoreExceptionsIncident articleCacheIncident;
}