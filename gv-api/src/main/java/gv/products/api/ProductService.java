package gv.products.api;

import gv.api.Product;

import java.util.List;

public interface ProductService {
	
	void saveProduct(Product product);
	Product getProductById(Long productId);
	void deleteProduct(Product product);
	List<Product> listProducts();
	
}
