package net.metrosystems.inventoryaggregator.common.article_information.articlecache.model;

import net.metrosystems.inventoryaggregator.articleid.model.ArticleId;


public class ArticleBasicData {
	
	private String id;
	private Long   articleNumber;
	private String articleDescription;
	private String assignedStoreSupplierType;
	//private Long   departmentNumber;              // ArticleCache.ReaderDigest.categoryManagementArea
	private String ownBrand;                      // ArticleCache.ArticleCacheDataGroup(id=100).OWNBRANDID
	private Integer              commercialSupplier;
    private ArticleId articleId;
	public ArticleBasicData() {
		
	}

    public ArticleBasicData(String id, Long articleNumber, String articleDescription, String assignedStoreSupplierType,  String ownBrand, Integer commercialSupplier, ArticleId articleId) {
		this.id = id;
		this.articleNumber = articleNumber;
		this.articleDescription = articleDescription;
		this.assignedStoreSupplierType = assignedStoreSupplierType;
		//this.departmentNumber = departmentNumber;
		this.ownBrand = ownBrand;
		this.commercialSupplier = commercialSupplier;
        this.articleId = articleId;
	}

	public ArticleBasicData(String id, Long articleNumber, String articleDescription, String assignedStoreSupplierType,
							 String ownBrand) {
		this.id                        = id;
		this.articleNumber             = articleNumber;
		this.articleDescription        = articleDescription;
		this.assignedStoreSupplierType = assignedStoreSupplierType;
		//this.departmentNumber          = departmentNumber;
		this.ownBrand                  = ownBrand;
    }
	
	public ArticleBasicData(String id,
			                Long articleNumber,
			                String articleDescription) {
		this.id = id;
		this.articleNumber = articleNumber;
		this.articleDescription = articleDescription;
	}

	public Integer getCommercialSupplier() {
		return commercialSupplier;
	}

	public void setCommercialSupplier(Integer commercialSupplier) {
		this.commercialSupplier = commercialSupplier;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getArticleNumber() {
		return articleNumber;
	}

	public void setArticleNumber(Long articleNumber) {
		this.articleNumber = articleNumber;
	}

	public String getArticleDescription() {
		return articleDescription;
	}

	public void setArticleDescription(String articleDescription) {
		this.articleDescription = articleDescription;
	}

	public String getAssignedStoreSupplierType() {
		return assignedStoreSupplierType;
	}

	public void setAssignedStoreSupplierType(String assignedStoreSupplierType) {
		this.assignedStoreSupplierType = assignedStoreSupplierType;
	}

	public String getOwnBrand() {
		return ownBrand;
	}

	public void setOwnBrand(String ownBrand) {
		this.ownBrand = ownBrand;
	}

    public ArticleId getArticleId() {
        return articleId;
    }

    public void setArticleId(ArticleId articleId) {
        this.articleId = articleId;
    }

	@Override
	public String toString() {
        return "ArticleBasicData{" +
                "id='" + id + '\'' +
                ", articleNumber=" + articleNumber +
                ", articleDescription='" + articleDescription + '\'' +
                ", assignedStoreSupplierType='" + assignedStoreSupplierType + '\'' +
                ", ownBrand='" + ownBrand + '\'' +
                ", commercialSupplier=" + commercialSupplier +
                ", articleId=" + articleId +
                '}';
    }
}