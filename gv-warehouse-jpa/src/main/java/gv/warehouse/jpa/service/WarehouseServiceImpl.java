package gv.warehouse.jpa.service;

import gv.api.Shipment;
import gv.api.ShipmentLine;
import gv.warehouse.api.ShipmentConfirmation;
import gv.warehouse.api.ShipmentRequest;
import gv.warehouse.api.StockAlert;
import gv.warehouse.api.StockAlertListener;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.StockQueryRequest;
import gv.warehouse.api.WarehouseService;
import gv.warehouse.jpa.entity.StockLevel;
import gv.warehouse.jpa.service.repository.StockLevelRepository;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
	
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
	public String getName() {
		return "Local Database Service";
	}

	@Override
	public int updateStock(StockChangeRequest request) {
		
		Long warehouseId = request.getWarehouseId();
		Long productId = request.getProductId();
		int stockDelta = request.getQty();
		int oldLevel = 0;
		
		StockLevel stockLevel = repository.findByWarehouseIdAndProductId(warehouseId, productId);
		if(stockLevel == null) {
			stockLevel = new StockLevel(warehouseId, productId, stockDelta);
		} else { 
			oldLevel = stockLevel.getQty();
			stockLevel.addQty(stockDelta);
		}
		
		repository.save(stockLevel);
		
		
		raiseStockAlert(productId, warehouseId, oldLevel, stockLevel.getQty());
		return stockLevel.getQty();
	}

	@Override
	public int setStock(StockChangeRequest request) {
		
		Long warehouseId = request.getWarehouseId();
		Long productId = request.getProductId();
		int stockOnHand = request.getQty();
		int oldLevel = 0;
		
		StockLevel stockLevel = repository.findByWarehouseIdAndProductId(warehouseId, productId);
		
		if(stockLevel == null) {
			stockLevel = new StockLevel(warehouseId, productId, stockOnHand);
		} else { 
			oldLevel = stockLevel.getQty();
			stockLevel.setQty(stockOnHand);
		}
		
		repository.save(stockLevel);
		
		raiseStockAlert(productId, warehouseId, oldLevel, stockLevel.getQty());
		
		return stockLevel.getQty();
	}

	@Override
	public int getStock(StockQueryRequest request) {
		
		Long warehouseId = request.getWarehouseId();
		Long productId = request.getProductId();

		StockLevel stockLevel = repository.findByWarehouseIdAndProductId(warehouseId, productId);
		if(stockLevel != null) {
			return stockLevel.getQty();
		}
		return 0;
	}

	@Override
	public ShipmentConfirmation requestShipment(ShipmentRequest request) {
		StockLevel stockLevel = repository.findByWarehouseIdAndProductId(request.getWarehouseId(), request.getProductId());
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
		
		raiseStockAlert(request.getProductId(), request.getWarehouseId(), oldLevel, stockLevel.getQty());
		
		return new ShipmentConfirmation(request.getProductId(), new LocalDate(), allocatedQty);
	}

	@Override
	public void cancelShipment(Shipment shipment) {
		for(ShipmentLine line : shipment.getLines()) {
			
			StockLevel stockLevel = repository.findByWarehouseIdAndProductId(shipment.getWarehouse().getId(), line.getProduct().getId());
			
			if(stockLevel != null) {
				
				int oldLevel = stockLevel.getQty();

				stockLevel.addQty(line.getQty());
				repository.save(stockLevel);

				raiseStockAlert(line.getProduct().getId(), shipment.getWarehouse().getId(), oldLevel, stockLevel.getQty());
			}
		}
	}
	
	protected void raiseStockAlert(long productId, long warehouseId, int oldLevel, int newLevel) {
		
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
			stockAlertListener.handleStockAlert(new StockAlert(productId, warehouseId, newLevel, stockAlertThreshold));
		}
	}
}
