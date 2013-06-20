package gv.stock.api;

/**
 * A POJO to encapsulate the parameters needed to query a Warehouse for a Product's stock level
 * 
 * @author mcintyred
 *
 */
public class StockQueryRequest {
	
	private Long warehouseId;
	private Long productId;
	
	public StockQueryRequest() {
		this(null, null);
	}
	
	public StockQueryRequest(Long warehouseId, Long productId) {
		super();
		this.warehouseId = warehouseId;
		this.productId = productId;
	}
	
	public Long getWarehouseId() {
		return warehouseId;
	}
	
	public Long getProductId() {
		return productId;
	}
}
