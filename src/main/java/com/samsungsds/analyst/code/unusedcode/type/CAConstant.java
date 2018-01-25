package com.samsungsds.analyst.code.unusedcode.type;

public class CAConstant extends CAType {
	private String desc;
	private Object value;
	private String typeName;
	private String type;
	
	private String className;

	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		if(desc == null || desc.isEmpty()) {
			throw new IllegalArgumentException("wrong desc format");
		}
		
		this.typeName = desc.substring(desc.lastIndexOf("/")+1, desc.length()-1);
		this.desc = desc;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
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
		CAConstant other = (CAConstant) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
