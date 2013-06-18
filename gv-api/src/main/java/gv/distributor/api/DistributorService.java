package gv.distributor.api;

import gv.api.Order;
import gv.api.OrderConfirmation;
import gv.api.Product;

import java.util.List;

/**
 * Defines the interface of a service which is used by distributors to obtain product and stock information
 * and to place orders
 * 
 * @author mcintyred
 *
 */
public interface DistributorService {
	
	public List<Product> listProducts();
	public Product getProductById(Long productId);
	public int getTotalStock(Long productId);
	public OrderConfirmation placeOrder(Order order);

}
