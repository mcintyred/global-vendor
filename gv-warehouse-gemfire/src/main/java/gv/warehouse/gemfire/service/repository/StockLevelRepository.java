package gv.warehouse.gemfire.service.repository;

import gv.warehouse.gemfire.entity.StockLevel;

import org.springframework.data.repository.CrudRepository;

public interface StockLevelRepository extends CrudRepository<StockLevel, StockLevel.Id> {
	
}
