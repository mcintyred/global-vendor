package gv.warehouse.gemfire.service;

import gv.api.Shipment;
import gv.api.ShipmentLine;
import gv.stock.api.DiscontinueProductRequest;
import gv.stock.api.ShipmentConfirmation;
import gv.stock.api.ShipmentRequest;
import gv.stock.api.StockChangeRequest;
import gv.stock.api.StockQueryRequest;
import gv.warehouse.api.WarehouseService;
import gv.warehouse.gemfire.entity.StockLevel;
import gv.warehouse.gemfire.entity.StockLevel.Id;
import gv.warehouse.gemfire.service.repository.StockLevelRepository;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
	
	@Autowired
	private final StockLevelRepository repository;
	
	public WarehouseServiceImpl() {
		this(null);
	}
	
	public WarehouseServiceImpl(StockLevelRepository stockLevelRepository) {
		this.repository = stockLevelRepository;
	}

	@Override
	public int updateStock(StockChangeRequest request) {
		
		String warehouseName = request.getWarehouseName();
		Long productId = request.getProductId();
		int stockDelta = request.getQty();
		
		StockLevel stockLevel = repository.findOne(new Id(warehouseName, productId));

		if(stockLevel == null) {
			stockLevel = new StockLevel(warehouseName, productId, stockDelta);
		} else { 
			stockLevel = new StockLevel(warehouseName, productId, stockLevel.getQty(), stockLevel.getQty() + stockDelta);
		}
		
		repository.save(stockLevel);
		return stockLevel.getQty();
	}

	@Override
	public int setStock(StockChangeRequest request) {
		
		String warehouseName = request.getWarehouseName();
		Long productId = request.getProductId();
		int stockOnHand = request.getQty();
		
		StockLevel stockLevel = new StockLevel(warehouseName, productId, stockOnHand);
		
		repository.save(stockLevel);
		return stockLevel.getQty();
	}

	@Override
	public int getStock(StockQueryRequest request) {
		
		String warehouseName = request.getWarehouseName();
		Long productId = request.getProductId();

		StockLevel stockLevel = repository.findOne(new Id(warehouseName, productId));
		if(stockLevel != null) {
			return stockLevel.getQty();
		}
		return 0;
	}
	
	@Override
	public void discontinueProduct(DiscontinueProductRequest request) {
		StockLevel stockLevel = repository.findOne(new Id(request.getWarehouseName(), request.getProductId()));
		if(stockLevel != null) {
			repository.delete(stockLevel);
		}
	}

	@Override
	public ShipmentConfirmation requestShipment(ShipmentRequest request) {
		String warehouseName = request.getWarehouseName();
		Long productId = request.getProductId();

		StockLevel stockLevel = repository.findOne(new Id(warehouseName, productId));
		int allocatedQty = 0;
		
		if(stockLevel == null) {
			return new ShipmentConfirmation(request.getProductId(), null, 0);
		}
		
		if(stockLevel.getQty() < request.getQty()) {
			allocatedQty = stockLevel.getQty();
		} else {
			allocatedQty = request.getQty();
		}			
		
		stockLevel = new StockLevel(warehouseName, productId, stockLevel.getQty(), stockLevel.getQty() - allocatedQty);

		repository.save(stockLevel);
		
		return new ShipmentConfirmation(request.getProductId(), new LocalDate(), allocatedQty);
	}

	@Override
	public void cancelShipment(Shipment shipment) {
		for(ShipmentLine line : shipment.getLines()) {
			
			Id id = new Id(shipment.getWarehouse().getName(), line.getProduct().getId());
			
			StockLevel stockLevel = repository.findOne(id);
			
			if(stockLevel != null) {
				stockLevel = new StockLevel(id, stockLevel.getQty(), stockLevel.getQty() + line.getQty());
			} else {
				stockLevel = new StockLevel(id, line.getQty());
			}
			
			repository.save(stockLevel);
		}
	}
}
