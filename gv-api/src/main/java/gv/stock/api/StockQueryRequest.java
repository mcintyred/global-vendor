package gv.stock.api;

/**
 * A POJO to encapsulate the parameters needed to query a Warehouse for a Product's stock level
 * 
 * @author mcintyred
 *
 */
public class StockQueryRequest {
	
	private String warehouseName;
	private Long productId;
	
	public StockQueryRequest() {
		this(null, null);
	}
	
	public StockQueryRequest(String warehouseName, Long productId) {
		super();
		this.warehouseName = warehouseName;
		this.productId = productId;
	}
	
	public String getWarehouseName() {
		return warehouseName;
	}
	
	public Long getProductId() {
		return productId;
	}
}
