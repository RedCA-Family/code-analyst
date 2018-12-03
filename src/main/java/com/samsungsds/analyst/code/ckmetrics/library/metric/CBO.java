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
import org.eclipse.jdt.core.dom.*;

import java.util.HashSet;
import java.util.Set;


public class CBO extends ASTVisitor implements Metric {

	private Set<String> coupling = new HashSet<String>();

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		coupleTo(node.getType().resolveBinding());
		return super.visit(node);
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		coupleTo(node.getType().resolveBinding());
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayCreation node) {
		coupleTo(node.getType().resolveBinding());
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		coupleTo(node.getType().resolveBinding());
		return super.visit(node);
	}

	public boolean visit(ReturnStatement node) {
		if (node.getExpression() != null) {
			coupleTo(node.getExpression().resolveTypeBinding());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeLiteral node) {
		coupleTo(node.resolveTypeBinding());
		coupleTo(node.getType().resolveBinding());
		return super.visit(node);
	}
	
	public boolean visit(ThrowStatement node) {
		coupleTo(node.getExpression().resolveTypeBinding());
		return super.visit(node);
	}

	public boolean visit(TypeDeclaration node) {
		ITypeBinding type = node.resolveBinding();

		ITypeBinding binding = type.getSuperclass();
		if (binding != null)
			coupleTo(binding);

		for (ITypeBinding interfaces : type.getInterfaces()) {
			coupleTo(interfaces);
		}

		return super.visit(node);
	}

	public boolean visit(MethodDeclaration node) {

		IMethodBinding method = node.resolveBinding();
		if (method == null)
			return super.visit(node);

		coupleTo(method.getReturnType());

		for (ITypeBinding param : method.getParameterTypes()) {
			coupleTo(param);
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(CastExpression node) {
		coupleTo(node.getType().resolveBinding());

		return super.visit(node);
	}

	@Override
	public boolean visit(InstanceofExpression node) {

		coupleTo(node.getRightOperand().resolveBinding());
		coupleTo(node.getLeftOperand().resolveTypeBinding());

		return super.visit(node);
	}

	public boolean visit(NormalAnnotation node) {
		coupleTo(node.resolveTypeBinding());
		return super.visit(node);
	}

	public boolean visit(MarkerAnnotation node) {
		coupleTo(node.resolveTypeBinding());
		return super.visit(node);
	}

	public boolean visit(SingleMemberAnnotation node) {
		coupleTo(node.resolveTypeBinding());
		return super.visit(node);
	}

	public boolean visit(ParameterizedType node) {
		ITypeBinding binding = node.resolveBinding();
		if (binding == null)
			return super.visit(node);

		coupleTo(binding);

		for (ITypeBinding types : binding.getTypeArguments()) {
			coupleTo(types);
		}

		return super.visit(node);
	}

	private void coupleTo(ITypeBinding binding) {
		if (binding == null)
			return;
		if (binding.isWildcardType())
			return;

		String type = binding.getQualifiedName();
		if (type.equals("null"))
			return;

		if (!isFromJava(type) && !binding.isPrimitive())
			coupling.add(type.replace("[]", ""));
	}

	private boolean isFromJava(String type) {
		return type.startsWith("java.") || type.startsWith("javax.");
	}

	@Override
	public void execute(CompilationUnit cu, CKNumber number, CKReport report) {
		cu.accept(this);
	}

	@Override
	public void setResult(CKNumber result) {
		result.setCbo(coupling.size());
	}
}
