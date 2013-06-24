package gv.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrderResponse {
	
	@XmlElement(name="confirmation")
	private OrderConfirmation confirmation;
	
	public OrderResponse() {
		
	}

	public OrderConfirmation getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(OrderConfirmation confirmation) {
		this.confirmation = confirmation;
	}

}
