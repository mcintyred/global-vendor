package gv.core.service;

import gv.api.Product;
import gv.api.Warehouse;
import gv.core.service.entity.StockAlertEntity;
import gv.core.service.entity.WarehouseServiceBinding;
import gv.core.service.repository.StockAlertEntityRepository;
import gv.core.service.repository.WarehouseRepository;
import gv.products.api.ProductService;
import gv.warehouse.api.DistributedWarehouseService;
import gv.warehouse.api.ShipmentConfirmation;
import gv.warehouse.api.ShipmentRequest;
import gv.warehouse.api.StockAlert;
import gv.warehouse.api.StockAlertDetails;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.StockQueryRequest;
import gv.warehouse.api.WarehouseService;
import gv.warehouse.api.WarehouseStockData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Service
@Transactional
public class DistributedWarehouseServiceImpl implements DistributedWarehouseService {
	
	@Autowired
	private WarehouseServiceLocator warehouseServiceLocator;
	
	@Autowired
	private WarehouseRepository repository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private StockAlertEntityRepository stockAlertRepository;


	public void setWarehouseServiceLocator(
			WarehouseServiceLocator warehouseServiceLocator) {
		this.warehouseServiceLocator = warehouseServiceLocator;
	}

	public void setWarehouseRepository(WarehouseRepository repository) {
		this.repository = repository;
	}
	
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
	
	public void setStockAlertEntityRepository(StockAlertEntityRepository repository) {
		stockAlertRepository = repository;
	}

	@Override
	public Set<Warehouse> listWarehouses() {
		
		Set<Warehouse> warehouses = Sets.newHashSet();
		
		for(WarehouseServiceBinding entity : repository.findAll()) {
			warehouses.add(new Warehouse(entity.getId(), entity.getName()));
		}
		
		return warehouses;
	}

	@Override
	public Warehouse getWarehouseById(Long warehouseId) {
		WarehouseServiceBinding entity = repository.findOne(warehouseId);
		
		if(entity == null) {
			throw new IllegalArgumentException();
		}
		
		return new Warehouse(entity.getId(), entity.getName());
	}


	@Override
	public int updateStock(StockChangeRequest request) {
		return getWarehouseService(request.getWarehouseId()).updateStock(request);
	}

	@Override
	public void setStock(StockChangeRequest request) {
		getWarehouseService(request.getWarehouseId()).setStock(request);
	}

	@Override
	public int getStockInWarehouse(StockQueryRequest request) {
		return getWarehouseService(request.getWarehouseId()).getStock(request);
	}
	
	@Override
	public List<WarehouseStockData> getStockData(Long productId) {
		List<WarehouseStockData> availableStock = new ArrayList<WarehouseStockData>();
		for(Warehouse w : listWarehouses()) {
			StockQueryRequest query = new StockQueryRequest(w.getId(), productId);
			int stockAtWarehouse = getStockInWarehouse(query);
			if(stockAtWarehouse > 0) {
				availableStock.add(new WarehouseStockData(stockAtWarehouse, w));
			}
		}
		
		return availableStock;
	}
	
	@Override
	public ShipmentConfirmation requestShipment(ShipmentRequest request) {
		return getWarehouseService(request.getWarehouseId()).requestShipment(request);
	}
	
	protected WarehouseService getWarehouseService(Long warehouseId) {
		Warehouse warehouse = getWarehouseById(warehouseId);
		return warehouseServiceLocator.locateService(warehouse);
	}
	
	@Override
	public void handleStockAlert(StockAlert alert) {
		if(alert.getThreshold() >= alert.getStockLevel()) {
			createStockAlert(alert);
		} else {
			deleteStockAlerts(alert.getWarehouseId(), alert.getProductId());
		}
	}

	protected void createStockAlert(StockAlert alert) {
		StockAlertEntity ent = new StockAlertEntity();
		ent.setWarehouseId(alert.getWarehouseId());
		ent.setProductId(alert.getProductId());
		ent.setStockLevel(alert.getStockLevel());
		ent.setThreshold(alert.getThreshold());
		stockAlertRepository.save(ent);
	}

	protected void deleteStockAlerts(long warehouseId, long productId) {
		List<StockAlertEntity> ents = stockAlertRepository.findByWarehouseIdAndProductId(warehouseId, productId);
		for(StockAlertEntity ent : ents) {
			stockAlertRepository.delete(ent);
		}
	}

	@Override
	public List<StockAlertDetails> getStockAlerts() {
		Iterable<StockAlertEntity> ents = stockAlertRepository.findAll();
		List<StockAlertDetails> alerts = Lists.newArrayList();
		for(StockAlertEntity ent : ents) {
			Warehouse w = getWarehouseById(ent.getWarehouseId());
			Product p = productService.getProductById(ent.getProductId());
			alerts.add(new StockAlertDetails(p, w, ent.getStockLevel(), ent.getThreshold()));
		}
		
		return alerts;
	}
	
	
	@Override
	public int getTotalStock(Long productId) {
		
		List<WarehouseStockData> stockData = getStockData(productId);
		
		int total = 0;
		
		for(WarehouseStockData data : stockData) {
			total += data.getQty();
		}
		
		return total;
	}
}
