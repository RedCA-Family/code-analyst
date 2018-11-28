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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.samsungsds.analyst.code.ckmetrics.library.CKNumber;
import com.samsungsds.analyst.code.ckmetrics.library.CKReport;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class LCOM extends ASTVisitor implements Metric {

	ArrayList<TreeSet<String>> methods = new ArrayList<TreeSet<String>>();
	Set<String> declaredFields;
	
	public LCOM() {
		this.declaredFields = new HashSet<String>();
	}
	
	public boolean visit(FieldDeclaration node) {

		for (Object o : node.fragments()) {
			VariableDeclarationFragment vdf = (VariableDeclarationFragment) o;
			declaredFields.add(vdf.getName().toString());
		}
		
		return super.visit(node);
	}
	
	public boolean visit(SimpleName node) {
		String name = node.getFullyQualifiedName();
		if(declaredFields.contains(name)) {
			acessed(name);
		}
		
		return super.visit(node);
	}

	private void acessed(String name) {
		if(!methods.isEmpty()){
			methods.get(methods.size() - 1).add(name);
		}
	}
	
	public boolean visit(MethodDeclaration node) {
		methods.add(new TreeSet<String>());
		
		return super.visit(node);
	}
	
	@Override
	public void execute(CompilationUnit cu, CKNumber number, CKReport report) {
		cu.accept(this);
	}

	@Override
	public void setResult(CKNumber result) {
		
		/*
		 * LCOM = |P| - |Q| if |P| - |Q| > 0
		 * where
		 * P = set of all empty set intersections
		 * Q = set of all nonempty set intersections
		 */
		
		// extracted from https://github.com/dspinellis/ckjm
		int lcom = 0;
		for (int i = 0; i < methods.size(); i++)
		    for (int j = i + 1; j < methods.size(); j++) {

				TreeSet<?> intersection = (TreeSet<?>) methods.get(i).clone();
				intersection.retainAll(methods.get(j));
				if (intersection.size() == 0) lcom++;
				else lcom--;
		    }
		result.setLcom(lcom > 0 ? lcom : 0);
	}

}
