package inventory.aggregator.common.article_information.articlecache.model;

public class ArticlePlannedDeliveryDate {
	
	private String plannedDeliveryDate;
	private Float onOrder;
	
	
	public ArticlePlannedDeliveryDate(){
		
	}
	
	
	public ArticlePlannedDeliveryDate(String pPlannedDeliveryDate,
	                           Float  pOnOrder)
	{
		
		this.plannedDeliveryDate = pPlannedDeliveryDate;
		this.onOrder             = pOnOrder;
	  
	}


	public String getPlannedDeliveryDate() {
		return plannedDeliveryDate;
	}


	public void setPlannedDeliveryDate(String plannedDeliveryDate) {
		this.plannedDeliveryDate = plannedDeliveryDate;
	}


	public Float getOnOrder() {
		return onOrder;
	}


	public void setOnOrder(Float onOrder) {
		this.onOrder = onOrder;
	}


	@Override
	public String toString() {
		return "PlannedDeliveryDate [plannedDeliveryDate=" + plannedDeliveryDate + ", onOrder=" + onOrder + "]";
	}
	
	
	

}
