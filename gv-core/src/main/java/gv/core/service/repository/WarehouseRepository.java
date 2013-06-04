package gv.core.service.repository;

import gv.core.service.entity.WarehouseServiceBinding;

import org.springframework.data.repository.CrudRepository;

public interface WarehouseRepository extends CrudRepository<WarehouseServiceBinding, Long>{
	
	public WarehouseServiceBinding findOneByName(String name);
}
