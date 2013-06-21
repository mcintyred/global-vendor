package gv.core.service;

import gv.warehouse.api.WarehouseService;

import java.util.List;
import java.util.Map;

public class WarehouseServiceMap {
	
	private Map<String, WarehouseService> map;
	
	public WarehouseServiceMap(Map<String, WarehouseService> map) {
		this.map = map;
	}
	
	public Map<String, WarehouseService> getMap() {
		return map;
	}
}
