package gv.web;

import gv.api.Order;
import gv.api.OrderConfirmation;
import gv.api.OrderLine;
import gv.api.Product;
import gv.api.Warehouse;
import gv.core.StockAlertDetails;
import gv.core.service.DistributedWarehouseService;
import gv.core.service.WarehouseServiceLocator;
import gv.core.service.entity.WarehouseServiceBinding;
import gv.orders.api.OrderService;
import gv.products.api.ProductService;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.StockQueryRequest;
import gv.warehouse.api.WarehouseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

/**
 * This controller provides summary information on all Warehouses and Products
 * known to the system
 * 
 * @author mcintyred
 *
 */
@Controller
public class AdminController {
	
	@Autowired
	private DistributedWarehouseService warehouseService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private WarehouseServiceLocator warehouseServiceLocator;
	
	@Autowired
	private OrderService orderService;

	@RequestMapping("/admin.html")
	public String index(Model model) {
		
		List<WarehouseService> services = warehouseServiceLocator.listServices();
		List<WarehouseServiceBinding> warehouses = warehouseServiceLocator.listBindings();
		List<Product> products = productService.listProducts();
		
		List<String> serviceNames = Lists.newArrayList();
		for(WarehouseService service : services) {
			serviceNames.add(service.getName());
		}
		
		model.addAttribute("serviceNames", serviceNames);
		model.addAttribute("warehouses", warehouses);
		model.addAttribute("products", products);
		
		Map<Product, Map<Long, Integer>> stock = new HashMap<Product, Map<Long, Integer>>();
		for(Product product : products) {

			stock.put(product, getStockLevels(product));
		}
		
		model.addAttribute("stockLevels", stock);
		
		WarehouseForm bindingForm = new WarehouseForm();
		model.addAttribute("warehouseForm", bindingForm);
		
		return "admin";
	}
	
	/**
	 * Create a new WarehouseServiceBinding manually
	 * @return
	 */
	@RequestMapping(value="/createWarehouse.html", method=RequestMethod.POST)
	public String createWarehouse(@ModelAttribute("warehouseForm") WarehouseForm bindingForm) {
		Warehouse warehouse = new Warehouse(bindingForm.getName());
		warehouseServiceLocator.bindWarehouseService(warehouse, bindingForm.getServiceName());
		return "redirect:admin.html";
	}
	
	@RequestMapping(value="/createProduct.html", method=RequestMethod.POST)
	public String createProduct(@RequestParam("name") String name, @RequestParam("description") String description, Model model) {
		productService.saveProduct(new Product(name, description));
		return "redirect:admin.html";
	}
	
	@RequestMapping(value="/product/{productId}/stock.html")
	public String enterStock(@PathVariable("productId") Long productId, Model model) {

		Set<Warehouse> warehouses = warehouseService.listWarehouses();
		Map<Long, Warehouse> warehouseMap = new HashMap<Long, Warehouse>();
		for(Warehouse w : warehouses) {
			warehouseMap.put(w.getId(), w);
		}
		Product product = productService.getProductById(productId);
		
		model.addAttribute("warehouseMap", warehouseMap);
		model.addAttribute("product", product);
		model.addAttribute("stockForm", getStockForm(product));
		
		
		return "enterStock";
	}
	
	@RequestMapping(value="/product/{productId}/stock.html", method=RequestMethod.POST)
	public String setStock(@ModelAttribute("stockForm") StockForm stockForm, @PathVariable("productId") Long productId) {
		for(Long warehouseId : stockForm.getStockLevels().keySet()) {
			Integer stockLevel = stockForm.getStockLevels().get(warehouseId);
			warehouseService.setStock(new StockChangeRequest(warehouseId, productId, stockLevel));
		}
		return "redirect:admin.html";
	}
	
	@RequestMapping("/stockAlerts.html")
	public String stockAlerts(Model model) {
		List<StockAlertDetails> stockAlerts = warehouseService.getStockAlerts();
		model.addAttribute("stockAlerts", stockAlerts);
		return "stockAlerts";
	}
	
	public StockForm getStockForm(Product product) {
		return new StockForm(getStockLevels(product));
	}
	
	public Map<Long, Integer> getStockLevels(Product product) {
		Map<Long, Integer> productStock = new HashMap<Long, Integer>();
		for(Warehouse w : warehouseService.listWarehouses()) {
			productStock.put(w.getId(),  warehouseService.getStockInWarehouse(new StockQueryRequest(w.getId(), product.getId())));
		}
		return productStock;
	}
}
