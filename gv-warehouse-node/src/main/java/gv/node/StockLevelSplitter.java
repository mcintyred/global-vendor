package gv.node;

import gv.warehouse.jpa.service.entity.StockLevel;

import java.util.List;

import org.springframework.integration.annotation.Splitter;

import com.google.common.collect.Lists;

public class StockLevelSplitter {
	
	@Splitter
	public List<StockLevel> split(List<StockLevel> results) {
		return Lists.newArrayList(results);
	}

}
