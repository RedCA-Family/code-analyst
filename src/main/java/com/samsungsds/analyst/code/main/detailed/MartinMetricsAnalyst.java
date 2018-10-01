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
package com.samsungsds.analyst.code.main.detailed;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.samsungsds.analyst.code.jdepend.framework.JavaPackage;

public class MartinMetricsAnalyst {
	private static final Logger LOGGER = LogManager.getLogger(MartinMetricsAnalyst.class);
	
	private final static float THRESHOLD = 0.8f;
	
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
