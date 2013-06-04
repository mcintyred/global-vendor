package gv.core.service.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This entity is used by the core system to bind Warehouses to the appropriate repositories
 * 
 * @author mcintyred
 *
 */

@Entity
public class WarehouseServiceBinding {
	
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String serviceBindingName;
	
	public WarehouseServiceBinding() { }
	
	public WarehouseServiceBinding(Long id, String name , String binding) {
		this.id = id;
		this.name = name;
		this.serviceBindingName = binding;
	}
	
	public WarehouseServiceBinding(String name , String binding) {
		this.name = name;
		this.serviceBindingName = binding;
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

	public String getServiceBindingName() {
		return serviceBindingName;
	}

	public void setServiceBindingName(String serviceBindingName) {
		this.serviceBindingName = serviceBindingName;
	}

}
