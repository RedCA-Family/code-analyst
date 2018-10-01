/*
Copyright 2018 Samsung SDS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.samsungsds.analyst.code.unusedcode.type;

import java.util.Arrays;

import org.springframework.asm.Type;

public class CAMethod extends CAType {
	private String desc;
	private String[] parameterTypes;
	private String returnType;
	private String[] exceptions;
	private String className;
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		Type[] parameterTypes = Type.getArgumentTypes(desc);
		
		this.parameterTypes = new String[parameterTypes.length];
		for(int i = 0 ; i < parameterTypes.length ; i++) {
			this.parameterTypes[i] = parameterTypes[i].getClassName();
		}
		
		this.desc = desc;
	}
	
	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public String[] getParameterTypes() {
		return parameterTypes;
	}
	public String getReturnType() {
		return returnType;
	}
	public String[] getExceptions() {
		return exceptions;
	}
	public void setExceptions(String[] exceptions) {
		this.exceptions = exceptions;
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
		result = prime * result + Arrays.hashCode(parameterTypes);
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
		CAMethod other = (CAMethod) obj;
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
		if (! Arrays.equals(parameterTypes, other.parameterTypes))
			return false;
		return true;
	}
}
