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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CKReport {

	private Map<String, CKNumber> results;

	public CKReport() {
		this.results = new HashMap<String, CKNumber>();
	}

	public void add(CKNumber ck) {
		results.put(ck.getFile(), ck);
	}

	public CKNumber get(String name) {
		return results.get(name);
	}

	public Collection<CKNumber> all() {
		return results.values();
	}

	public CKNumber getByClassName(String name) {
		for (CKNumber ck : all()) {
			if (ck.getClassName().equals(name))
				return ck;
		}

		return null;
	}
}
