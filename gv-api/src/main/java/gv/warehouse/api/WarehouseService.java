package gv.warehouse.api;

import gv.api.Shipment;
import gv.stock.api.DiscontinueProductRequest;
import gv.stock.api.ShipmentConfirmation;
import gv.stock.api.ShipmentRequest;
import gv.stock.api.StockChangeRequest;
import gv.stock.api.StockQueryRequest;

public interface WarehouseService {
	
	int updateStock(StockChangeRequest request);
	
	int setStock(StockChangeRequest request);
	
	int getStock(StockQueryRequest request);
	
	/**
	 * Request a shipment of at most the qty of the product given
	 * @param order
	 * @return
	 */
	ShipmentConfirmation requestShipment(ShipmentRequest request);
	
	void cancelShipment(Shipment shipment);
	void discontinueProduct(DiscontinueProductRequest request);
}
