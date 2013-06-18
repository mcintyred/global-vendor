package gv.node;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import gv.warehouse.jpa.entity.StockLevel;

public class StockLevelMapper implements RowMapper<StockLevel>{
	public StockLevel mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		StockLevel s = new StockLevel();
		s.setId(rs.getLong("id"));
		s.setProductId(rs.getLong("productId"));
		s.setQty(rs.getInt("qty"));
		s.setWarehouseId(rs.getLong("warehouseId"));
		return s;
	}
}
