package gv.products.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gv.api.Product;
import gv.products.service.repository.ProductRepository;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class ProductServiceImplTest {
	
	private ProductServiceImpl service;
	
	@Autowired
	ProductRepository repository;
	
	@Before
	public void setUp() {
		service = new ProductServiceImpl(repository);
	}
	
	@Test
	public void shouldSaveProductAndSetGeneratedId() {
		// Given
		Product p = new Product("p1", "p1d");

		// when
		service.saveProduct(p);
		
		// then
		assertNotNull(p.getId());
	}
	
	@Test
	public void shouldSaveChanges() {
		// Given
		Product p = new Product("p1", "p1d");
		service.saveProduct(p);	
		
		// when
		Product retrieved = service.getProductById(p.getId());
		
		// then
		assertEquals(p.getName(), retrieved.getName());
		assertEquals(p.getDescription(), retrieved.getDescription());
		assertEquals(p.getId(), retrieved.getId());
	}
	
	@Test
	public void shouldDelete() {
		// given
		Product p = new Product("p1", "p1d");
		service.saveProduct(p);	
		
		// when
		service.deleteProduct(p);
		Product retrieved = service.getProductById(p.getId());
		
		// then
		assertNull(retrieved);
		
	}
	
	@Test
	public void shouldListProducts() {
		// given
		Product p1 = new Product("p1", "p1d");
		service.saveProduct(p1);	
		
		Product p2 = new Product("p2", "p2d");
		service.saveProduct(p2);	
		
		// when
		List<Product> products = service.listProducts();
		
		// then
		assertEquals(2, products.size());
	}
}
