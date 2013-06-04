package gv.stock.service;

import gv.core.WarehouseStockData;
import gv.core.service.DistributedWarehouseService;
import gv.stock.api.StockService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockService {
	
	@Autowired
	private DistributedWarehouseService distributedWarehouseService;
	
	@Override
	public int getTotalStock(Long productId) {
		
		List<WarehouseStockData> stockData = distributedWarehouseService.getStockData(productId);
		
		int total = 0;
		
		for(WarehouseStockData data : stockData) {
			total += data.getQty();
		}
		
		return total;
	}

}
