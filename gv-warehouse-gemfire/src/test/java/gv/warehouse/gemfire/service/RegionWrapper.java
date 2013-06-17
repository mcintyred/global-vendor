package gv.warehouse.gemfire.service;

import gv.warehouse.gemfire.entity.StockLevel;
import gv.warehouse.gemfire.entity.StockLevel.Id;

import com.gemstone.gemfire.cache.Region;

/**
 * Autowiring of Maps requires that the key be assignable to String.
 * 
 * Id is not assignable, so a Region<Id, StockLevel> cannot be autoinjected.
 * 
 * This wrapper just wraps the region to get around the issue.
 * 
 * @author mcintyred
 *
 */
public class RegionWrapper {
	
	private Region<Id, StockLevel> region;

	public Region<Id, StockLevel> getRegion() {
		return region;
	}

	public void setRegion(Region<Id, StockLevel> region) {
		this.region = region;
	}
}
