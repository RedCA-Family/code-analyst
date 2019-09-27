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
package com.samsungsds.analyst.code.api;

import com.samsungsds.analyst.code.api.impl.CSharpCodeAnalystImpl;
import com.samsungsds.analyst.code.api.impl.JavaCodeAnalystImpl;
import com.samsungsds.analyst.code.api.impl.JavaScriptCodeAnalystImpl;
import com.samsungsds.analyst.code.api.impl.PythonCodeAnalystImpl;

public class CodeAnalystFactory {
	public static CodeAnalyst create() {
		return new JavaCodeAnalystImpl();
	}

	public static CodeAnalyst create(Language language) {
		if (language == Language.JAVA) {
			return new JavaCodeAnalystImpl();
		} else if (language == Language.JAVASCRIPT) {
			return new JavaScriptCodeAnalystImpl();
		} else if (language == Language.CSHARP) {
		    return new CSharpCodeAnalystImpl();
        } else {    // Python
		    return new PythonCodeAnalystImpl();
        }
	}
}
