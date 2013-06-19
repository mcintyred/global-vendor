package gv.distributor.service;

import gv.api.Order;
import gv.api.OrderConfirmation;
import gv.api.Product;
import gv.distributor.api.DistributorService;
import gv.orders.api.OrderService;
import gv.products.api.ProductService;
import gv.warehouse.api.DistributedWarehouseService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributorServiceImpl implements DistributorService {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private DistributedWarehouseService distributedWarehouseService;
	
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
		return distributedWarehouseService.getTotalStock(productId);
	}

	@Override
	public OrderConfirmation placeOrder(Order order) {
		return orderService.placeOrder(order);
	}

}
