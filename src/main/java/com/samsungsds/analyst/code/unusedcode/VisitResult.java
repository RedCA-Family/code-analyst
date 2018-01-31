package com.samsungsds.analyst.code.unusedcode;

import java.util.HashSet;
import java.util.Set;

import com.samsungsds.analyst.code.unusedcode.type.CAClass;
import com.samsungsds.analyst.code.unusedcode.type.CAConstant;
import com.samsungsds.analyst.code.unusedcode.type.CAField;
import com.samsungsds.analyst.code.unusedcode.type.CAMethod;

public class VisitResult {
	private boolean skipThisClass = false;
	
	private Set<CAClass> classes = new HashSet<>();
	private Set<CAClass> usedClasses = new HashSet<>();
	private Set<CAField> fields = new HashSet<>();
	private Set<CAField> usedFields = new HashSet<>();
	private Set<CAMethod> methods = new HashSet<>();
	private Set<CAMethod> usedMethods = new HashSet<>();
	private Set<CAConstant> contants = new HashSet<>();
	private Set<CAConstant> usedConstants = new HashSet<>();
	
	//for setting line number
	private Set<CAMethod> tempMethods = new HashSet<>();
	private Set<CAField> tempFields = new HashSet<>();
	private Set<CAConstant> tempConstants = new HashSet<>();
	
	public boolean skipThisClass() {
		return skipThisClass;
	}
	public void setSkipThisClass(boolean skipThisClass) {
		this.skipThisClass = skipThisClass;
	}
	public Set<CAClass> getClasses() {
		return classes;
	}
	public void setClasses(Set<CAClass> classes) {
		this.classes = classes;
	}
	public Set<CAClass> getUsedClasses() {
		return usedClasses;
	}
	public void setUsedClasses(Set<CAClass> usedClasses) {
		this.usedClasses = usedClasses;
	}
	public Set<CAField> getFields() {
		return fields;
	}
	public void setFields(Set<CAField> fields) {
		this.fields = fields;
	}
	public Set<CAField> getUsedFields() {
		return usedFields;
	}
	public void setUsedFields(Set<CAField> usedFields) {
		this.usedFields = usedFields;
	}
	public Set<CAMethod> getMethods() {
		return methods;
	}
	public void setMethods(Set<CAMethod> methods) {
		this.methods = methods;
	}
	public Set<CAMethod> getUsedMethods() {
		return usedMethods;
	}
	public void setUsedMethods(Set<CAMethod> usedMethods) {
		this.usedMethods = usedMethods;
	}
	public Set<CAConstant> getContants() {
		return contants;
	}
	public void setContants(Set<CAConstant> contants) {
		this.contants = contants;
	}
	public Set<CAConstant> getUsedConstants() {
		return usedConstants;
	}
	public void setUsedConstants(Set<CAConstant> usedConstants) {
		this.usedConstants = usedConstants;
	}
	public Set<CAMethod> getTempMethods() {
		return tempMethods;
	}
	public void setTempMethods(Set<CAMethod> tempMethods) {
		this.tempMethods = tempMethods;
	}
	public Set<CAField> getTempFields() {
		return tempFields;
	}
	public void setTempFields(Set<CAField> tempFields) {
		this.tempFields = tempFields;
	}
	public Set<CAConstant> getTempConstants() {
		return tempConstants;
	}
	public void setTempConstants(Set<CAConstant> tempConstants) {
		this.tempConstants = tempConstants;
	}
}
