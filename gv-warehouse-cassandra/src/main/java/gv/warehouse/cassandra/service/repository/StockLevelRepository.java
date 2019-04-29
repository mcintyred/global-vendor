package gv.warehouse.cassandra.service.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;

import gv.warehouse.cassandra.service.entity.StockLevel;

public interface StockLevelRepository extends CassandraRepository<StockLevel, Long> {
	
	public StockLevel findByWarehouseNameAndProductId(String warehouseName, Long productId);

}
