package net.metrosystems.inventoryaggregator.articleattributes.model;

import java.util.List;

public class ArticleIdTreeStructureList {
	
	private List<ArticleIdTreeStructure> articleIdTreeStructureList;
	
	public ArticleIdTreeStructureList() {
		
	}
	
	public ArticleIdTreeStructureList(List<ArticleIdTreeStructure> articleIdTreeStructureList) {
		this.articleIdTreeStructureList = articleIdTreeStructureList;
	}

	public List<ArticleIdTreeStructure> getArticleIdTreeStructureList() {
		return articleIdTreeStructureList;
	}

	public void setArticleIdTreeStructureList(List<ArticleIdTreeStructure> articleIdTreeStructureList) {
		this.articleIdTreeStructureList = articleIdTreeStructureList;
	}
	
	public Integer findArticleIndex(Long articleNumber) {
		if (this.articleIdTreeStructureList == null) {
			return null;
		}else {
			Integer articleIndex = 0;
			for(ArticleIdTreeStructure articleIdTreeStructure : this.articleIdTreeStructureList) {
				if (articleIdTreeStructure.getArticleNumber().equals(articleNumber)) {
					return articleIndex;
				}
				articleIndex++;
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		return "ArticleIdTreeStructureList [articleIdTreeStructureList=" + articleIdTreeStructureList + "]";
	}

}
