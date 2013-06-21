package gv.core.service;

import gv.api.Warehouse;
import gv.core.service.entity.WarehouseServiceBinding;
import gv.core.service.repository.WarehouseRepository;
import gv.warehouse.api.WarehouseService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Service
@Transactional
public class WarehouseServiceLocatorImpl implements WarehouseServiceLocator {
	
	@Autowired
	private WarehouseRepository repository;
	
	private WarehouseServiceMap serviceMap;
	
	@Autowired
	public void setServiceMap(WarehouseServiceMap map) {
		
		serviceMap = map;
	}
	
	public Map<String, WarehouseService> getServiceNameMap() {
		return serviceMap.getMap();
	}
	
	public void setRepository(WarehouseRepository repository) {
		this.repository = repository;
	}

	@Override
	public WarehouseService locateService(Long warehouseId) {
		
		WarehouseServiceBinding entity = repository.findOne(warehouseId);
		
		if(entity == null) {
			throw new IllegalArgumentException("No such warehouse registered : " + warehouseId);
		}
		
		if(getServiceNameMap().containsKey(entity.getServiceBindingName())) {
			return getServiceNameMap().get(entity.getServiceBindingName());
		}
		
		throw new IllegalArgumentException("No service registered for warehouse : " + entity.getName());
	}

	@Override
	public WarehouseService locateService(String warehouseName) {
		
		WarehouseServiceBinding entity = repository.findOneByName(warehouseName);
		
		if(entity == null) {
			throw new IllegalArgumentException("No such warehouse registered : " + warehouseName);
		}
		
		if(getServiceNameMap().containsKey(entity.getServiceBindingName())) {
			return getServiceNameMap().get(entity.getServiceBindingName());
		}
		
		throw new IllegalArgumentException("No service registered for warehouse : " + entity.getName());
	}

	@Override
	public WarehouseServiceBinding bindWarehouseService(Warehouse warehouse, String serviceName) {
		
		if(!getServiceNameMap().containsKey(serviceName)) {
			throw new IllegalArgumentException("Cannot bind service '"+serviceName+"' - no such service is registered");
		}
		
		WarehouseServiceBinding entity = repository.findOneByName(warehouse.getName());
		
		if(entity == null) {
			entity = new WarehouseServiceBinding(warehouse.getName(), serviceName);
		} else {
			
			if(warehouse.getId() != null && !warehouse.getId().equals(entity.getId())) {
				throw new IllegalArgumentException("Warehouse "+warehouse.getId()+":"+warehouse.getName()+" already registered with id "+entity.getId());
			}
			entity.setServiceBindingName(serviceName);
		}
		
		repository.save(entity);
		
		warehouse.setId(entity.getId());
		
		return entity;
	}

	@Override
	public List<WarehouseServiceBinding> listBindings() {
		return Lists.newArrayList(repository.findAll());
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
	public Warehouse getWarehouseByName(String warehouseName) {
		WarehouseServiceBinding entity = repository.findOneByName(warehouseName);
		
		if(entity == null) {
			throw new IllegalArgumentException();
		}
		
		return new Warehouse(entity.getId(), entity.getName());
	}
}
