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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.github.javaparser.ParseResult;
import com.samsungsds.analyst.code.util.FindFileUtils;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.unusedcode.type.CAClass;
import com.samsungsds.analyst.code.unusedcode.type.CAConstant;
import com.samsungsds.analyst.code.unusedcode.type.CAField;
import com.samsungsds.analyst.code.unusedcode.type.CAMethod;
import com.samsungsds.analyst.code.unusedcode.type.CAType;

public class UnusedCodeAnalysisLauncher implements UnusedCodeAnalysis {

	//private static final String DEFAULT_TARGET_SRC = "src";

	private static final Logger LOGGER = LogManager.getLogger(UnusedCodeAnalysisLauncher.class);

	public static final String UNUSED_CODE_TYPE_FIELD = "Field";
	public static final String UNUSED_CODE_TYPE_METHOD = "Method";
	public static final String UNUSED_CODE_TYPE_CONSTANT = "Constant";
	public static final String UNUSED_CODE_TYPE_CLASS = "Class";

	private String projectBaseDir = null;
	private String targetSrc = null;
	private String targetBinary = null;

	//private String rootPackage = null;
	//private String sourceRootFolderPath = null;
	//private String classRootFolderPath = null;

	@Override
	public void setProjectBaseDir(String directory) {
		this.projectBaseDir = IOAndFileUtils.getNormalizedPath(directory);
	}

	@Override
	public void setTargetSrc(String directory) {
		LOGGER.debug("UnusedCode Target Src : {}", directory);
		//this.targetSrc = directory.replaceAll("/", "\\\\");
		this.targetSrc = IOAndFileUtils.getNormalizedPath(directory);
	}


	@Override
	public void setTargetBinary(String directory) {
		LOGGER.debug("UnusedCode Target Binary : {}", directory);
		//this.targetBinary = directory.replaceAll("/", "\\\\");
		this.targetBinary = IOAndFileUtils.getNormalizedPath(directory);
	}

	@Override
	public void run(String instanceKey) {
		MeasuredResult measuredResult = MeasuredResult.getInstance(instanceKey);

		VisitResult visitResult = new VisitResult();

		Queue<File> waitingQueue = new LinkedList<>();

		for (String projectBinary : getProjectBinary()) {

			waitingQueue.offer(new File(projectBinary));

			while (!waitingQueue.isEmpty()) {
				File f = waitingQueue.poll();

				if (f.isDirectory()) {
					for (File sub : f.listFiles()) {
						waitingQueue.offer(sub);
					}
				} else {
					if (!f.getName().contains(".class")) continue;
					if (f.getName().contains("$")) continue;    // skip additional classes that share a source file

					String path = IOAndFileUtils.getPrefixRemovedPath(f.getPath(), projectBinary);
					String binary = IOAndFileUtils.getPrefixRemovedPath(projectBinary, projectBaseDir);

					path = IOAndFileUtils.getPrefixRemovedPath(path, binary);

					if (measuredResult.haveToSkip(path.replace("\\", "/").replace(".class", ".java"), true)) {
						continue;
					}

					File sourceFileOf = null;
					try {
						ClassReader reader = new ClassReader(new FileInputStream(f));
						reader.accept(new CodeAnalysisClassVisitor(Opcodes.ASM7, visitResult), 0);

						if (!visitResult.skipThisClass()) {
							//if (rootPackage == null) {
							//	initPathInfo(visitResult.getClasses().iterator().next().getName());
							//}

							sourceFileOf = sourceFileOf(f);
							FileInputStream in = new FileInputStream(sourceFileOf);
							//CompilationUnit unit = JavaParser.parse(in);
                            JavaParser javaParser = new JavaParser();
                            ParseResult<CompilationUnit> result = javaParser.parse(in);
                            CompilationUnit unit = result.getResult().get();
							unit.accept(new CodeAnalysisSourceVisitor(visitResult), null);
						}
					} catch (IOException e) {
						LOGGER.info("SKIP FileNotFound: {}", e.getMessage());
						visitResult.getFields().removeAll(visitResult.getTempFields());
						visitResult.getMethods().removeAll(visitResult.getTempMethods());
						visitResult.getContants().removeAll(visitResult.getTempConstants());
					}
				}

				clearTempInfo(visitResult);
			}
		}

		//set total count per type
		measuredResult.setUcTotalClassCount(visitResult.getClasses().size());
		measuredResult.setUcTotalMethodCount(visitResult.getMethods().size());
		measuredResult.setUcTotalFieldCount(visitResult.getFields().size());
		measuredResult.setUcTotalConstantCount(visitResult.getContants().size());

		Collection<CAClass> unusedClasses = CollectionUtils.subtract(visitResult.getClasses(), visitResult.getUsedClasses());
		Collection<CAMethod> unusedMethods = CollectionUtils.subtract(visitResult.getMethods(), visitResult.getUsedMethods());
		Collection<CAField> unusedFields = CollectionUtils.subtract(visitResult.getFields(), visitResult.getUsedFields());
		Collection<CAConstant> unusedConstants = CollectionUtils.subtract(visitResult.getContants(), visitResult.getUsedConstants());

		//set unused count per type
		measuredResult.setUnusedClassCount(unusedClasses.size());
		measuredResult.setUnusedMethodCount(unusedMethods.size());
		measuredResult.setUnusedFieldCount(unusedFields.size());
		measuredResult.setUnusedConstantCount(unusedConstants.size());

		List<UnusedCodeResult> resultList = new ArrayList<>();
		for (CAClass caClass : unusedClasses) {
			UnusedCodeResult result = new UnusedCodeResult();
			if (caClass.getName().contains(".")) {
				result.setPackageName(caClass.getName().substring(0, caClass.getName().lastIndexOf(".")));
			}
			result.setClassName(caClass.getName().substring(caClass.getName().lastIndexOf(".")+1));
			result.setType(UNUSED_CODE_TYPE_CLASS);
			result.setName(result.getClassName());
			result.setDescription(String.format("%s %s has 0 references", result.getType(), result.getName()));
			resultList.add(result);
		}

		for (CAMethod caMethod : unusedMethods) {
			if (isNotExistInSource(caMethod)) {
				measuredResult.setUnusedMethodCount(measuredResult.getUnusedMethodCount() - 1);
				continue;
			}

			UnusedCodeResult result = new UnusedCodeResult();
			if (caMethod.getClassName().contains(".")) {
				result.setPackageName(caMethod.getClassName().substring(0, caMethod.getClassName().lastIndexOf(".")));
			}
			result.setClassName(caMethod.getClassName().substring(caMethod.getClassName().lastIndexOf(".")+1));
			result.setType(UNUSED_CODE_TYPE_METHOD);
			result.setName(caMethod.getName());
			result.setDescription(String.format("%s %s(%s) has 0 references", result.getType(), result.getName(), Arrays.toString(caMethod.getParameterTypes())));
			result.setLine(caMethod.getLine());
			resultList.add(result);
		}

		for (CAField caField : unusedFields) {
			if (isNotExistInSource(caField)) {
				measuredResult.setUnusedFieldCount(measuredResult.getUnusedFieldCount() - 1);
				continue;
			}

			UnusedCodeResult result = new UnusedCodeResult();
			if (caField.getClassName().contains(".")) {
				result.setPackageName(caField.getClassName().substring(0, caField.getClassName().lastIndexOf(".")));
			}
			result.setClassName(caField.getClassName().substring(caField.getClassName().lastIndexOf(".")+1));
			result.setType(UNUSED_CODE_TYPE_FIELD);
			result.setName(caField.getName());
			result.setDescription(String.format("%s %s has 0 references", result.getType(), result.getName()));
			result.setLine(caField.getLine());
			resultList.add(result);
		}

		for (CAConstant caConstant : unusedConstants) {
			if(isNotExistInSource(caConstant)) {
				measuredResult.setUnusedConstantCount(measuredResult.getUnusedConstantCount() - 1);
				continue;
			}

			UnusedCodeResult result = new UnusedCodeResult();
			if (caConstant.getClassName().contains(".")) {
				result.setPackageName(caConstant.getClassName().substring(0, caConstant.getClassName().lastIndexOf(".")));
			}
			result.setClassName(caConstant.getClassName().substring(caConstant.getClassName().lastIndexOf(".")+1));
			result.setType(UNUSED_CODE_TYPE_CONSTANT);
			result.setName(caConstant.getName());
			result.setDescription(String.format("%s %s has 0 references", result.getType(), result.getName()));
			result.setLine(caConstant.getLine());
			resultList.add(result);
		}

		measuredResult.putUnusedCodeList(resultList);
	}

	private String[] getProjectBinary() {
		List<String> list = new ArrayList<>();

		for (String binary : targetBinary.split(FindFileUtils.COMMA_SPLITTER)) {
			list.add(this.projectBaseDir + File.separator + binary);
		}

		return list.toArray(new String[0]);
	}

	private String[] getProjectSrc() {
		List<String> list = new ArrayList<>();

		for (String src : targetSrc.split(FindFileUtils.COMMA_SPLITTER)) {
			list.add(this.projectBaseDir + File.separator + src);
		}

		return list.toArray(new String[0]);
	}

	private boolean isNotExistInSource(CAType caType) {
		return caType.getLine() == 0;
	}

	private void clearTempInfo(VisitResult visitResult) {
		visitResult.getTempFields().clear();
		visitResult.getTempMethods().clear();
		visitResult.getTempConstants().clear();
	}

	/*
	private void initPathInfo(String className) {
		//if (className.contains(".")) {
		//	rootPackage = className.substring(0, className.indexOf("."));
		//	sourceRootFolderPath = targetFolderPath(className, getProjectSrc()).replaceAll("/", "\\\\");
		//	classRootFolderPath = targetFolderPath(className, getProjectBinary()).replaceAll("/", "\\\\");
		//} else {
		//	rootPackage = "default";
		//	sourceRootFolderPath = sourceFolderPathOfDefaultPackage(className, getProjectSrc()).replaceAll("/", "\\\\");
		//	classRootFolderPath = sourceFolderPathOfDefaultPackage(className, getProjectBinary()).replaceAll("/", "\\\\");
		//}
		rootPackage = "default";
		sourceRootFolderPath = sourceFolderPathOfDefaultPackage(className, getProjectSrc()).replaceAll("/", "\\\\");
		classRootFolderPath = sourceFolderPathOfDefaultPackage(className, getProjectBinary()).replaceAll("/", "\\\\");
	}
	*/

	private File sourceFileOf(File classFile) throws IOException {

		String classFilePath = null;

		for (String binary : getProjectBinary()) {
			if (classFile.getPath().startsWith(binary)) {
				if (classFile.getPath().length() == binary.length()) {
					classFilePath = classFile.getPath();
				} else {
					classFilePath = classFile.getPath().substring(binary.length() + 1);
				}
				break;
			}
		}

		if (classFilePath == null) {
			throw new IOException("Class file path not found : " + classFile);
		}

		for (String src : getProjectSrc()) {
			if (Files.exists(Paths.get(src)) && !Files.isDirectory(Paths.get(src))) {
				return new File(src);
			}

			File sourceFile = new File(src + File.separator + classFilePath.replace(".class", ".java"));

			LOGGER.debug("Check source file : {}", sourceFile);

			if (sourceFile.exists()) {
				return sourceFile;
			}
		}

		throw new IOException("Source file not found : " + classFilePath);
	}

	/*
	private String sourceFolderPathOfDefaultPackage(String className, String[] targetDir) {

		for (String target : targetDir) {

			File f = new File(target);
			Queue<File> waitingQueue = new LinkedList<>();
			waitingQueue.offer(f);
			while (!waitingQueue.isEmpty()) {
				f = waitingQueue.poll();
				for (File sub : f.listFiles()) {
					if (sub.isDirectory()) {
						waitingQueue.offer(sub);
					} else {
						String java = sub.getParentFile().getPath() + File.separator + className.replaceAll("\\.", "/") + ".java";
						String clazz =  sub.getParentFile().getPath()+ File.separator + className.replaceAll("\\.", "/") + ".class";

						if (Files.exists(Paths.get(java)) || Files.exists(Paths.get(clazz))) {
							return sub.getParent();
						}
					}
				}
			}
		}

		throw new IllegalArgumentException("the source file path of default package doesn't exist in " + String.join(",", targetDir));
	}
	*/

	/*
	private String targetFolderPath(String className, String[] searchDir) {

		for (String sourceDir : searchDir) {
			if (sourceDir.indexOf(rootPackage, this.projectBaseDir.length()) > -1) {
				return sourceDir.substring(0, sourceDir.indexOf(rootPackage) - 1);
			}

			File f = new File(sourceDir);
			Queue<File> waitingQueue = new LinkedList<>();
			waitingQueue.offer(f);
			while (!waitingQueue.isEmpty()) {
				f = waitingQueue.poll();
				for (File sub : f.listFiles()) {

					String java = sub.getParentFile().getPath() + File.separator + className.replaceAll("\\.", "/") + ".java";
					String clazz =  sub.getParentFile().getPath()+ File.separator + className.replaceAll("\\.", "/") + ".class";

					if (Files.exists(Paths.get(java)) || Files.exists(Paths.get(clazz))) {
						return sub.getParent();
					}

					//if (rootPackage.equals(sub.getName())) {
					//	// source directory가 default 값이면, 테스트 폴더에 대한 검사는 SKIP한다.
					//	if (isNotDefaultTargetSrc() || isTestFolderWhenTargetSrcIsDefaultValue(sub)) {
					//		return sub.getParent();
					//	}
					//}

					if (sub.isDirectory()) {
						waitingQueue.offer(sub);
					}
				}
			}
		}

		throw new IllegalArgumentException("the source file path of given rootPackage was not found");
	}

	private boolean isTestFolderWhenTargetSrcIsDefaultValue(File sub) {
		return targetSrc.equals(DEFAULT_TARGET_SRC) && !sub.getParentFile().getPath().contains("\\test\\");
	}

	private boolean isNotDefaultTargetSrc() {
		return !targetSrc.equals(DEFAULT_TARGET_SRC);
	}
	*/
}
