package gv.api;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class OrderConfirmation {
	
	@XmlElement
	private Order order;
	
	@XmlElementWrapper(name = "shipments")
	@XmlElement(name="shipment", type=Shipment.class)
	private List<Shipment> shipments;
	
	public OrderConfirmation() {}
	
	public OrderConfirmation(Order order, List<Shipment> shipments) {
		super();
		this.order = order;
		this.shipments = shipments;
	}

	public Order getOrder() {
		return order;
	}

	public List<Shipment> getShipments() {
		return shipments;
	}
	
	public boolean isPartial() {
		for(OrderLine line : order.getLines()) {
			int missing = line.getQty();
			for(Shipment s : shipments) {
				missing -= s.getQty(line.getProductId());
			}
			
			if(missing > 0) {
				return true;
			}
		}
		
		return false;
	}

}
