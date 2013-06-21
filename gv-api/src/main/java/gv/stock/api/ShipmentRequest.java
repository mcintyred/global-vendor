package gv.stock.api;
/**
 * A POJO to encapsulate the parameters needed to issue a request to a
 * Warehouse to ship a Product
 * 
 * @author mcintyred
 *
 */
public class ShipmentRequest {
	
	private String warehouseName;
	private Long productId;
	private int qty;
	
	public ShipmentRequest() {
		this(null, null, 0);
	}
	
	public ShipmentRequest(String warehouseName, Long productId, int qty) {
		this.warehouseName = warehouseName;
		this.productId = productId;
		this.qty = qty;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public Long getProductId() {
		return productId;
	}

	public int getQty() {
		return qty;
	}

}
