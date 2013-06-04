package gv.products.service;

import static com.google.common.collect.Lists.newArrayList;
import gv.api.Product;
import gv.products.api.ProductService;
import gv.products.service.entity.ProductEntity;
import gv.products.service.repository.ProductRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("productService")
@Transactional
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	private final ProductRepository repository;
	
	public ProductServiceImpl() {
		this(null);
	}
	
	public ProductServiceImpl(ProductRepository repository) {
		this.repository = repository;
	}

	@Override
	public void saveProduct(Product product) {
		ProductEntity e = toEntity(product);
		repository.save(e);
		product.setId(e.getId());
	}

	@Override
	public Product getProductById(Long productId) {
		ProductEntity e = repository.findOne(productId);
		if(e == null) {
			return null;
		}
		
		return toDto(e);
	}

	@Override
	public void deleteProduct(Product product) {
		ProductEntity e = toEntity(product);
		repository.delete(e);
	}

	@Override
	public List<Product> listProducts() {
		Iterable<ProductEntity> entities = repository.findAll();
		List<Product> dtos = newArrayList();
		for(ProductEntity e : entities) {
			dtos.add(toDto(e));
		}
		return dtos;
	}
	
	private ProductEntity toEntity(Product dto) {
		ProductEntity e = new ProductEntity();
		e.setDescription(dto.getDescription());
		e.setId(dto.getId());
		e.setName(dto.getName());
		return e;
	}

	private Product toDto(ProductEntity e) {
		Product dto = new Product(e.getId(), e.getName(), e.getDescription());
		return dto;
	}

}
