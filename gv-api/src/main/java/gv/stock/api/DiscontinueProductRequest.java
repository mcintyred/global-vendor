package gv.stock.api;

public class DiscontinueProductRequest {
	
	private final String warehouseName;
	private final long productId;
	
	public DiscontinueProductRequest() {
		this(null, 0);
	}
	
	public DiscontinueProductRequest(String warehouseName, long productId) {
		this.warehouseName = warehouseName;
		this.productId = productId;
	}
	
	public String getWarehouseName() {
		return warehouseName;
	}
	
	public long getProductId() {
		return productId;
	}
}
