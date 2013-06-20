package gv.orders.service;

import gv.api.Order;
import gv.api.OrderConfirmation;
import gv.api.OrderLine;
import gv.api.Product;
import gv.api.Shipment;
import gv.api.ShipmentLine;
import gv.api.Warehouse;
import gv.orders.api.OrderService;
import gv.products.api.ProductService;
import gv.stock.api.ShipmentConfirmation;
import gv.stock.api.ShipmentRequest;
import gv.stock.api.StockService;
import gv.stock.api.WarehouseStockData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private StockService stockService;

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public StockService getStockService() {
		return stockService;
	}

	public void setStockService(StockService stockService) {
		this.stockService = stockService;
	}

	/**
	 * Place an order, fulfilling the requested qty of each Product from the warehouses with the most stock
	 */
	@Override
	public OrderConfirmation placeOrder(Order order) {
		
		Map<Warehouse, List<ShipmentLine>> shipments = new HashMap<Warehouse, List<ShipmentLine>>();
		
		for(OrderLine line : order.getLines()) {
			Map<Warehouse, ShipmentLine> shipmentLines = getLineShipments(line);
			for(Warehouse w : shipmentLines.keySet()) {
				if(!shipments.containsKey(w)) {
					shipments.put(w, new ArrayList<ShipmentLine>());
				}
				
				shipments.get(w).add(shipmentLines.get(w));
			}
		}
		
		List<Shipment> consolidatedShipments = new ArrayList<Shipment>();
		for(Warehouse w : shipments.keySet()) {
			consolidatedShipments.add(new Shipment(w, shipments.get(w)));
		}
		
		return new OrderConfirmation(order, consolidatedShipments);
	}
	
	protected Map<Warehouse, ShipmentLine> getLineShipments(OrderLine orderLine) {
		
		int fulfilledQty = 0;
		
		Product product = productService.getProductById(orderLine.getProductId());
		
		List<WarehouseStockData> availableStock = stockService.getStockData(orderLine.getProductId());
		
		// Sort the list of warehouses in descending order of stockAvailable
		Collections.sort(availableStock, Collections.reverseOrder(new Comparator<WarehouseStockData>() {

			@Override
			public int compare(WarehouseStockData w1, WarehouseStockData w2) {
				// TODO Auto-generated method stub
				return w1.getQty() - w2.getQty();
			}
		}));
		
		// Go ahead and try to place the orders
		Map<Warehouse, ShipmentLine> shipmentLines = new HashMap<Warehouse, ShipmentLine>();
		for(WarehouseStockData ws : availableStock) {
			
			ShipmentRequest request = new ShipmentRequest(ws.getWarehouse().getId(), orderLine.getProductId(), orderLine.getQty() - fulfilledQty);
			ShipmentConfirmation shipmentConfirmation = stockService.requestShipment(request);
			if(shipmentConfirmation.getQty() > 0) {
				fulfilledQty += shipmentConfirmation.getQty();
				shipmentLines.put(ws.getWarehouse(), new ShipmentLine(shipmentConfirmation.getShipmentDate(), shipmentConfirmation.getQty(), product));
			}
			if(fulfilledQty == orderLine.getQty()) {
				break;
			}
		}

		return shipmentLines;
	}
}
