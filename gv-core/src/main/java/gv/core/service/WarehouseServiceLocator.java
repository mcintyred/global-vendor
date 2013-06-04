package gv.core.service;

import gv.api.Warehouse;
import gv.core.service.entity.WarehouseServiceBinding;
import gv.warehouse.api.WarehouseService;

import java.util.List;

public interface WarehouseServiceLocator {

	WarehouseService locateService(Warehouse warehouse);
	
	List<WarehouseService> listServices();
	
	WarehouseServiceBinding bindWarehouseService(Warehouse warehouse, String serviceName);
	
	List<WarehouseServiceBinding> listBindings();
	
}
