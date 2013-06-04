package gv.stock.api;

import java.util.List;

import gv.warehouse.api.StockAlert;

/**
 * A simple service which can be queried to discover the total available stock of a Product
 * @author mcintyred
 *
 */
public interface StockService {
	
	public int getTotalStock(Long productId);

}
