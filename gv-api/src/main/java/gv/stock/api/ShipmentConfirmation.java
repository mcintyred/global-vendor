package gv.stock.api;

import org.joda.time.LocalDate;

public class ShipmentConfirmation {
	
	private final Long productId;
	private final LocalDate shipmentDate;
	private final int qty;
	
	// Default constructor needed to keep Jackson happy
	public ShipmentConfirmation() {
		this(null, null, 0);
	}
	
	public ShipmentConfirmation(Long productId, LocalDate shipmentDate, int qty) {
		this.productId = productId;
		this.shipmentDate = shipmentDate;
		this.qty = qty;
	}
	
	public Long getProductId() {
		return productId;
	}
	
	public LocalDate getShipmentDate() {
		return shipmentDate;
	}
	
	public int getQty() {
		return qty;
	}
	
	@Override
	public String toString() {
		return "ShipmentConfirmation [productId=" + productId
				+ ", shipmentDate=" + shipmentDate + ", qty=" + qty + "]";
	}
}
