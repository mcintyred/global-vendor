package gv.stock.api;
/**
 * A POJO to encapsulate the parameters needed to issue a request to a
 * Warehouse to set or update the stock level for a Product
 * 
 * @author mcintyred
 *
 */
public class StockChangeRequest {
	
	private String warehouseName;
	private Long productId;
	private int qty;
	
	public StockChangeRequest() {
		this(null, null, 0);
	}
	
	public StockChangeRequest(String warehouseName, Long productId, int qty) {
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
