package gv.api;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Product {
	
	@XmlAttribute
	private Long id;
	
	@XmlAttribute
	private String name;
	
	@XmlElement
	private String description;
	
	public Product(Long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public Product(String name, String description) {
		this(null, name, description);
	}
	
	public Product() {
		
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
