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
package com.samsungsds.analyst.code.unusedcode;

import java.util.ArrayList;
import java.util.List;

import  org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.samsungsds.analyst.code.unusedcode.type.CAClass;
import com.samsungsds.analyst.code.unusedcode.type.CAConstant;
import com.samsungsds.analyst.code.unusedcode.type.CAField;
import com.samsungsds.analyst.code.unusedcode.type.CAMethod;
import com.samsungsds.analyst.code.util.TypeUtil;

public class CodeAnalysisSourceVisitor extends VoidVisitorAdapter<Void> {

	private static final Logger LOGGER = LogManager.getLogger(CodeAnalysisSourceVisitor.class);

	VisitResult result;

	String className;
	String packageName;

	List<String> importListWithClassName = new ArrayList<>();
	List<String> importListWithAsterisk = new ArrayList<>();


	public CodeAnalysisSourceVisitor(VisitResult result) {
		this.result = result;
	}

	@Override
	public void visit(CompilationUnit n, Void arg) {
//		LOGGER.debug(String.format("visit CompilationUnit %s", n.getPackageDeclaration().get().getNameAsString()));
		if(n.getPackageDeclaration().isPresent()) {
			this.packageName = n.getPackageDeclaration().get().getNameAsString();
		} else {
			this.packageName = "";
		}

		this.className = this.packageName;
		super.visit(n, arg);
	}

	@Override
	public void visit(ImportDeclaration n, Void arg) {
//		LOGGER.debug(String.format("visit ImportDeclaration|| %s %s", n.getNameAsString(), n.getTokenRange()));
		setImportInfo(n);
		super.visit(n, arg);
	}

	private void setImportInfo(ImportDeclaration n) {
		if(n.getTokenRange().toString().indexOf("*") > -1) {
			importListWithAsterisk.add(n.getNameAsString());
		} else {
			importListWithClassName.add(n.getNameAsString());
		}
	}

	private String getFullClassNameBy(String type) {
		if(TypeUtil.isPrimitiveType(type)) return type;
		type = removeGeneric(type);

		String fullClassName = "java.lang."+type;
		for (String expectedClassName : importListWithClassName) {
			String name = expectedClassName.substring(expectedClassName.lastIndexOf(".")+1);
			if(name.equals(type)) {
				return expectedClassName;
			}
		}

		try {
			String expectedClassName = this.packageName+"."+type;
			Class.forName(expectedClassName);
			return expectedClassName;
		} catch (ClassNotFoundException e) {
			//DO NOTHING
		} catch (NoClassDefFoundError e) {
			//DO NOTHING
		}

		for (String packageName : importListWithAsterisk) {
			try {
				String expectedClassName = packageName+"."+type;
				Class.forName(expectedClassName);
				return expectedClassName;
			} catch (ClassNotFoundException e) {
				//DO NOTHING
			}
		}


		return fullClassName;
	}

	private String removeGeneric(String typeString) {
		return typeString.indexOf("<") > -1 ? typeString.substring(0, typeString.indexOf("<")) : typeString;
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
//		LOGGER.debug(String.format("visit ClassOrInterfaceDeclaration %s", n.getNameAsString()));
		if(this.className.equals(this.packageName)) {
			this.className += (this.packageName.equals("") ? "" : ".") + n.getNameAsString();
		}
		super.visit(n, arg);
	}

	@Override
	public void visit(MethodDeclaration n, Void arg) {
//		LOGGER.debug(String.format("visit Method %s %s %s %s %s %s", n.getSignature(), n.getModifiers(), n.getName(), n.getParameters(), n.getType(), n.getRange().get().begin.line));
		CAMethod method = new CAMethod();
		method.setName(n.getNameAsString());
		method.setClassName(this.className);
		method.setLine(n.getRange().get().begin.line);
		String[] parameterTypes = new String[n.getParameters().size()];
 		for(int i = 0 ; i < parameterTypes.length ; i++) {
			parameterTypes[i] = getFullClassNameBy(n.getParameter(i).getType().toString());
		}
		method.setParameterTypes(parameterTypes);

		setMethodLineFrom(method);
		super.visit(n, arg);
	}

	private void setMethodLineFrom(CAMethod method) {
		for (CAMethod cam : result.getTempMethods()) {
			if(cam.equals(method)) {
				cam.setLine(method.getLine());
				return;
			}
		}
	}

	@Override
	public void visit(FieldDeclaration n, Void arg) {
//		LOGGER.debug(String.format("visit Field|| %s %s %s %s", n.getElementType(), n.getModifiers(), n.getVariables(), n.getRange().get().begin.line));
		for (VariableDeclarator v : n.getVariables()) {
			if(n.getModifiers().contains(Modifier.staticModifier()) && n.getModifiers().contains(Modifier.finalModifier())) {
				CAConstant constant = new CAConstant();
				constant.setTypeName(v.getType().toString());
				constant.setType(getFullClassNameBy(v.getType().toString()));
				constant.setName(v.getNameAsString());
				constant.setClassName(this.className);
				constant.setLine(n.getRange().get().begin.line);
				setConstantLineFrom(constant);
			} else {
				CAField field = new CAField();
				field.setTypeName(v.getType().toString());
				field.setType(getFullClassNameBy(v.getType().toString()));
				field.setName(v.getNameAsString());
				field.setClassName(this.className);
				field.setLine(n.getRange().get().begin.line);
				setFieldLineFrom(field);
			}
		}

		super.visit(n, arg);
	}

	private void setFieldLineFrom(CAField field) {
		for (CAField caf : result.getTempFields()) {
			if(caf.equals(field)) {
				caf.setLine(field.getLine());
				break;
			}
		}
	}

	private void setConstantLineFrom(CAConstant constant) {
		for (CAConstant cac : result.getTempConstants()) {
			if(cac.equals(constant)) {
				cac.setLine(constant.getLine());
				break;
			}
		}
	}

	@Override
	public void visit(NameExpr n, Void arg) {
		//자기 클래스의 contant 필드 사용 detection
//		LOGGER.debug(String.format("visit NameExpr|| %s %s %s", this.className, n, n.getRange().get().begin.line));

		CAConstant usedConstant = new CAConstant();
		usedConstant.setClassName(this.className);
		usedConstant.setName(n.getNameAsString());
		result.getUsedConstants().add(usedConstant);
		super.visit(n, arg);
	}

	@Override
	public void visit(FieldAccessExpr n, Void arg) {
		//클래스.필드 형태의 코드는 이곳에서 detection 가능
//		LOGGER.debug(String.format("visit FieldAccessExpr|| %s %s %s %s", this.className, n.getScope(), n.getNameAsString(), n.getRange().get().begin.line));

		if(!n.getScope().toString().equals("this")) {
			CAConstant usedConstant = new CAConstant();
			usedConstant.setClassName(getFullClassNameBy(n.getScope().toString()));
			usedConstant.setName(n.getNameAsString());
			result.getUsedConstants().add(usedConstant);

//			System.out.println(usedConstant.getClassName());

			CAClass usedClass = new CAClass();
			usedClass.setName(usedConstant.getClassName());
			result.getUsedClasses().add(usedClass);
		}

		super.visit(n, arg);
	}
}
