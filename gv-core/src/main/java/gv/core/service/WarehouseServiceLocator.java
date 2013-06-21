package gv.core.service;

import gv.api.Warehouse;
import gv.core.service.entity.WarehouseServiceBinding;
import gv.warehouse.api.WarehouseService;

import java.util.List;
import java.util.Set;

public interface WarehouseServiceLocator {

	WarehouseService locateService(Long warehouseId);
	WarehouseService locateService(String warehouseName);
	
	List<WarehouseService> listServices();
	
	WarehouseServiceBinding bindWarehouseService(Warehouse warehouse, String serviceName);
	
	List<WarehouseServiceBinding> listBindings();
	
	Set<Warehouse> listWarehouses();
	Warehouse getWarehouseById(Long warehouseId);
	Warehouse getWarehouseByName(String warehouseName);
	
}
