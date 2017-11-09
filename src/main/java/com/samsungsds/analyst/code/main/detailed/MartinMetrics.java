package com.samsungsds.analyst.code.main.detailed;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class MartinMetrics implements Serializable {
	private static final long serialVersionUID = -7097402415035127604L;
	
	@Expose
	private String packageName;
	@Expose
	private int afferentCoupling;
	@Expose
	private int efferentCoupling;
	@Expose
	private float abstractness;
	@Expose
	private float instability;
	@Expose
	private float distance;
	
	public MartinMetrics() {
		// no-op
	}
	
	public MartinMetrics(String packageName, int afferentCoupling, int efferentCoupling, float abstractness, float instability, float distance) {
		this.packageName = packageName;
		this.afferentCoupling = afferentCoupling;
		this.efferentCoupling = efferentCoupling;
		this.abstractness = abstractness;
		this.instability = instability;
		this.distance = distance;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getAfferentCoupling() {
		return afferentCoupling;
	}

	public void setAfferentCoupling(int afferentCoupling) {
		this.afferentCoupling = afferentCoupling;
	}

	public int getEfferentCoupling() {
		return efferentCoupling;
	}

	public void setEfferentCoupling(int efferentCoupling) {
		this.efferentCoupling = efferentCoupling;
	}

	public float getAbstractness() {
		return abstractness;
	}

	public void setAbstractness(float abstractness) {
		this.abstractness = abstractness;
	}

	public float getInstability() {
		return instability;
	}

	public void setInstability(float instability) {
		this.instability = instability;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
