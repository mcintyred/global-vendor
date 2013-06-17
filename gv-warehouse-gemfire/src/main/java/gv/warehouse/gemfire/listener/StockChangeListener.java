package gv.warehouse.gemfire.listener;

import gv.warehouse.api.StockAlert;
import gv.warehouse.api.StockAlertEventSource;
import gv.warehouse.api.StockAlertListener;
import gv.warehouse.gemfire.entity.StockLevel;
import gv.warehouse.gemfire.entity.StockLevel.Id;

import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;

public class StockChangeListener extends CacheListenerAdapter<Id, StockLevel> implements StockAlertEventSource {

	private StockAlertListener stockAlertListener;
	
	private int stockAlertThreshold;
	
	
	public StockAlertListener getStockAlertListener() {
		return stockAlertListener;
	}

	public void setStockAlertListener(StockAlertListener stockAlertListener) {
		this.stockAlertListener = stockAlertListener;
	}

	public int getStockAlertThreshold() {
		return stockAlertThreshold;
	}

	public void setStockAlertThreshold(int stockAlertThreshold) {
		this.stockAlertThreshold = stockAlertThreshold;
	}

	@Override
	public void afterCreate(EntryEvent<Id, StockLevel> event) {
		StockLevel after = event.getNewValue();
		raiseStockAlert(after.getId(), 0, after.getQty());
	}

	@Override
	public void afterDestroy(EntryEvent<Id, StockLevel> event) {
		StockLevel before = event.getOldValue();
		raiseStockAlert(before.getId(), before.getQty(), 0);
	}

	@Override
	public void afterInvalidate(EntryEvent<Id, StockLevel> event) {
		StockLevel before = event.getOldValue();
		raiseStockAlert(before.getId(), before.getQty(), 0);
	}

	@Override
	public void afterUpdate(EntryEvent<Id, StockLevel> event) {
		StockLevel before = event.getOldValue();
		StockLevel after = event.getNewValue();
		
		raiseStockAlert(after.getId(), before.getQty(), after.getQty());
	}
	
	protected void raiseStockAlert(Id id, int oldLevel, int newLevel) {
		
		if(oldLevel == newLevel || stockAlertListener == null) {
			return;
		}
		
		boolean triggerAlert = false;
		
		if(newLevel == 0) {
			triggerAlert = true;
		} else if(oldLevel > stockAlertThreshold && newLevel <= stockAlertThreshold) {
			triggerAlert = true;
		} else if(oldLevel <= stockAlertThreshold && newLevel > stockAlertThreshold) {
			triggerAlert = true;
		}
		
		if(triggerAlert) {
			stockAlertListener.handleStockAlert(new StockAlert(id.getProductId(), id.getWarehouseId(), newLevel, stockAlertThreshold));
		}
	}
}
