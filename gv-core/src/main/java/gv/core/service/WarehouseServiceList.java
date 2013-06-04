package gv.core.service;

import gv.warehouse.api.WarehouseService;

import java.util.List;

public class WarehouseServiceList {
	
	private List<WarehouseService> list;
	
	public WarehouseServiceList(List<WarehouseService> list) {
		this.list = list;
	}
	
	public List<WarehouseService> getList() {
		return list;
	}
}
