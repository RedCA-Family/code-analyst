package com.samsungsds.analyst.code.jdepend;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.samsungsds.analyst.code.util.FindFileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.jdepend.framework.JDepend;
import com.samsungsds.analyst.code.jdepend.framework.JavaPackage;
import com.samsungsds.analyst.code.jdepend.framework.PackageComparator;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.main.detailed.MartinMetricsAnalyst;

public class JDependAnalysisLauncher implements JDependAnalysis {
	private static final Logger LOGGER = LogManager.getLogger(JDependAnalysisLauncher.class);
	
	private final JDepend analyzer = new JDepend();
	private final NumberFormat formatter = NumberFormat.getInstance();
	private final List<String> includePackageList = new ArrayList<>();
	
	private final MartinMetricsAnalyst martinMetrics = new MartinMetricsAnalyst();
	
	private String directory = null;
	
	public JDependAnalysisLauncher() {
		formatter.setMaximumFractionDigits(2);
	}
	
	@Override
	public void addIncludePackage(String packageName) {
		includePackageList.add(packageName);
	}
	
	@Override
	public void setTarget(String directory) {
		this.directory = directory;
	}

	@Override
	public void run(String instanceKey) {
		try {
			for (String dir : directory.split(FindFileUtils.COMMA_SPLITTER)) {
				analyzer.addDirectory(dir);
			}
		} catch (IOException ioe) {
			throw new IllegalArgumentException(ioe);
		}
		
		Collection<JavaPackage> packages = analyzer.analyze();
		
		ArrayList<JavaPackage> packageList = new ArrayList<>(packages);

        Collections.sort(packageList, new PackageComparator(PackageComparator.byName()));
		
		ResultIncludeFilter filter = getResultIncludeFilter();
		
		checkResult(packageList, filter, instanceKey);
	}

	public ResultIncludeFilter getResultIncludeFilter() {
		ResultIncludeFilter filter = new ResultIncludeFilter();
		
		for (String packageName : includePackageList) {
			filter.addIncludePackage(packageName);
		}
		
		return filter;
	}

	public void checkResult(ArrayList<JavaPackage> packageList, ResultIncludeFilter filter, String instanceKey) {
		List<JavaPackage> list = new ArrayList<>();
		List<List<JavaPackage>> cycleList = new ArrayList<>();
		
		LOGGER.info("Cyclic Dependencies:");
		for (JavaPackage jPackage : packageList) {
			if (!filter.include(jPackage.getName())) {
				continue;
			}
			
			if (MeasuredResult.getInstance(instanceKey).isDetailAnalysis()) {
				martinMetrics.addPackageInfo(jPackage);
			}
						
	        jPackage.collectCycle(list);

	        if (!jPackage.containsCycle()) {
	            continue;
	        }

	        JavaPackage cyclePackage = (JavaPackage) list.get(list.size() - 1);
			
			int i = 0;
			
			boolean isInCycle = false;
			List<JavaPackage> currentCycle = new LinkedList<>();
			
	        for (JavaPackage pkg : list) {
	            i++;
	            
	            if (pkg == cyclePackage) {
	            	isInCycle = true;
	            }
	            
	            if (isInCycle == true && i < list.size()) {
	            	currentCycle.add(pkg);
	            }
	        }
	        
	     
	        JavaPackage headPackage = Collections.min(currentCycle, Comparator.comparing(JavaPackage::getName));
	        
	        while (currentCycle.get(0) != headPackage) {
	        	Collections.rotate(currentCycle, 1);
	        }

	        boolean isDuplicatedCycle = false;
	      
	        for (List<JavaPackage> cycle : cycleList) {
	        	if(CollectionUtils.isEqualCollection(currentCycle, cycle)) {
	        		isDuplicatedCycle = true;
	        		break;
		        }
	        }
	        
	        if (isDuplicatedCycle == false) {
	        	cycleList.add(currentCycle);
	        }
	        
	        list.clear();
        }
		
	    if (cycleList.size() > 0) {
	    	
	        for (List<JavaPackage> cycle : cycleList) {
	        	
	        	StringBuilder print = new StringBuilder();
	        	boolean isSkipedCycle = false;
	        	int i = 0;
	        	
	        	for (JavaPackage pkg : cycle) {
	        		if (i++ == 0) {
	        			print.append(pkg.getName());
		        		LOGGER.info("{}", pkg.getName());
		                LOGGER.info("{}|", tab());
	        		} else {
	        			print.append(" | " + pkg.getName());
	                	if (filter.include(pkg.getName())) {
	                		LOGGER.info("{}|   {}", tab(), pkg.getName());
	                	} else {
	                		LOGGER.info("{}|   {} (skip)", tab(), pkg.getName());
	                		isSkipedCycle = true;
	                	}
	        		}
	        	}
	        	
	        	print.append(" |-> " + cycle.get(0).getName());
            	if (filter.include(cycle.get(0).getName())) {
            		LOGGER.info("{}|-> {}", tab(), cycle.get(0).getName());
            	} else {
            		LOGGER.info("{}|-> {} (skip)", tab(), cycle.get(0).getName());
            		isSkipedCycle = true;
            	}
	        	 
            	if (!isSkipedCycle) {
            		MeasuredResult.getInstance(instanceKey).addAcyclicDependency(print.toString());
            	}
	        }
	    }
	     
		if (MeasuredResult.getInstance(instanceKey).isDetailAnalysis()) {
			MeasuredResult.getInstance(instanceKey).setTopMartinMetrics(martinMetrics.getMartinMetricsList());
		}
	}
	
	protected String tab() {
		return "    ";
	}
}
