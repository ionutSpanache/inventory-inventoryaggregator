package net.metrosystems.inventoryaggregator.articleattributes.model;

import java.util.List;

public class ArticleDataFromMovie {
	StockInformation articleStockData;
	List<ArticlePlannedDeliveryDate> articlePlannedDeliveryDates;
	
	public ArticleDataFromMovie() {
		
	}

	public ArticleDataFromMovie(StockInformation articleStockData,
			List<ArticlePlannedDeliveryDate> articlePlannedDeliveryDates) {
		this.articleStockData = articleStockData;
		this.articlePlannedDeliveryDates = articlePlannedDeliveryDates;
	}

	public StockInformation getArticleStockData() {
		if (this.articleStockData != null) {
			return articleStockData;
		}
		return null;
	}

	public void setArticleStockData(StockInformation articleStockData) {
		this.articleStockData = articleStockData;
	}

	public List<ArticlePlannedDeliveryDate> getArticlePlannedDeliveryDates() {
		if (this.articlePlannedDeliveryDates != null) {
			return articlePlannedDeliveryDates;
		}
		return null;
	}

	public void setArticlePlannedDeliveryDates(List<ArticlePlannedDeliveryDate> articlePlannedDeliveryDates) {
		this.articlePlannedDeliveryDates = articlePlannedDeliveryDates;
	}

	@Override
	public String toString() {
		return "ArticleDataFromMovie [articleStockData=" + articleStockData + ", articlePlannedDeliveryDates="
				+ articlePlannedDeliveryDates + ", getArticleStockData()=" + getArticleStockData()
				+ ", getArticlePlannedDeliveryDates()=" + getArticlePlannedDeliveryDates() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}


}
