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
	
	public StockLevel(Long warehouseId, Long productId, int qty) {
		this.id = new Id(warehouseId, productId);
		this.qty = qty;
		this.oldQty = 0;
	}

	public StockLevel(Long warehouseId, Long productId, int oldQty, int newQty) {
		this.id = new Id(warehouseId, productId);
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
		
		private Long warehouseId;
		
		private Long productId;
		
		public Id(Long warehouseId, Long productId) {
			this.warehouseId = warehouseId;
			this.productId = productId;
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
		
		public String toString() {
			return warehouseId+"_"+productId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((productId == null) ? 0 : productId.hashCode());
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
			Id other = (Id) obj;
			if (productId == null) {
				if (other.productId != null)
					return false;
			} else if (!productId.equals(other.productId))
				return false;
			if (warehouseId == null) {
				if (other.warehouseId != null)
					return false;
			} else if (!warehouseId.equals(other.warehouseId))
				return false;
			return true;
		}
	}
}
