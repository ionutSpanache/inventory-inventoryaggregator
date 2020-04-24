package net.metrosystems.inventoryaggregator.articlestoremasterdata.client.model;

import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;

import java.util.ArrayList;
import java.util.List;

public class ArticleStoreMasterDataACLResponse {
	
	private List<ArticleStoreMasterData> articleStoreMasterData;

	public ArticleStoreMasterDataACLResponse() {
		articleStoreMasterData = new ArrayList<>();
	}

	public List<ArticleStoreMasterData> getArticleStoreMasterData() {
		return articleStoreMasterData;
	}

	public void setArticleStoreMasterData(List<ArticleStoreMasterData> articleStoreMasterData) {
		this.articleStoreMasterData = articleStoreMasterData;
	}

	

}
