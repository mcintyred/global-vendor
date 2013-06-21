package gv.stock.api;


public class StockAlert {
	
	private final long productId;
	private final String warehouseName;
	private final int stockLevel;
	private final int threshold;
	
	public StockAlert() {
		this(0,null,0,0);
	}
	
	
	public StockAlert(long productId, String warehouseName, int stockLevel, int threshold) {
		this.productId = productId;
		this.warehouseName = warehouseName;
		this.stockLevel = stockLevel;
		this.threshold = threshold;
	}


	public long getProductId() {
		return productId;
	}


	public String getWarehouseName() {
		return warehouseName;
	}


	public int getStockLevel() {
		return stockLevel;
	}


	public int getThreshold() {
		return threshold;
	}
}
