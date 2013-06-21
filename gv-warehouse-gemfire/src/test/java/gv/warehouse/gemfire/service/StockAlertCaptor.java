package gv.warehouse.gemfire.service;

import gv.stock.api.StockAlert;
import gv.stock.api.StockAlertListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StockAlertCaptor implements StockAlertListener {
	
	private StockAlert alert;
	
	private CountDownLatch latch;
	
	public void expect() {
		latch = new CountDownLatch(1);
	}

	@Override
	public void handleStockAlert(StockAlert alert) {
		this.alert = alert;
		latch.countDown();
	}
	
	public StockAlert getAlert() throws Exception {
		latch.await(1000L, TimeUnit.MILLISECONDS);
		return alert;
	}
	
	public void clear() throws Exception {
		if(latch != null) getAlert();
		alert = null;
	}
}
