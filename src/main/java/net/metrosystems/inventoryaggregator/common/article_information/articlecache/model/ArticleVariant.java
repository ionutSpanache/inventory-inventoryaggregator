package net.metrosystems.inventoryaggregator.common.article_information.articlecache.model;

import java.util.ArrayList;
import java.util.List;

public class ArticleVariant {
	
	private Long variantNumber;
	private String variantDescription;
	private List<ArticleBundle> bundles;
	
	
	public ArticleVariant() {
		
	}
	
	
	public ArticleVariant(Long variantNumber,
                          String variantDescription,
                          ArticleBundle bundle) {
		this.variantNumber      = variantNumber;
		this.variantDescription = variantDescription;
		this.bundles            = new ArrayList<ArticleBundle>();
		this.bundles.add(bundle);
	}
	
	
	public ArticleVariant(Long variantNumber,
			              String variantDescription,
			              List<ArticleBundle> bundles) {
		
		this.variantNumber      = variantNumber;
		this.variantDescription = variantDescription;
		this.bundles            = bundles;
		
	}


	public Long getVariantNumber() {
		return variantNumber;
	}


	public void setVariantNumber(Long variant) {
		this.variantNumber = variant;
	}


	public String getVariantDescription() {
		return variantDescription;
	}


	public void setVariantDescription(String variantDescription) {
		this.variantDescription = variantDescription;
	}


	public List<ArticleBundle> getBundles() {
		return bundles;
	}


	public void setBundles(List<ArticleBundle> bundles) {
		this.bundles = bundles;
	}


	@Override
	public String toString() {
		return "ArticleVariant [variant=" + variantNumber + ", variantDescription=" + variantDescription + ", bundles="
				+ bundles + "]";
	}
	
	
	public void addArticleBundle(ArticleBundle articleBundle) {
		this.bundles.add(articleBundle);
	}

}
