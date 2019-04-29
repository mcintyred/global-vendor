package gv.warehouse.cassandra.service.entity;

import org.springframework.data.cassandra.mapping.Indexed;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table(name="stock_levels")
public class StockLevel {
	
	@PrimaryKey
	private Long id;
	
	@Indexed
	private String warehouseName;
	
	@Indexed
	private Long productId;
	
	private int qty;
	
	public StockLevel() {
		
	}
	
	public StockLevel(String warehouseId, Long productId, int qty) {
		this.warehouseName = warehouseId;
		this.productId = productId;
		this.qty = qty;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseId) {
		this.warehouseName = warehouseId;
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
				+ ((warehouseName == null) ? 0 : warehouseName.hashCode());
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
		if (warehouseName == null) {
			if (other.warehouseName != null)
				return false;
		} else if (!warehouseName.equals(other.warehouseName))
			return false;
		return true;
	}
	

	
}
