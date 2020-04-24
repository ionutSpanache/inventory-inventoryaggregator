package net.metrosystems.inventoryaggregator.common.article_information.articlecache.model;

public class ArticleVariantBundleIdentifier {
	
	private Long articleNumber;
	private Long variantNumber;
	private Long bundleNumber;
	
	public ArticleVariantBundleIdentifier() {
		
	}
	
	public ArticleVariantBundleIdentifier(
			Long articleNumber,
			Long variantNumber,
			Long bundleNumber) {
		this.articleNumber = articleNumber;
		this.variantNumber = variantNumber;
		this.bundleNumber = bundleNumber;
	}

	public Long getArticleNumber() {
		return articleNumber;
	}

	public void setArticleNumber(Long articleNumber) {
		this.articleNumber = articleNumber;
	}

	public Long getVariantNumber() {
		return variantNumber;
	}

	public void setVariantNumber(Long variantNumber) {
		this.variantNumber = variantNumber;
	}

	public Long getBundleNumber() {
		return bundleNumber;
	}

	public void setBundleNumber(Long bundleNumber) {
		this.bundleNumber = bundleNumber;
	}

	@Override
	public String toString() {
		return "ArticleVariantBundleIdentifier [articleNumber=" + articleNumber + ", variantNumber=" + variantNumber
				+ ", bundleNumber=" + bundleNumber + "]";
	}

}
