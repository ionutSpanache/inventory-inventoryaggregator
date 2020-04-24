package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="Incident")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleCacheStoreExceptionsIncident {
	@XmlAttribute(name="id")
	private int id;
	@XmlElement(name="Domain")
	private ArticleCacheStoreExceptionsDomain articleCacheStoreExceptionsDomain;
	@XmlElement(name="DataGroup")
	private List<ArticleCacheStoreExceptionsDataGroup> articleCacheStoreExceptionsDataGroups;

	public ArticleCacheStoreExceptionsDataGroup getArticleCacheStoreExceptionsDataGroupById(int dataGroupId){
		try {
			return this.articleCacheStoreExceptionsDataGroups.stream()
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == dataGroupId))
					.collect(Collectors.toList())
					.get(0);
		}catch(Exception e){
			return new ArticleCacheStoreExceptionsDataGroup();
		}
	}
}
