package gv.core.service;

import gv.api.Warehouse;
import gv.core.service.entity.WarehouseServiceBinding;
import gv.core.service.repository.WarehouseRepository;
import gv.warehouse.api.WarehouseService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
@Transactional
public class WarehouseServiceLocatorImpl implements WarehouseServiceLocator {
	
	@Autowired
	private WarehouseRepository repository;
	
	private Map<String, WarehouseService> serviceNameMap;
	
	private WarehouseServiceList serviceList;
	
	@Autowired
	public void setServiceList(WarehouseServiceList list) {
		
		serviceList = list;
	}
	
	public Map<String, WarehouseService> getServiceNameMap() {
		
		if(serviceNameMap == null) {
			
			List<WarehouseService> services = serviceList.getList();
			
			serviceNameMap = Maps.newHashMap();
			
			for(WarehouseService service : services) {
				
				if(serviceNameMap.containsKey(service.getName())) {
					throw new IllegalArgumentException("Attempt to register two WarehouseServices with the same name : " + service.getName());
				}
				
				serviceNameMap.put(service.getName(),  service);
			}
		}
		return serviceNameMap;
	}
	
	
	
	public void setRepository(WarehouseRepository repository) {
		this.repository = repository;
	}

	@Override
	public WarehouseService locateService(Warehouse warehouse) {
		
		WarehouseServiceBinding entity = repository.findOne(warehouse.getId());
		
		if(entity == null) {
			throw new IllegalArgumentException("No such warehouse registered : " + warehouse);
		}
		
		if(getServiceNameMap().containsKey(entity.getServiceBindingName())) {
			return getServiceNameMap().get(entity.getServiceBindingName());
		}
		
		throw new IllegalArgumentException("No service registered for warehouse : " + warehouse);
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
	public List<WarehouseService> listServices() {
		return Lists.newArrayList(getServiceNameMap().values());
	}
	
	@Override
	public List<WarehouseServiceBinding> listBindings() {
		return Lists.newArrayList(repository.findAll());
	}
}