package gv.warehouse.gemfire.entity;

import java.io.Serializable;

import org.springframework.data.gemfire.mapping.Region;


@Region("stock_levels")
public class StockLevel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@org.springframework.data.annotation.Id
	private final StockLevel.Id id;
	
	private final int qty;
	private final int oldQty;
	
	public StockLevel() {
		this(null, 0);
	}
	
	public StockLevel(Id id, int qty) {
		this.id = id;
		this.qty = qty;
		this.oldQty = 0;
	}
	
	public StockLevel(Id id, int oldQty, int newQty) {
		this.id = id;
		this.qty = newQty;
		this.oldQty = oldQty;
	}
	
	public StockLevel(String warehouseName, Long productId, int qty) {
		this.id = new Id(warehouseName, productId);
		this.qty = qty;
		this.oldQty = 0;
	}

	public StockLevel(String warehouseName, Long productId, int oldQty, int newQty) {
		this.id = new Id(warehouseName, productId);
		this.qty = newQty;
		this.oldQty = oldQty;
	}

	public int getQty() {
		return qty;
	}
	
	public int getOldQty() {
		return oldQty;
	}

	public Id getId() {
		return id;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + oldQty;
		result = prime * result + qty;
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
		if (oldQty != other.oldQty)
			return false;
		if (qty != other.qty)
			return false;
		return true;
	}



	public static class Id implements Serializable {
		
		private String warehouseName;
		
		private Long productId;
		
		public Id(String warehouseName, Long productId) {
			this.warehouseName = warehouseName;
			this.productId = productId;
		}
		
		public String getWarehouseId() {
			return warehouseName;
		}

		public void setWarehouseId(String warehouseName) {
			this.warehouseName = warehouseName;
		}

		public Long getProductId() {
			return productId;
		}

		public void setProductId(Long productId) {
			this.productId = productId;
		}
		
		public String toString() {
			return warehouseName+"_"+productId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((productId == null) ? 0 : productId.hashCode());
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
			Id other = (Id) obj;
			if (productId == null) {
				if (other.productId != null)
					return false;
			} else if (!productId.equals(other.productId))
				return false;
			if (warehouseName == null) {
				if (other.warehouseName != null)
					return false;
			} else if (!warehouseName.equals(other.warehouseName))
				return false;
			return true;
		}
	}
}
