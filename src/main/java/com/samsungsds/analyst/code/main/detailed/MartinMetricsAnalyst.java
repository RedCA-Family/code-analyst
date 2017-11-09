package com.samsungsds.analyst.code.main.detailed;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.jdepend.framework.JavaPackage;

public class MartinMetricsAnalyst {
	private static final Logger LOGGER = LogManager.getLogger(MartinMetricsAnalyst.class);
	
	private final static float THRESHOLD = 0.25f;
	
	private List<MartinMetrics> list = new ArrayList<>();

	public void addPackageInfo(JavaPackage jPackage) {
		if (jPackage.distance() > THRESHOLD) {
			LOGGER.debug("{} : Ca = {}, Ce = {}, A = {}, I = {}, D = {}", 
					jPackage.getName(), jPackage.afferentCoupling(), jPackage.efferentCoupling(), 
					jPackage.abstractness(), jPackage.instability(), jPackage.distance());
			
			list.add(new MartinMetrics(jPackage.getName(), jPackage.afferentCoupling(), jPackage.efferentCoupling(), 
					jPackage.abstractness(), jPackage.instability(), jPackage.distance()));
		}
	}
	
	public List<MartinMetrics> getMartinMetricsList() {
		return list;
	}

}
