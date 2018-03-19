package com.samsungsds.analyst.code.unusedcode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
	
	private static final Logger LOGGER = LogManager.getLogger(UnusedCodeAnalysisLauncher.class);
	
	public static final String UNUSED_CODE_TYPE_FIELD = "Field";
	public static final String UNUSED_CODE_TYPE_METHOD = "Method";
	public static final String UNUSED_CODE_TYPE_CONSTANT = "Constant";
	public static final String UNUSED_CODE_TYPE_CLASS = "Class";
	
	private String targetSrc = null;
	private String targetBinary = null;
	private String excludePath = null;
	private String includePath = null;
	
	private String rootPackage = null;
	private String sourceRootFolderPath = null;
	private String classRootFolderPath = null;
	
	@Override
	public void setTargetSrc(String directory) {
		LOGGER.debug("UnusedCode Target Src : {}", directory);
		this.targetSrc = directory; 
	}


	@Override
	public void setTargetBinary(String directory) {
		LOGGER.debug("UnusedCode Target Binary : {}", directory);
		this.targetBinary = directory;
	}
	
	@Override
	public void setExclude(String path) {
		LOGGER.debug("UnusedCode Exclude Path : {}", path);
		this.excludePath = path;
	}
	
	@Override
	public void setInclude(String path) {
		LOGGER.debug("UnusedCode Include Path : {}", path);
		this.includePath = path;
	}

	@Override
	public void run(String instanceKey) {
		MeasuredResult measuredResult = MeasuredResult.getInstance(instanceKey);
		
		if(this.excludePath != null && !"".equals(this.excludePath)) {
			measuredResult.setExcludeFilters(this.excludePath);
		}
		
		if(this.includePath != null && !"".equals(this.includePath)) {
			measuredResult.setIncludeFilters(this.includePath);
		}
		
		VisitResult visitResult = new VisitResult();
		
		Queue<File> waitingQueue = new LinkedList<>();
		waitingQueue.offer(new File(this.targetBinary));
		while(!waitingQueue.isEmpty()) {
			File f = waitingQueue.poll();
			if(f.isDirectory()) {
				for (File sub : f.listFiles()) {
					waitingQueue.offer(sub);
				}
			} else {
				if(f.getName().indexOf(".class") == -1) continue;
				if(f.getName().indexOf("$") > -1) continue;//skip additional classes that share a source file
				if(measuredResult.haveToSkip(f.getPath().replaceAll("\\\\", "/").replace(".class", ".java"))) continue;
				
				try {
//					LOGGER.info("Scan file... "+f.getPath());
					ClassReader reader = new ClassReader(new FileInputStream(f));
					reader.accept(new CodeAnalysisClassVisitor(Opcodes.ASM5, visitResult), 0);
					
					if(!visitResult.skipThisClass()) {
						if(rootPackage == null) {
							initPathInfo(visitResult.getClasses().iterator().next().getName());
						}
						
						FileInputStream in = new FileInputStream(sourceFileOf(f));
						CompilationUnit unit = JavaParser.parse(in);
						unit.accept(new CodeAnalysisSourceVisitor(visitResult), null);
					}
				} catch (IOException e) {
					LOGGER.info("SKIP FileNotFound: "+sourceFileOf(f).getPath());
					visitResult.getFields().removeAll(visitResult.getTempFields());
					visitResult.getMethods().removeAll(visitResult.getTempMethods());
					visitResult.getContants().removeAll(visitResult.getTempConstants());
				}
			}

			clearTempInfo(visitResult);
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
		for(CAClass caClass : unusedClasses) {
			UnusedCodeResult result = new UnusedCodeResult();
			if(caClass.getName().indexOf(".") > -1) {
				result.setPackageName(caClass.getName().substring(0, caClass.getName().lastIndexOf(".")));
			}
			result.setClassName(caClass.getName().substring(caClass.getName().lastIndexOf(".")+1));
			result.setType(UNUSED_CODE_TYPE_CLASS);
			result.setName(result.getClassName());
			result.setDescription(String.format("%s %s has 0 references", result.getType(), result.getName()));
			resultList.add(result);
		}
		
		for(CAMethod caMethod : unusedMethods) {
			if(isNotExistInSource(caMethod)) {
				measuredResult.setUnusedMethodCount(measuredResult.getUnusedMethodCount() - 1);
				continue;
			}
			
			UnusedCodeResult result = new UnusedCodeResult();
			if(caMethod.getClassName().indexOf(".") > -1) {
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
			if(isNotExistInSource(caField)) {
				measuredResult.setUnusedFieldCount(measuredResult.getUnusedFieldCount() - 1);
				continue;
			}
			
			UnusedCodeResult result = new UnusedCodeResult();
			if(caField.getClassName().indexOf(".") > -1) {
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
			if(caConstant.getClassName().indexOf(".") > -1) {
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
	
	private boolean isNotExistInSource(CAType caType) {
		return caType.getLine() == 0;
	}
	
	private void clearTempInfo(VisitResult visitResult) {
		visitResult.getTempFields().clear();
		visitResult.getTempMethods().clear();
		visitResult.getTempConstants().clear();
	}
	
	private void initPathInfo(String className) {
		boolean isDefaultPackage = className.indexOf(".") == -1;
		if(isDefaultPackage) {
			rootPackage = "default";
			sourceRootFolderPath = sourceFolderPathOfDefaultPackage(this.targetSrc).replaceAll("/", "\\\\");;
			classRootFolderPath = sourceFolderPathOfDefaultPackage(this.targetBinary).replaceAll("/", "\\\\");;
		} else {
			rootPackage = className.substring(0, className.indexOf("."));
			sourceRootFolderPath = targetFolerPath(this.targetSrc, rootPackage).replaceAll("/", "\\\\");
			classRootFolderPath = targetFolerPath(this.targetBinary, rootPackage).replaceAll("/", "\\\\");;
		}
	}
	
	private File sourceFileOf(File classFile) {
		String sourceFilePath = classFile.getPath().replace(classRootFolderPath, sourceRootFolderPath).replace(".class", ".java");
		return new File(sourceFilePath);
	}
	
	private String sourceFolderPathOfDefaultPackage(String targetDir) {
		String sourceDir = targetDir;
		
		File f = new File(sourceDir);
		Queue<File> waitingQueue = new LinkedList<>();
		waitingQueue.offer(f);
		while(!waitingQueue.isEmpty()) {
			f = waitingQueue.poll();
			for (File sub : f.listFiles()) {
				if(sub.isDirectory()) {
					waitingQueue.offer(sub);
				} else {
					if(sub.getPath().indexOf(".java") > -1 || sub.getPath().indexOf(".class") > -1) {
						return sub.getParent();
					}
				}
			}
		}
		
		throw new IllegalArgumentException("the source file path of default package doesn't exist in "+sourceDir);
	}
	
	private String targetFolerPath(String searchDir, String targetPackage) {
		String sourceDir = searchDir;
		if(sourceDir.indexOf(rootPackage) > -1) {
			return sourceDir.substring(0, sourceDir.indexOf(rootPackage)-1);
		}
		
		File f = new File(sourceDir);
		Queue<File> waitingQueue = new LinkedList<>();
		waitingQueue.offer(f);
		while(!waitingQueue.isEmpty()) {
			f = waitingQueue.poll();
			for (File sub : f.listFiles()) {
				if(rootPackage.equals(sub.getName())) {
					if(sub.getParentFile().getPath().indexOf("test") < 0) {//package 경로 전, 파일경로에 test 폴더가 포함되어 있으면 skip한다.
						return sub.getParent();
					} 
				}
				
				if(sub.isDirectory()) {
					waitingQueue.offer(sub);
				}
			}
		}
		
		throw new IllegalArgumentException("the source file path of given rootPackage was not found");
	}
}
