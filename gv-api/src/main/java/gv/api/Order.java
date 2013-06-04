package gv.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An Order is a request for a given quantity of one or more Products.
 * 
 * @author mcintyred
 *
 */

@XmlRootElement()
public class Order {
	
	@XmlElementWrapper(name = "lines")
	@XmlElement(name="line", type = OrderLine.class)
	private List<OrderLine> lines;
	
	public Order() {
		
	}

	public Order(List<OrderLine> lines) {
		this.lines = lines;
	}
	
	public void setLines(List<OrderLine> lines) {
		this.lines = lines;
	}

	public List<OrderLine> getLines() {
		if(lines == null) {
			lines = new ArrayList<OrderLine>();
		}
		return lines;
	}
}
