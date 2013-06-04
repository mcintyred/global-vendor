package gv.warehouse.api;
/**
 * A POJO to encapsulate the parameters needed to issue a request to a
 * Warehouse to ship a Product
 * 
 * @author mcintyred
 *
 */
public class ShipmentRequest {
	
	private Long warehouseId;
	private Long productId;
	private int qty;
	
	public ShipmentRequest() {
		this(null, null, 0);
	}
	
	public ShipmentRequest(Long warehouseId, Long productId, int qty) {
		this.warehouseId = warehouseId;
		this.productId = productId;
		this.qty = qty;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public Long getProductId() {
		return productId;
	}

	public int getQty() {
		return qty;
	}

}
