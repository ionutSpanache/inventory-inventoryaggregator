package net.metrosystems.inventoryaggregator.articleattributes.model;

public class ArtAttributes {
	
	private ArticleData articleData;
	
	public ArtAttributes(ArticleData articleData) {
		this.articleData = articleData;
	}

	public ArticleData getArtAttributes() {
		return articleData;
	}

	public void setArtAttributes(ArticleData articleData) {
		this.articleData = articleData;
	}

	@Override
	public String toString() {
		return "ArtAttributes [articleForUI=" + articleData + "]";
	}

}
