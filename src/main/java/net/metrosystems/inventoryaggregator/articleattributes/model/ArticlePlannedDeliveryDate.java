package net.metrosystems.inventoryaggregator.articleattributes.model;

public class ArticlePlannedDeliveryDate {
	private Float quantity;
	private String deliveryDate;
	private String orderDate;
	
	public ArticlePlannedDeliveryDate() {
		
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public ArticlePlannedDeliveryDate(Float quantity, String deliveryDate, String orderDate) {
		this.quantity = quantity;
		this.deliveryDate = deliveryDate;
		this.orderDate = orderDate;
	}

	@Override
	public String toString() {
		return "ArticlePlannedDeliveryDate [quantity=" + quantity + ", deliveryDate=" + deliveryDate + ", orderDate="
				+ orderDate + "]";
	}
	
	
}
