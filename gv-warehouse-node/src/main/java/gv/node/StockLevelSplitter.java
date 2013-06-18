package gv.node;

import java.util.List;

import org.springframework.integration.annotation.Splitter;

import com.google.common.collect.Lists;

import gv.warehouse.jpa.entity.StockLevel;

public class StockLevelSplitter {
	
	@Splitter
	public List<StockLevel> split(List<StockLevel> results) {
		return Lists.newArrayList(results);
	}

}
