package gv.api;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Shipment {
	
	@XmlElement
	private Warehouse warehouse;
	
	@XmlElementWrapper(name = "lines")
	@XmlElement(name="line", type=ShipmentLine.class)
	private List<ShipmentLine> lines;
	
	public Shipment() {}
	
	public Shipment(Warehouse warehouse, List<ShipmentLine> lines) {
		this.warehouse = warehouse;
		this.lines = lines;
	}

	public List<ShipmentLine> getLines() {
		return lines;
	}
	
	public Warehouse getWarehouse() {
		return warehouse;
	}
	
	public int getQty(Long productId) {
		
		int qty = 0;
		
		for(ShipmentLine line : lines) {
			if(line.getProduct().getId().equals(productId)) {
				qty += line.getQty();
			}
		}
		
		return qty;
	}
}
