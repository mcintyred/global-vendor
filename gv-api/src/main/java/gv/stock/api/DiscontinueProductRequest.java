package gv.stock.api;

public class DiscontinueProductRequest {
	
	private final long warehouseId;
	private final long productId;
	
	public DiscontinueProductRequest(long warehouseId, long productId) {
		this.warehouseId = warehouseId;
		this.productId = productId;
	}
	
	public long getWarehouseId() {
		return warehouseId;
	}
	
	public long getProductId() {
		return productId;
	}
}
