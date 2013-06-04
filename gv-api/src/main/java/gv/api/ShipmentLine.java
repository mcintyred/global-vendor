package gv.api;

import gv.jaxb.LocalDateAdaptor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

public class ShipmentLine {

	@XmlAttribute
	@XmlJavaTypeAdapter(value = LocalDateAdaptor.class)
	private LocalDate shipDate;
	
	@XmlAttribute
	private int qty;
	
	@XmlElement
	private Product product;

	public ShipmentLine() {
	}

	public ShipmentLine(LocalDate shipDate, int qty, Product product) {
		this.shipDate = shipDate;
		this.qty = qty;
		this.product = product;
	}

	public LocalDate getShipDate() {
		return shipDate;
	}

	public int getQty() {
		return qty;
	}

	public Product getProduct() {
		return product;
	}

}
