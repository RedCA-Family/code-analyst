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

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Runner {

	public static void main(String[] args) throws FileNotFoundException {

		if (args == null || args.length < 2) {
			System.out.println("Usage java -jar ck.jar <path to project> <path to csv>");
			return;
		}
		
		String path = args[0];
		String csvPath = args[1];
		
		CKReport report = new CK().calculate(path, "");
		
		PrintStream ps = new PrintStream(csvPath);
		ps.println("file,class,type,cbo,wmc,dit,noc,rfc,lcom,nom,nopm,nosm,nof,nopf,nosf,nosi,loc");
		for (CKNumber result : report.all()) {
			if (result.isError()) continue;
			
			ps.println(
				result.getFile() + "," +
				result.getClassName() + "," +
				result.getType() + "," +
				result.getCbo() + "," +
				result.getWmc() + "," +
				result.getDit() + "," +
				result.getNoc() + "," +
				result.getRfc() + "," +
				result.getLcom() + "," +
				result.getNom() + "," +
				result.getNopm() + "," + 
				result.getNosm() + "," +
				result.getNof() + "," +
				result.getNopf() + "," + 
				result.getNosf() + "," +
				result.getNosi() + "," +
				result.getLoc()
			);
		}

		ps.close();
	}
}
