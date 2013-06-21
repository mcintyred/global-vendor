package gv.warehouse.jpa.service;

import gv.api.Shipment;
import gv.api.ShipmentLine;
import gv.stock.api.DiscontinueProductRequest;
import gv.stock.api.ShipmentConfirmation;
import gv.stock.api.ShipmentRequest;
import gv.stock.api.StockAlert;
import gv.stock.api.StockAlertEventSource;
import gv.stock.api.StockAlertListener;
import gv.stock.api.StockChangeRequest;
import gv.stock.api.StockQueryRequest;
import gv.warehouse.api.WarehouseService;
import gv.warehouse.jpa.service.entity.StockLevel;
import gv.warehouse.jpa.service.repository.StockLevelRepository;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService, StockAlertEventSource {
	
	@Autowired
	private final StockLevelRepository repository;
	
	private int stockAlertThreshold;
	
	private StockAlertListener stockAlertListener;
	
	public WarehouseServiceImpl() {
		this(null);
	}
	
	public WarehouseServiceImpl(StockLevelRepository stockLevelRepository) {
		this.repository = stockLevelRepository;
	}

	@Override
	public void setStockAlertListener(StockAlertListener listener) {
		this.stockAlertListener = listener;
	}

	@Override
	public void setStockAlertThreshold(int threshold) {
		this.stockAlertThreshold = threshold;	
	}

	@Override
	public int updateStock(StockChangeRequest request) {
		
		String warehouseName = request.getWarehouseName();
		Long productId = request.getProductId();
		int stockDelta = request.getQty();
		int oldLevel = 0;
		
		StockLevel stockLevel = repository.findByWarehouseNameAndProductId(warehouseName, productId);
		if(stockLevel == null) {
			stockLevel = new StockLevel(warehouseName, productId, stockDelta);
		} else { 
			oldLevel = stockLevel.getQty();
			stockLevel.addQty(stockDelta);
		}
		
		repository.save(stockLevel);
		
		
		raiseStockAlert(productId, warehouseName, oldLevel, stockLevel.getQty());
		return stockLevel.getQty();
	}

	@Override
	public int setStock(StockChangeRequest request) {
		
		String warehouseName = request.getWarehouseName();
		Long productId = request.getProductId();
		int stockOnHand = request.getQty();
		int oldLevel = 0;
		
		StockLevel stockLevel = repository.findByWarehouseNameAndProductId(warehouseName, productId);
		
		if(stockLevel == null) {
			stockLevel = new StockLevel(warehouseName, productId, stockOnHand);
		} else { 
			oldLevel = stockLevel.getQty();
			stockLevel.setQty(stockOnHand);
		}
		
		repository.save(stockLevel);
		
		raiseStockAlert(productId, warehouseName, oldLevel, stockLevel.getQty());
		
		return stockLevel.getQty();
	}

	@Override
	public int getStock(StockQueryRequest request) {
		
		String warehouseName = request.getWarehouseName();
		Long productId = request.getProductId();

		StockLevel stockLevel = repository.findByWarehouseNameAndProductId(warehouseName, productId);
		if(stockLevel != null) {
			return stockLevel.getQty();
		}
		return 0;
	}
	
	@Override
	public void discontinueProduct(DiscontinueProductRequest request) {
		StockLevel stockLevel = repository.findByWarehouseNameAndProductId(request.getWarehouseName(), request.getProductId());
		if(stockLevel != null) {
			repository.delete(stockLevel);
		}
	}

	@Override
	public ShipmentConfirmation requestShipment(ShipmentRequest request) {
		StockLevel stockLevel = repository.findByWarehouseNameAndProductId(request.getWarehouseName(), request.getProductId());
		int allocatedQty = 0;
		
		if(stockLevel == null) {
			return new ShipmentConfirmation(request.getProductId(), null, 0);
		}
		
		int oldLevel = stockLevel.getQty();

		if(stockLevel.getQty() < request.getQty()) {
			allocatedQty = stockLevel.getQty();
		} else {
			allocatedQty = request.getQty();
		}
		
		stockLevel.addQty(-allocatedQty);
		repository.save(stockLevel);
		
		raiseStockAlert(request.getProductId(), request.getWarehouseName(), oldLevel, stockLevel.getQty());
		
		return new ShipmentConfirmation(request.getProductId(), new LocalDate(), allocatedQty);
	}

	@Override
	public void cancelShipment(Shipment shipment) {
		for(ShipmentLine line : shipment.getLines()) {
			
			StockLevel stockLevel = repository.findByWarehouseNameAndProductId(shipment.getWarehouse().getName(), line.getProduct().getId());
			
			if(stockLevel != null) {
				
				int oldLevel = stockLevel.getQty();

				stockLevel.addQty(line.getQty());
				repository.save(stockLevel);

				raiseStockAlert(line.getProduct().getId(), shipment.getWarehouse().getName(), oldLevel, stockLevel.getQty());
			}
		}
	}
	
	protected void raiseStockAlert(long productId, String warehouseName, int oldLevel, int newLevel) {
		
		if(oldLevel == newLevel || stockAlertListener == null) {
			return;
		}
		
		boolean triggerAlert = false;
		
		if(newLevel == 0) {
			triggerAlert = true;
		} else if(oldLevel > stockAlertThreshold && newLevel <= stockAlertThreshold) {
			triggerAlert = true;
		} else if(oldLevel <= stockAlertThreshold && newLevel > stockAlertThreshold) {
			triggerAlert = true;
		}
		
		if(triggerAlert) {
			stockAlertListener.handleStockAlert(new StockAlert(productId, warehouseName, newLevel, stockAlertThreshold));
		}
	}
}
