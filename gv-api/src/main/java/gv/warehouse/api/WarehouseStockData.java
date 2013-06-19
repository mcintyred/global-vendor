package gv.warehouse.api;

import gv.api.Warehouse;

public class WarehouseStockData {
	
	private final int qty;
	private final Warehouse warehouse;
	public WarehouseStockData(int qty, Warehouse warehouse) {
		super();
		this.qty = qty;
		this.warehouse = warehouse;
	}
	
	public int getQty() {
		return qty;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}

}