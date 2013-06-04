package gv.api;

import javax.xml.bind.annotation.XmlAttribute;

public class Warehouse {

	@XmlAttribute
	private Long id;
	
	@XmlAttribute
	private String name;

	public Warehouse(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Warehouse(String name) {
		this(null, name);
	}
	
	public Warehouse() {
		this(null, null);
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Warehouse [id=" + id + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Warehouse other = (Warehouse) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
