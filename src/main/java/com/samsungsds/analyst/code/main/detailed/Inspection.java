package com.samsungsds.analyst.code.main.detailed;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class Inspection implements Serializable {
	private static final long serialVersionUID = -6519277475012573492L;

	@Expose
	private String rule;
	
	@Expose
	private int count;
	
	public Inspection(String rule) {
		this.rule = rule;
	}
	
	public String getRule() {
		return rule;
	}

	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Inspection other = (Inspection) obj;
		if (rule == null) {
			if (other.rule != null) {
				return false;
			}
		} else if (!rule.equals(other.rule)) {
			return false;
		}
		return true;
	}
	
	
}
