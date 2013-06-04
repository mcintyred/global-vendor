package gv.store.service;

import gv.api.Order;
import gv.api.OrderConfirmation;
import gv.api.Product;
import gv.orders.api.OrderService;
import gv.products.api.ProductService;
import gv.stock.api.StockService;
import gv.store.api.StoreService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements StoreService {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private StockService stockService;
	
	@Autowired
	private OrderService orderService;

	@Override
	public List<Product> listProducts() {
		return productService.listProducts();
	}

	@Override
	public Product getProductById(Long productId) {
		return productService.getProductById(productId);
	}

	@Override
	public int getTotalStock(Long productId) {
		return stockService.getTotalStock(productId);
	}

	@Override
	public OrderConfirmation placeOrder(Order order) {
		return orderService.placeOrder(order);
	}

}
