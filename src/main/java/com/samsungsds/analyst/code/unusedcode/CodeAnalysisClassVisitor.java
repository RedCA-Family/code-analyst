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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.samsungsds.analyst.code.unusedcode.type.CAClass;
import com.samsungsds.analyst.code.unusedcode.type.CAConstant;
import com.samsungsds.analyst.code.unusedcode.type.CAField;
import com.samsungsds.analyst.code.unusedcode.type.CAMethod;
import com.samsungsds.analyst.code.util.TypeUtil;

public class CodeAnalysisClassVisitor extends ClassVisitor {

	private static final Logger LOGGER = LogManager.getLogger(CodeAnalysisClassVisitor.class);

	VisitResult result;

	String className;

	public CodeAnalysisClassVisitor(int api, VisitResult result) {
		super(api);
		this.result = result;
		result.setSkipThisClass(false);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName,
			String[] interfaces) {
//		LOGGER.debug(String.format("#visit %s %s %s %s", name, signature, superName, interfaces));
		this.className = TypeUtil.formatClassNameFromSlashToDot(name);
		String formattedSuperName = TypeUtil.formatClassNameFromSlashToDot(superName);
		String formattedSignature = TypeUtil.formatClassNameFromSlashToDot(signature);
		String[] formattedInterfaces = TypeUtil.formatClassNamesFormSlashToDot(interfaces);

		if(isEnumClass(formattedSuperName)) {
			this.result.setSkipThisClass(true);
			return;
		}

		addClassToResult(access, this.className, formattedSignature, formattedSuperName, formattedInterfaces);

		addUsedClassToResult(formattedSuperName);
		if(formattedInterfaces.length > 0) {
			for (String interfaceName : formattedInterfaces) {
				addUsedClassToResult(interfaceName);
			}
		}

		super.visit(version, access, this.className, signature, superName, interfaces);
	}

	private boolean isEnumClass(String superName) {
		return superName != null && superName.equals("java.lang.Enum");
	}

	private void addUsedClassToResult(String className) {
		if(className != null
		&& !className.startsWith("java.lang.")
		&& !className.startsWith("java.util.")) {
			CAClass superClass = new CAClass();
			superClass.setName(className);
			result.getUsedClasses().add(superClass);
		}
	}

	private void addUsedClassesToResult(List<String> classNames) {
		for (String className : classNames) {
			addUsedClassToResult(className);
		}
	}

	private void addClassToResult(int access, String name, String signature, String superName, String[] interfaces) {
		CAClass caClass = new CAClass();
		caClass.setName(name);
		caClass.setAccess(access);
		caClass.setSignature(signature);
		caClass.setSuperName(superName);
		caClass.setInterfaces(interfaces);
		result.getClasses().add(caClass);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
//		LOGGER.debug(String.format("###visitField %d %s %s", access, desc, name));
		if(result.skipThisClass()) {//enum skip
			return null;
		}

		String formattedSignature = TypeUtil.formatClassNameFromSlashToDot(signature);

		if(isConstant(access)) {
			addConstantToResult(access, name, desc, formattedSignature, value);
		} else {
			addFieldToResult(access, name, desc, formattedSignature, value);
		}

		addUsedClassToResult(Type.getType(desc).getClassName());

		return super.visitField(access, name, desc, signature, value);
	}

	private void addFieldToResult(int access, String name, String desc, String signature, Object value) {
		CAField field = new CAField();
		field.setClassName(className);
		field.setName(name);
		field.setAccess(access);
		field.setDesc(desc);
		field.setSignature(signature);
		field.setValue(value);
		result.getFields().add(field);
		result.getTempFields().add(field);
	}

	private void addConstantToResult(int access, String name, String desc, String signature, Object value) {
		CAConstant constant = new CAConstant();
		constant.setClassName(className);
		constant.setName(name);
		constant.setAccess(access);
		constant.setDesc(desc);
		constant.setSignature(signature);
		constant.setValue(value);
		result.getContants().add(constant);
		result.getTempConstants().add(constant);
	}

	private boolean isConstant(int access) {
		return (Opcodes.ACC_FINAL & access) == Opcodes.ACC_FINAL &&
				(Opcodes.ACC_STATIC & access) == Opcodes.ACC_STATIC;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature,
			String[] exceptions) {
//		LOGGER.debug(String.format("###visitMethod %s %s %s", name, desc, signature));
		if(result.skipThisClass()) {//enum skip
			return null;
		}

		//MethodVisitor 내의 method에서 사용하기 위한 변수 할당
		String methodName = name;
		String methodDesc = desc;

		String formattedSignature = TypeUtil.formatClassNameFromSlashToDot(signature);

		addMethodToResult(access, name, desc, formattedSignature, exceptions);

		addUsedClassesToResult(getParameterAndReturnClassNames(desc));

		MethodVisitor methodVisitor = new MethodVisitor(Opcodes.ASM7, super.visitMethod(access, name, desc, signature, exceptions)) {

			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				LOGGER.debug(String.format("#####visitAnnotation %s %s %s", className, desc, methodDesc));

				String requestMappingDesc = "Lorg/springframework/web/bind/annotation/RequestMapping;";
				if(desc.equals(requestMappingDesc)) {
					CAMethod caMethod = new CAMethod();
					caMethod.setClassName(className);
					caMethod.setName(methodName);
					caMethod.setDesc(methodDesc);
					result.getUsedMethods().add(caMethod);
					addUsedClassToResult(className);
				}

				return super.visitAnnotation(desc, visible);
			}

			@Override
			public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//				LOGGER.debug(String.format("#####visitFieldInsn %s %s %s %s", className, desc, name, owner));

				String formattedOwner = TypeUtil.formatClassNameFromSlashToDot(owner);
				//add used fields
				CAField field = new CAField();
				field.setClassName(formattedOwner);
				field.setName(name);
				field.setDesc(desc);
				result.getUsedFields().add(field);

				if(!className.equals(formattedOwner)) {
					addUsedClassToResult(formattedOwner);
				}

				super.visitFieldInsn(opcode, owner, name, desc);
			}

			@Override
			public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//				LOGGER.debug(String.format("#####visitMethodInsn %s %s %s %b", owner, name, desc, itf));

				String formattedOwner = TypeUtil.formatClassNameFromSlashToDot(owner);
				//add used methods
				CAMethod method = new CAMethod();
				method.setClassName(formattedOwner);
				method.setName(name);
				method.setDesc(desc);
				result.getUsedMethods().add(method);

				if(!className.equals(formattedOwner)) {
					addUsedClassToResult(formattedOwner);
				}

				//add used methods for superclass and interface
//				try {
//					Class superClass = Class.forName(formattedOwner).getSuperclass();
//					if(superClass != null) {
//						String superClassName = superClass.getName();
//
//						CAMethod superClassMethod = new CAMethod();
//						superClassMethod.setClassName(superClassName);
//						superClassMethod.setName(name);
//						superClassMethod.setDesc(desc);
//						result.getUsedMethods().add(superClassMethod);
//
//						addUsedClassToResult(superClassName);
//					}
//				} catch (ClassNotFoundException e) {
//					LOGGER.error(e);
//				}



				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		};

		return methodVisitor;
	}

	private List<String> getParameterAndReturnClassNames(String desc) {
		Type[] parameterTypes = Type.getArgumentTypes(desc);
		List<String> parameterClassNames = new ArrayList<String>();
		for (Type type : parameterTypes) {
			parameterClassNames.add(type.getClassName());
		}

		parameterClassNames.add(Type.getReturnType(desc).getClassName());

		return parameterClassNames;
	}

	private void addMethodToResult(int access, String name, String desc, String signature, String[] exceptions) {
		if(name.startsWith("<") || name.indexOf("$") > -1) return;

		CAMethod method = new CAMethod();
		method.setAccess(access);
		method.setName(name);
		method.setDesc(desc);
		method.setSignature(signature);
		method.setExceptions(exceptions);
		method.setClassName(className);
		result.getMethods().add(method);
		result.getTempMethods().add(method);
	}
}
