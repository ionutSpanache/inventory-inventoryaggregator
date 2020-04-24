package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "IhdsTree", namespace = "http://cpim.metrosystems.net/ihds-tree/1.0/")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleCacheStoreExceptionsIhdsTree {
	@XmlAttribute(name="version")
	private String version;
	@XmlAttribute(name="scope")
	private String scope;
	@XmlElement(name="Transaction")
	ArticleCacheStoreExceptionsTransaction transaction;
}
