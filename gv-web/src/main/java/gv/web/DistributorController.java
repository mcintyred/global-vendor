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
import gv.distributor.api.DistributorService;
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
public class DistributorController {
	
	@Autowired
	private DistributorService distributorService;

	@RequestMapping("/index.html")
	public String index(Model model) {

		List<Product> products = distributorService.listProducts();
		model.addAttribute("products", products);
		
		Map<Product, Integer> stock = new HashMap<Product, Integer>();
		for(Product product : products) {

			stock.put(product, (Integer)distributorService.getTotalStock(product.getId()));
		}
		
		model.addAttribute("stockLevels", stock);
		
		return "index";
	}
	
	@RequestMapping("/product/{productId}/purchase.html")
	public String purchaseForm(@PathVariable("productId") Long productId, Model model) {
		model.addAttribute("product", distributorService.getProductById(productId));
		return "purchaseForm";
	}
	
	@RequestMapping(value="/product/{productId}/purchase.html", method=RequestMethod.POST)
	public String purchase(@PathVariable("productId") Long productId, @RequestParam("qty") Integer qty, Model model) {
		OrderLine orderLine = new OrderLine(productId, qty);
		Order order = new Order(Lists.newArrayList(orderLine));
		OrderConfirmation confirmation = distributorService.placeOrder(order);
		model.addAttribute("confirmation", confirmation);
		return "orderConfirmation";
	}
}
