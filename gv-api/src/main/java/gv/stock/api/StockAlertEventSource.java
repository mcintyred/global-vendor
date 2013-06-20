package gv.stock.api;

public interface StockAlertEventSource {
	
	public void setStockAlertListener(StockAlertListener listener);

	public void setStockAlertThreshold(int threshold);
}
