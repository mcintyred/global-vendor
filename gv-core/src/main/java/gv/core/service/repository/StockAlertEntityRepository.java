package gv.core.service.repository;

import gv.core.service.entity.StockAlertEntity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface StockAlertEntityRepository extends CrudRepository<StockAlertEntity, Long>{

	public List<StockAlertEntity> findByWarehouseIdAndProductId(long warehouseId, long productId);
	
}
