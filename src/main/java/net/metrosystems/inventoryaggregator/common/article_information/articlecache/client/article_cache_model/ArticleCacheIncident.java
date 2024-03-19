package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model;

import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name="Incident")
public class ArticleCacheIncident {
	
	private int id;
	public int getId() {
		return id;
	}
	@XmlAttribute(name="id")
	public void setId(int id) {
		this.id = id;
	}
	
	private ArticleCacheDomain articleCacheDomain;
	public ArticleCacheDomain getArticleCacheDomain() {
		return articleCacheDomain;
	}
	@XmlElement(name="Domain")
	public void setArticleCacheDomain(ArticleCacheDomain articleCacheDomain) {
		this.articleCacheDomain = articleCacheDomain;
	}
	
	private ArticleCacheReaderDigest articleCacheReaderDigest;
	public ArticleCacheReaderDigest getArticleCacheReaderDigest() {
		return articleCacheReaderDigest;
	}
	@XmlElement(name="ReaderDigest")
	public void setArticleCacheReaderDigest(ArticleCacheReaderDigest articleCacheReaderDigest) {
		this.articleCacheReaderDigest = articleCacheReaderDigest;
	}
	
	private List<ArticleCacheDataGroup> articleCacheDataGroups;
	public List<ArticleCacheDataGroup> getArticleCacheDataGroup() {
		return articleCacheDataGroups;
	}
	@XmlElement(name="DataGroup")
	public void setArticleCacheDataGroup(List<ArticleCacheDataGroup> articleCacheDataGroups) {
		this.articleCacheDataGroups = articleCacheDataGroups;
	}
	
	public String getBarcodeForBundle() {
		String barcodeForBundle = new String();
		if(  (this.getArticleCacheReaderDigest().getMainSalesBarcode() != null)
			&& (this.getArticleCacheReaderDigest().getMainSalesBarcode().length() > 0)){
				barcodeForBundle = this.getArticleCacheReaderDigest().getMainSalesBarcode();
        } else if (this.getArticleCacheReaderDigest().getSalesBarcode() != null) {
            //todo getSingleItemSalesBarcode este acelasi pentru 3 variante cauta primul din sales barecode in reader digest. o sa avem logica noua
            barcodeForBundle = this.getArticleCacheReaderDigest().getSalesBarcode().get(0);
        } else {
			List<ArticleCacheDataGroup> articleCacheDataGroupList200 = this.getArticleCacheDataGroup().stream()
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId()==200))
					.toList();
			try {
				if(  (!articleCacheDataGroupList200.isEmpty())
						&&(!articleCacheDataGroupList200.get(0).getSingleItemSalesBarcode().isEmpty())) {
							barcodeForBundle = articleCacheDataGroupList200.get(0).getSingleItemSalesBarcode().get(0);
					}
			}catch (Exception e){
				barcodeForBundle = "";
			}
		}
		return barcodeForBundle;
	}
	
	public String getAssignedStoreSupplierTypeForArticle(int storeNo) {
		try {
			return this.getArticleCacheDataGroup().stream()
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 320))
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getStoreNo() == storeNo))
					.toList()
					.get(0).getAssignedStoreSupplierType();
		}catch(Exception e) {
			return null;
		}

	}
	
	public Integer getHierarchySequenceStoreNo(int storeNo) {
		try {
			Integer result = this.getArticleCacheDataGroup().stream()
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 320))
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getStoreNo() == storeNo))
					.toList()
					.get(0).getHierarchySequenceStore();
			if (result == null) {
				throw new ArticleNotFoundException();
			}
			return result;
		}catch(Exception e) {
			return null;
		}

	}
	
	public Byte getBundleProcurementCodeForStore(int storeNo) {
		try {
			return this.getArticleCacheDataGroup().stream()
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 325))
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getHierarchySequenceStoreRef() == storeNo))
					.toList()
					.get(0).getProcurementCd();
		}catch (Exception e) {
			return null;
		}

	}
	
	public Byte getBundleBlockingCodeForStore(int storeNo) {
		try {
			return this.getArticleCacheDataGroup().stream()
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 325))
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getHierarchySequenceStoreRef() == storeNo))
					.toList()
					.get(0).getBlockingCode();
		}catch (Exception e) {
			return null;
		}

	}
	
	public String getPromotionIdForStore(int storeNo) {
		try {
			return this.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 320))
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getHierarchySequenceStore() == storeNo))
						.toList()
						.get(0).getPromotionId();
		}catch(Exception e){
			return null;
		}
	}
	
	public String getOwnBrand() {
		try {
			return this.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 100))
						.toList()
						.get(0).getOwnBrandId();
		}catch(Exception e){
			return "";   // in case of missing or null data from ArticleCache, we assume the article is not own brand
		}
	}
	
	public Integer getMerchandiseCode() {
		try {
			return this.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 100))
						.toList()
						.get(0).getPresentationMerchandiseGroup();
		}catch(Exception e){
			return 0;   // in case of missing or null data from ArticleCache
		}
	}
	
	public Byte getSellingBundleIndicator() {
		try {
			return this.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 300))
						.toList()
						.get(0).getSalesBundleIndicator();
		}catch(Exception e){
			return null;
		}
	}
	
	public Integer getSellingBundleContent() {
		try {
			return this.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 310))
						.toList()
						.get(0).getSellingBundleContent();
		}catch(Exception e){
			return null;
		}
	}
	
	public Byte getPriceCheck() {
		try {
			return this.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 310))
						.toList()
						.get(0).getPriceCheckCodeMccSalesLine();
		}catch(Exception e){
			return null;
		}
	}
	
	public Byte getStatusForStore(int storeNo) {
		try {
			return this.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 320))
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getHierarchySequenceStore() == storeNo))
						.toList()
						.get(0).getActivationStatus();
		}catch(Exception e){
			return null;
		}
	}

	public Boolean getWeightArticle(int storeNo) {
		try {
			return this.getArticleCacheDataGroup().stream()
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 320))
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getHierarchySequenceStore() == storeNo))
					.toList()
					.get(0).getWeightArticle();
		} catch (Exception e) {
			return null;
		}
	}


	public ArticleCacheDataGroup getArticleCacheDataGroupWithStore(int dataGroupId, int storeNo){
		try {
			return this.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == dataGroupId))
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getHierarchySequenceStore() == storeNo))
						.toList()
						.get(0);
		}catch(Exception e){
			return new ArticleCacheDataGroup();
		}
	}

	public List<String> getPromotionIdListWithStore(int dataGroupId, int storeNo){
		try {
			return this.getArticleCacheDataGroup().stream()
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == dataGroupId))
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getHierarchySequenceStore() == storeNo))
					.map(ArticleCacheDataGroup::getPromotionId)
					.toList();
		}catch(Exception e){
			return Collections.emptyList();
		}
	}


	public ArticleCacheDataGroup getArticleCacheDataGroupWithStoreRef(int dataGroupId, int storeNo){
		try {
			return this.getArticleCacheDataGroup().stream()
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == dataGroupId))
					.filter(articleCacheDataGroup -> (articleCacheDataGroup.getHierarchySequenceStoreRef() == storeNo))
					.toList()
					.get(0);
		}catch(Exception e){
			return new ArticleCacheDataGroup();
		}
	}
	
	public ArticleCacheDataGroup getArticleCacheDataGroupWithoutStore(int dataGroupId){
		try {
			return this.getArticleCacheDataGroup().stream()
						.filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == dataGroupId))
						.toList()
						.get(0);
		}catch(Exception e){
			return new ArticleCacheDataGroup();
		}
	}

	public ArticleCacheIncident() {
		
	}
	
	public ArticleCacheIncident(int id, ArticleCacheDomain articleCacheDomain,
			ArticleCacheReaderDigest articleCacheReaderDigest, List<ArticleCacheDataGroup> articleCacheDataGroups) {
		super();
		this.id = id;
		this.articleCacheDomain = articleCacheDomain;
		this.articleCacheReaderDigest = articleCacheReaderDigest;
		this.articleCacheDataGroups = articleCacheDataGroups;
	}
	
	@Override
	public String toString() {
		return "ArticleCacheIncident [id=" + id + ", articleCacheDomain=" + articleCacheDomain
				+ ", articleCacheReaderDigest=" + articleCacheReaderDigest + ", articleCacheDataGrousp="
				+ articleCacheDataGroups + "]";
	}

}
