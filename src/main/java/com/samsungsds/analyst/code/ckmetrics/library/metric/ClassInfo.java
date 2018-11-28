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

Modified from CK metrics calculator(https://github.com/mauricioaniche/ck) under Apache 2.0 license
@author Mauricio Aniche
 */
package com.samsungsds.analyst.code.ckmetrics.library.metric;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClassInfo extends ASTVisitor {

	private String className;
	private String type;

	@Override
	public boolean visit(TypeDeclaration node) {
		
		getFullClassName(node.resolveBinding());
		
		if(node.isInterface()) type = "interface";
		else type = "class";
		
		return false;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		type = "enum";
		getFullClassName(node.resolveBinding());
		return false;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getType() {
		return type;
	}
	
	private void getFullClassName(ITypeBinding binding) {
		if (binding != null)
			this.className = binding.getBinaryName();
	}
	
}
