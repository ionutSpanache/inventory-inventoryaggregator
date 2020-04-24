package net.metrosystems.inventoryaggregator.rawarticledata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArticleComponent implements Serializable {

	private static final long serialVersionUID = 3693335627202195526L;

	public class ArticleBundle implements Serializable {
		private static final long serialVersionUID = 4588860858277558630L;
		Long bundleNumber;
		
		public ArticleBundle() {
			
		}
		
		public ArticleBundle(Long bundleNumber) {
			this.bundleNumber = bundleNumber;
		}
		
		@JsonIgnore
		public long getSerialversionuid() {
			return serialVersionUID;
		}

		public Long getBundleNumber() {
			return bundleNumber;
		}

		public void setBundleNumber(Long bundleNumber) {
			this.bundleNumber = bundleNumber;
		}

		@Override
		public String toString() {
			return "ArticleBundle [BundleNumber=" + bundleNumber + "]";
		}
	}
	
	public class ArticleVariant implements Serializable {
		private static final long serialVersionUID = 5731686138457880568L;
		private Long VariantNumber;
		private List<ArticleBundle> bundleList;
		
		public ArticleVariant(){
			
		}
		
		public ArticleVariant(Long variantNumber, Long bundleNumber) {
			this.VariantNumber = variantNumber;
			this.bundleList = new ArrayList<ArticleBundle>(Arrays.asList(new ArticleBundle(bundleNumber)));
		}

		@JsonIgnore
		public long getSerialversionuid() {
			return serialVersionUID;
		}

		public Long getVariantNumber() {
			return VariantNumber;
		}

		public void setVariantNumber(Long variantNumber) {
			VariantNumber = variantNumber;
		}

		public List<ArticleBundle> getBundleList() {
			return bundleList;
		}

		public void setBundleList(List<ArticleBundle> bundleList) {
			this.bundleList = bundleList;
		}
		
		public Integer findBundleIndexByBundleNumber(Long bundleNumber) {
			if(this.getBundleList() == null) {
				return null;
			}else {
				Integer bundleIndex = 0;
				for(ArticleBundle articleBundle : this.getBundleList()) {
					if (articleBundle.getBundleNumber().equals(bundleNumber)){
						return bundleIndex;
					}
					bundleIndex++;
				}
			}
			return -1;
		}
		
		public void addBundle(Long bundleNumber) {
			Integer bundleIndex = findBundleIndexByBundleNumber(bundleNumber);
			if (bundleIndex == null) {
				this.setBundleList(new ArrayList<ArticleBundle>(Arrays.asList(new ArticleBundle(bundleNumber))));
			}else if (bundleIndex == -1) {
				this.getBundleList().add(new ArticleBundle(bundleNumber));
			}
		}

		@Override
		public String toString() {
			return "ArticleVariant [VariantNumber=" + VariantNumber + ", bundeList=" + bundleList + "]";
		}
	}
	
	
	private List<ArticleVariant> variantList;
	
	public ArticleComponent() {
		
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<ArticleVariant> getVariantList() {
		return variantList;
	}

	public void setVariantList(List<ArticleVariant> variantList) {
		this.variantList = variantList;
	}
	
	private Integer findVariantIndex(Long variantNumber) {
		if (this.variantList == null) {
			return null;
		}else {
			Integer variantIndex = 0;
			for(ArticleVariant articleVariant : variantList) {
				if (articleVariant.getVariantNumber()==variantNumber) {
					return variantIndex;
				}
				variantIndex++;
			}
		}
		return -1;
	}
	
	public void addVariantBundle(
			Long variantNumber,
			Long bundleNumber) {
		Integer variantIndex = findVariantIndex(variantNumber);
		if (variantIndex == null) {
			this.setVariantList(new ArrayList<ArticleVariant>(Arrays.asList(new ArticleVariant(variantNumber, bundleNumber))));
		}else if (variantIndex.intValue() == -1) {
				ArticleVariant articleVariant = new ArticleVariant(variantNumber, bundleNumber);
				this.variantList.add(articleVariant);
		}else {
				this.variantList.get(variantIndex).addBundle(bundleNumber);
		}
	}

	@Override
	public String toString() {
		return "ArticleComponent [variantList=" + variantList + "]";
	}
}
