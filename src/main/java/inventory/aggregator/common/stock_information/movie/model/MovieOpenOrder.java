package inventory.aggregator.common.stock_information.movie.model;

public class MovieOpenOrder {
	
	private String deliveryDate;
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	private Float quantity;
	public Float getQuantity() {
		return quantity;
	}
	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}
	
	private String orderDate;
	
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public MovieOpenOrder() {
		
	}
	
	public MovieOpenOrder(String deliveryDate, Float quantity, String orderDate) {
		super();
		this.deliveryDate = deliveryDate;
		this.quantity = quantity;
		this.orderDate = orderDate;
	}
	@Override
	public String toString() {
		return "MovieOpenOrder [deliveryDate=" + deliveryDate + ", quantity=" + quantity + ", orderDate=" + orderDate
				+ "]";
	}
	


}
