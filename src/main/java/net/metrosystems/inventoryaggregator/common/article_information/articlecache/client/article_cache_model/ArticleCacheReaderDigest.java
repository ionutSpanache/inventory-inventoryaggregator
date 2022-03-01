package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="ReaderDigest")
public class ArticleCacheReaderDigest {
	
	private long purchaseArea;
	public long getPurchaseArea() {
		return purchaseArea;
	}
	@XmlElement(name="purchaseArea")
	public void setPurchaseArea(long purchaseArea) {
		this.purchaseArea = purchaseArea;
	}
	
	private Long merchGroup;
	public Long getMerchGroup() {
		return merchGroup;
	}
	@XmlElement(name="merchGroup")
	public void setMerchGroup(Long merchGroup) {
		this.merchGroup = merchGroup;
	}
	
	private long merchGroupSub;
	public long getMerchGroupSub() {
		return merchGroupSub;
	}
	@XmlElement(name="merchGroupSub")
	public void setMerchGroupSub(long merchGroupSub) {
		this.merchGroupSub = merchGroupSub;
	}
	
	private long merchGroupMain;
	public long getMerchGroupMain() {
		return merchGroupMain;
	}
	@XmlElement(name="merchGroupMain")
	public void setMerchGroupMain(long merchGroupMain) {
		this.merchGroupMain = merchGroupMain;
	}
		
	private String articleDescription;
	public String getArticleDescription() {
		return articleDescription;
	}

	@XmlElement(name="articleDescription")
	public void setArticleDescription(String articleDescription) {
		this.articleDescription = articleDescription;
	}
	
	private String brandName;
	public String getBrandName() {
		return brandName;
	}
	@XmlElement(name="brandName")
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	private String indicatorFoodNonFood;
	public String getIndicatorFoodNonFood() {
		return indicatorFoodNonFood;
	}
	@XmlElement(name="indicatorFoodNonFood")
	public void setIndicatorFoodNonFood(String indicatorFoodNonFood) {
		this.indicatorFoodNonFood = indicatorFoodNonFood;
	}
	
	private long categoryManagementArea;
	public long getCategoryManagementArea() {
		return categoryManagementArea;
	}
	@XmlElement(name="categoryManagementArea")
	public void setCategoryManagementArea(long categoryManagementArea) {
		this.categoryManagementArea = categoryManagementArea;
	}
	
	private String variantText;
	public String getVariantText() {
		return variantText;
	}
	@XmlElement(name="variantText")
	public void setVariantText(String variantText) {
		this.variantText = variantText;
	}
	
	private Long mmsArtNo;
	public Long getMmsArtNo() {
		return mmsArtNo;
	}
	@XmlElement(name="mmsArtNo")
	public void setMmsArtNo(Long mmsArtNo) {
		this.mmsArtNo = mmsArtNo;
	}
	
	private Long variantNo;
	public Long getVariantNo() {
		return variantNo;
	}
	@XmlElement(name="variantNo")
	public void setVariantNo(Long variantNo) {
		this.variantNo = variantNo;
	}
	
	private Long bundleNo;
	public Long getBundleNo() {
		return bundleNo;
	}
	@XmlElement(name="bundleNo")
	public void setBundleNo(Long bundleNo) {
		this.bundleNo = bundleNo;
	}
	
	private Long subsysNo;
	public Long getSubsysNo() {
		return subsysNo;
	}
	@XmlElement(name="subsysNo")
	public void setSubsysNo(Long subsysNo) {
		this.subsysNo = subsysNo;
	}
	
	private Integer producingSupplier;
	public Integer getProducingSupplier() {
		return producingSupplier;
	}
	@XmlElement(name="producingSupplier")
	public void setProducingSupplier(Integer producingSupplier) {
		this.producingSupplier = producingSupplier;
	}
	
	private long articleType;
	public long getArticleType() {
		return articleType;
	}
	@XmlElement(name="articleType")
	public void setArticleType(long articleType) {
		this.articleType = articleType;
	}
	
	private List<String> salesBarcode;
	public List<String> getSalesBarcode() {
		return salesBarcode;
	}
	@XmlElement(name="salesBarcode")
	public void setSalesBarcode(List<String> salesBarcode) {
		this.salesBarcode = salesBarcode;
	}
	
	private List<Long> assignedStoreSupplier;
	public List<Long> getAssignedStoreSupplier() {
		return assignedStoreSupplier;
	}
	@XmlElement(name="assignedStoreSupplier")
	public void setAssignedStoreSupplier(List<Long> assignedStoreSupplier) {
		this.assignedStoreSupplier = assignedStoreSupplier;
	}
	
	private Long blockingCode;
	public Long getBlockingCode() {
		return blockingCode;
	}
	@XmlElement(name="blockingCode")
	public void setBlockingCode(Long blockingCode) {
		this.blockingCode = blockingCode;
	}
	
	private String mainSalesBarcode;
	public String getMainSalesBarcode() {
		return mainSalesBarcode;
	}
	@XmlElement(name="mainSalesBarcode")
	public void setMainSalesBarcode(String mainSalesBarcode) {
		this.mainSalesBarcode = mainSalesBarcode;
	}

	private Long traceabilityIndicator;
	public Long getTraceabilityIndicator() {
		return traceabilityIndicator;
	}
	@XmlElement(name="traceabilityIndicator")
	public void setTraceabilityIndicator(Long traceabilityIndicator) {
		this.traceabilityIndicator = traceabilityIndicator;
	}
	
	public ArticleCacheReaderDigest() {
		
	}
	
	public ArticleCacheReaderDigest(long purchaseArea, long merchGroup, long merchGroupSub, long merchGroupMain,
			String articleDescription, String brandName, String indicatorFoodNonFood, long categoryManagementArea,
			String variantText, long mmsArtNo, long variantNo, long bundleNo, long subsysNo, Integer producingSupplier,
			long articleType, List<String> salesBarcode, List<Long> assignedStoreSupplier, long blockingCode,
			String mainSalesBarcode, long traceabilityIndicator) {
		this.purchaseArea = purchaseArea;
		this.merchGroup = merchGroup;
		this.merchGroupSub = merchGroupSub;
		this.merchGroupMain = merchGroupMain;
		this.articleDescription = articleDescription;
		this.brandName = brandName;
		this.indicatorFoodNonFood = indicatorFoodNonFood;
		this.categoryManagementArea = categoryManagementArea;
		this.variantText = variantText;
		this.mmsArtNo = mmsArtNo;
		this.variantNo = variantNo;
		this.bundleNo = bundleNo;
		this.subsysNo = subsysNo;
		this.producingSupplier = producingSupplier;
		this.articleType = articleType;
		this.salesBarcode = salesBarcode;
		this.assignedStoreSupplier = assignedStoreSupplier;
		this.blockingCode = blockingCode;
		this.mainSalesBarcode = mainSalesBarcode;
		this.traceabilityIndicator = traceabilityIndicator;
	}

	@Override
	public String toString() {
		return "ArticleCacheReaderDigest [purchaseArea=" + purchaseArea + ", merchGroup=" + merchGroup
				+ ", merchGroupSub=" + merchGroupSub + ", merchGroupMain=" + merchGroupMain + ", articleDescription="
				+ articleDescription + ", brandName=" + brandName + ", indicatorFoodNonFood=" + indicatorFoodNonFood
				+ ", categoryManagementArea=" + categoryManagementArea + ", variantText=" + variantText + ", mmsArtNo="
				+ mmsArtNo + ", variantNo=" + variantNo + ", bundleNo=" + bundleNo + ", subsysNo=" + subsysNo
				+ ", producingSupplier=" + producingSupplier + ", articleType=" + articleType + ", salesBarcode="
				+ salesBarcode + ", assignedStoreSupplier=" + assignedStoreSupplier + ", blockingCode=" + blockingCode
				+ ", traceabilityIndicator=" + traceabilityIndicator
				+ "]";
	}
}
