package gv.orders.api;

import gv.api.Order;
import gv.api.OrderConfirmation;

public interface OrderService {
	/**
	 * Place an order. 
	 * @param order
	 * @return
	 */
	OrderConfirmation placeOrder(Order order);
}
