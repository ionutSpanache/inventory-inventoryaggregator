package net.metrosystems.inventoryaggregator.articleattributes.model;

import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;

public class ArticleIdTreeStructure{
	
	private Long articleNumber;
	private ArticleComponent articleComponent;
	
	public ArticleIdTreeStructure() {
		
	}
	
	public ArticleIdTreeStructure(Long articleNumber,
			                      ArticleComponent articleComponent) {
		this.articleNumber = articleNumber;
		this.articleComponent = articleComponent;
	}

	public Long getArticleNumber() {
		return articleNumber;
	}

	public void setArticleNumber(Long articleNumber) {
		this.articleNumber = articleNumber;
	}

	public ArticleComponent getArticleComponent() {
		return articleComponent;
	}

	public void setArticleComponent(ArticleComponent articleComponent) {
		this.articleComponent = articleComponent;
	} 

	@Override
	public String toString() {
		return "ArticleIdTreeStructure [articleNumber=" + articleNumber + ", articleComponent=" + articleComponent
				+ "]";
	}
}