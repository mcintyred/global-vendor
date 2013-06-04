package gv.web;

import java.util.HashMap;
import java.util.Map;

public class StockForm {
	
	private Map<Long, Integer> stockLevels = new HashMap<Long,Integer>();
	
	public StockForm() {
		
	}
	
	public StockForm(Map<Long, Integer> stockLevels) {
		this.stockLevels = stockLevels;
	}

	public Map<Long, Integer> getStockLevels() {
		return stockLevels;
	}

	public void setStockLevels(Map<Long, Integer> stockLevels) {
		this.stockLevels = stockLevels;
	}
	
	

}
