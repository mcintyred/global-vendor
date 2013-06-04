package gv.api;

import javax.xml.bind.annotation.XmlAttribute;

public class OrderLine {
	
	@XmlAttribute
	private long productId;
	
	@XmlAttribute
	private int qty;
	
	public OrderLine() {}
	
	public OrderLine(long productId, int qty) {
		this.productId = productId;
		this.qty = qty;
	}

	public long getProductId() {
		return productId;
	}

	public int getQty() {
		return qty;
	}
}
