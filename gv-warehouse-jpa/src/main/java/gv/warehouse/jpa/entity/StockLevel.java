package gv.warehouse.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="stockLevels")
public class StockLevel {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private Long warehouseId;
	
	private Long productId;
	
	private int qty;
	
	public StockLevel() {
		
	}
	
	public StockLevel(Long warehouseId, Long productId, int qty) {
		this.warehouseId = warehouseId;
		this.productId = productId;
		this.qty = qty;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public void addQty(int delta) {
		this.qty += delta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + qty;
		result = prime * result
				+ ((warehouseId == null) ? 0 : warehouseId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StockLevel other = (StockLevel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (qty != other.qty)
			return false;
		if (warehouseId == null) {
			if (other.warehouseId != null)
				return false;
		} else if (!warehouseId.equals(other.warehouseId))
			return false;
		return true;
	}
	

	
}
