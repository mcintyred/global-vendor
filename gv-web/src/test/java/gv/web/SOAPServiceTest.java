package gv.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import gv.api.Product;
import gv.api.Warehouse;
import gv.core.service.DistributedWarehouseService;
import gv.core.service.WarehouseServiceLocator;
import gv.orders.api.OrderService;
import gv.products.api.ProductService;
import gv.warehouse.api.StockChangeRequest;
import gv.warehouse.api.WarehouseService;

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ws.SimpleWebServiceInboundGateway;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.pox.dom.DomPoxMessage;
import org.springframework.ws.pox.dom.DomPoxMessageFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * This test uses a configuration with two local warehouses to verify operation of the ws-inbound-gateway
 */
@ContextConfiguration()
@RunWith(SpringJUnit4ClassRunner.class)
public class SOAPServiceTest {

	@Autowired
	private SimpleWebServiceInboundGateway gateway;
	
	@Autowired
	private DistributedWarehouseService warehouseService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private WarehouseServiceLocator warehouseServiceLocator;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private WarehouseService localWarehouseService;
	
	private Product widget;
	private Warehouse london;
	private Warehouse paris;
	
	@Before
	public void setup() {
		london = new Warehouse(1L, "London");
		paris = new Warehouse(2L, "Paris");
		warehouseServiceLocator.bindWarehouseService(london, localWarehouseService.getName());
		warehouseServiceLocator.bindWarehouseService(paris, localWarehouseService.getName());
		
		widget = new Product("Widget", "The new improved Widget2.0");
		productService.saveProduct(widget);
		
		warehouseService.setStock(new StockChangeRequest(london.getId(), widget.getId(), 22));
		warehouseService.setStock(new StockChangeRequest(paris.getId(), widget.getId(), 33));
	}

	@Test
	public void testSendAndReceive() throws Exception {
		String xml = 
				"<gv:order xmlns:gv=\"http://www.gv.demo/api\">" +
					"<gv:lines>" +
						"<gv:line productId=\""+widget.getId()+"\" qty=\"44\"/>" +
					"</gv:lines>" +
				"</gv:order>";
		
		DomPoxMessageFactory messageFactory = new DomPoxMessageFactory();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DomPoxMessage request = new DomPoxMessage(document, transformer, "text/xml");
		MessageContext messageContext = new DefaultMessageContext(request, messageFactory);
		gateway.invoke(messageContext);
		Object reply = messageContext.getResponse().getPayloadSource();
		assertThat(reply, is(DOMSource.class));
		DOMSource replySource = (DOMSource) reply;
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		xpath.setNamespaceContext(new NS());
		 
		String expr = "//gv:orderConfirmation/gv:shipments/gv:shipment/gv:warehouse[@id='"+london.getId()+"']/../gv:lines/gv:line/gv:product[@id='"+widget.getId()+"']/../@qty";
		String londonQty = xpath.evaluate(expr, replySource.getNode());
		assertEquals("11", londonQty);
		 
		expr = "//gv:orderConfirmation/gv:shipments/gv:shipment/gv:warehouse[@id='"+paris.getId()+"']/../gv:lines/gv:line/gv:product[@id='"+widget.getId()+"']/../@qty";
		String parisQty = xpath.evaluate(expr, replySource.getNode());
		assertEquals("33", parisQty);
	}
	
	
	public class NS implements NamespaceContext {

		@Override
		public String getNamespaceURI(String prefix) {
			return "http://www.gv.demo/api";
		}

		@Override
		public String getPrefix(String namespaceURI) {
			return "gv";
		}

		@Override
		public Iterator getPrefixes(String namespaceURI) {
			// TODO Auto-generated method stub
			return null;
		}	
	}
}