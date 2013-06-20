package gv.stock.api;

import gv.api.Product;
import gv.api.Warehouse;

public class StockAlertDetails {
	
	private final Product product;
	private final Warehouse warehouse;
	private final int stockLevel;
	private final int threshold;
	public StockAlertDetails(Product product, Warehouse warehouse,
			int stockLevel, int threshold) {
		super();
		this.product = product;
		this.warehouse = warehouse;
		this.stockLevel = stockLevel;
		this.threshold = threshold;
	}
	
	public Product getProduct() {
		return product;
	}
	
	public Warehouse getWarehouse() {
		return warehouse;
	}
	
	public int getStockLevel() {
		return stockLevel;
	}
	
	public int getThreshold() {
		return threshold;
	}
}
