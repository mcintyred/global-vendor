package gv.warehouse.gemfire.listener;

import gv.warehouse.api.StockAlert;
import gv.warehouse.api.StockAlertEventSource;
import gv.warehouse.api.StockAlertListener;
import gv.warehouse.gemfire.entity.StockLevel;
import gv.warehouse.gemfire.entity.StockLevel.Id;

import com.gemstone.gemfire.cache.query.CqEvent;

/**
 * ContinuousQueries do not provide access to the old value of a cache entity,
 * so instead the StockAlert entity itself is provided with an oldValue field
 * which can be used in the event listener to determine when the stock threshold
 * has been crossed.
 * 
 * @author mcintyred
 *
 */
public class CQListener implements StockAlertEventSource {
	
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
	
	public void handleEvent(CqEvent event) {
		
		if(event.getBaseOperation().isCreate()) {
			
			StockLevel level = (StockLevel) event.getNewValue();
			raiseStockAlert(level.getId(), 0, level.getQty());
			
		} else if(event.getBaseOperation().isUpdate()) {
			
			StockLevel level = (StockLevel) event.getNewValue();
			
			raiseStockAlert(level.getId(), level.getOldQty(), level.getQty());
			
		} else if(event.getBaseOperation().isDestroy()) {

			StockLevel level = (StockLevel) event.getNewValue();
			
			raiseStockAlert(level.getId(), level.getQty(), 0);
		}
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
