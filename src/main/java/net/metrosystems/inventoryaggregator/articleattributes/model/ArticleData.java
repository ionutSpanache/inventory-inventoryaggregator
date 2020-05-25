package net.metrosystems.inventoryaggregator.articleattributes.model;

import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariant;

import java.util.List;

public class ArticleData {
	
	private String               id;
	private long                 articleNumber;
	private String               articleDescription;
	private String               assignedStoreSupplierType;
	private Integer              commercialSupplier;
	private Long                 departmentNumber;
	private String               ownBrand;
	private List<ArticleVariant> variantList;
	
	public ArticleData() {
		
	}

	public ArticleData(String id, long articleNumber, String articleDescription, String assignedStoreSupplierType, Integer commercialSupplier, Long departmentNumber, String ownBrand, List<ArticleVariant> variantList) {
		this.id = id;
		this.articleNumber = articleNumber;
		this.articleDescription = articleDescription;
		this.assignedStoreSupplierType = assignedStoreSupplierType;
		this.commercialSupplier = commercialSupplier;
		this.departmentNumber = departmentNumber;
		this.ownBrand = ownBrand;
		this.variantList = variantList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getArticleNumber() {
		return articleNumber;
	}

	public void setArticleNumber(long articleNumber) {
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

	public List<ArticleVariant> getVariantList() {
		return variantList;
	}

	public void setVariantList(List<ArticleVariant> variantList) {
		this.variantList = variantList;
	}
	
	public Long getDepartmentNumber() {
		return departmentNumber;
	}

	public void setDepartmentNumber(Long departmentNumber) {
		this.departmentNumber = departmentNumber;
	}

	public String getOwnBrand() {
		return ownBrand;
	}

	public void setOwnBrand(String ownBrand) {
		this.ownBrand = ownBrand;
	}

	public Integer getCommercialSupplier() {
		return commercialSupplier;
	}

	public void setCommercialSupplier(Integer commercialSupplier) {
		this.commercialSupplier = commercialSupplier;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ArticleData{");
		sb.append("id='").append(id).append('\'');
		sb.append(", articleNumber=").append(articleNumber);
		sb.append(", articleDescription='").append(articleDescription).append('\'');
		sb.append(", assignedStoreSupplierType='").append(assignedStoreSupplierType).append('\'');
		sb.append(", commercialSupplier=").append(commercialSupplier);
		sb.append(", departmentNumber=").append(departmentNumber);
		sb.append(", ownBrand=").append(ownBrand);
		sb.append(", variantList=").append(variantList);
		sb.append('}');
		return sb.toString();
	}
}
