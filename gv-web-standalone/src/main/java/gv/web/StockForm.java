package gv.web;

import java.util.HashMap;
import java.util.Map;

public class StockForm {
	
	private Map<String, Integer> stockLevels = new HashMap<String,Integer>();
	
	public StockForm() {
		
	}
	
	public StockForm(Map<String, Integer> stockLevels) {
		this.stockLevels = stockLevels;
	}

	public Map<String, Integer> getStockLevels() {
		return stockLevels;
	}

	public void setStockLevels(Map<String, Integer> stockLevels) {
		this.stockLevels = stockLevels;
	}

}
