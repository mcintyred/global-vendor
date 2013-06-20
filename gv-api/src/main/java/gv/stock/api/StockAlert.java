package gv.stock.api;


public class StockAlert {
	
	private final long productId;
	private final long warehouseId;
	private final int stockLevel;
	private final int threshold;
	
	public StockAlert() {
		this(0,0,0,0);
	}
	
	
	public StockAlert(long productId, long warehouseId, int stockLevel, int threshold) {
		this.productId = productId;
		this.warehouseId = warehouseId;
		this.stockLevel = stockLevel;
		this.threshold = threshold;
	}


	public long getProductId() {
		return productId;
	}


	public long getWarehouseId() {
		return warehouseId;
	}


	public int getStockLevel() {
		return stockLevel;
	}


	public int getThreshold() {
		return threshold;
	}
}
