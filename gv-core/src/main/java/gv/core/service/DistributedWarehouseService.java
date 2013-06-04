package gv.core.service;

import gv.api.Warehouse;
import gv.core.StockAlertDetails;
import gv.core.WarehouseStockData;
import gv.warehouse.api.ShipmentConfirmation;
import gv.warehouse.api.ShipmentRequest;
import gv.warehouse.api.StockAlert;
import gv.warehouse.api.StockAlertListener;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.StockQueryRequest;

import java.util.List;
import java.util.Set;

public interface DistributedWarehouseService extends StockAlertListener {
	
	Set<Warehouse> listWarehouses();
	Warehouse getWarehouseById(Long warehouseId);
	
	/**
	 * Request a Warehouse to update the stock level for the product specified in the request
	 * @return int The new total stock level for the product in the Warehouse
	 */
	int updateStock(StockChangeRequest request);
	
	/**
	 * Request a Warehouse to set the stock level for the product specified in the request
	 */
	void setStock(StockChangeRequest request);
	
	/**
	 * Return the total amount of stock of the given product in the Warehouse
	 * 
	 * @return int The total stock of the product in the Warehouse
	 */
	int getStockInWarehouse(StockQueryRequest request);
	
	/**
	 * Returns a list of all warehouses holding stock of the given product
	 */
	public List<WarehouseStockData> getStockData(Long productId);
	
	public ShipmentConfirmation requestShipment(ShipmentRequest request);
	
	public List<StockAlertDetails> getStockAlerts();
}
