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

public enum ProgressEvent {
	PREPARE_COMPLETE, SONAR_START_COMPLETE,
	CODE_SIZE_COMPLETE, DUPLICATION_COMPLETE, COMPLEXITY_COMPLETE,
	SONARJAVA_COMPLETE, JAVASCRIPT_COMPLETE, CSS_COMPLETE, HTML_COMPLETE,
    SONARCSHARP_COMPLETE, SONARPYTHON_COMPLETE, SONAR_ALL_COMPLETE,
	PMD_COMPLETE, FINDBUGS_COMPLETE, FINDSECBUGS_COMPLETE,
	DEPENDENCY_COMPLETE, UNUSED_COMPLETE, CK_METRICS_COMPLETE, CHECKSTYLE_COMPLETE,
	FINAL_COMPLETE
}
