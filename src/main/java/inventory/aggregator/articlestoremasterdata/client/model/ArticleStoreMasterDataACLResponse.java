package inventory.aggregator.articlestoremasterdata.client.model;

import inventory.aggregator.articlestoremasterdata.model.ArticleStoreMasterData;

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
