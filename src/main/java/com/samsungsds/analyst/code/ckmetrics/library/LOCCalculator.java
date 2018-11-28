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
package com.samsungsds.analyst.code.ckmetrics.library;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LOCCalculator {

	private static Logger log = LogManager.getLogger(LOCCalculator.class);
	
	public int calculate(InputStream sourceCode) {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(sourceCode));
			int lines = 0;
			
			String line = null;
			do {
				line = reader.readLine();
				if (line != null && !empty(line)) lines++;
			}
			while (line != null);
			reader.close();

			return lines;
		} catch (IOException e) {
			log.error(e);
			return 0;
		}
	}

	private boolean empty(String line) {
		String result = line.replace("\t", "").replace(" ", "").trim();

		return result.isEmpty();
	}

}
