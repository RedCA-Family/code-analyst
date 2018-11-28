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

import com.samsungsds.analyst.code.ckmetrics.library.CKNumber;
import com.samsungsds.analyst.code.ckmetrics.library.CKReport;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class NOC extends ASTVisitor implements Metric {

	private CKReport report;
	private NOCExtras extras;

	public NOC(NOCExtras extras) {
		this.extras = extras;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		ITypeBinding father = binding.getSuperclass();
		if (father != null) {
			CKNumber fatherCk = report.getByClassName(father.getBinaryName());
			if (fatherCk != null) fatherCk.incNoc();
			else extras.plusOne(father.getBinaryName());
		}

		return false;
	}

	@Override
	public void execute(CompilationUnit cu, CKNumber number, CKReport report) {
		this.report = report;
		cu.accept(this);
	}

	@Override
	public void setResult(CKNumber result) {
	}
}
