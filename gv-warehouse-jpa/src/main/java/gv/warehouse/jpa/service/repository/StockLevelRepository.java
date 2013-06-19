package gv.warehouse.jpa.service.repository;

import org.springframework.data.repository.CrudRepository;

import gv.warehouse.jpa.service.entity.StockLevel;

public interface StockLevelRepository extends CrudRepository<StockLevel, Long> {
	
	public StockLevel findByWarehouseIdAndProductId(Long warehouseId, Long productId);

}
