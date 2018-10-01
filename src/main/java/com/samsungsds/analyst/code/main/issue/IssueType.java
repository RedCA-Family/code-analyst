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
package com.samsungsds.analyst.code.main.issue;

import com.google.gson.annotations.SerializedName;

public enum IssueType {
    @SerializedName("Code Smell")
    CODE_SMELL("Code Smell"),
    @SerializedName("Bug")
    BUG("Bug"),
    @SerializedName("Vulnerability")
    VULNERABILITY("Vulnerability"),
    @SerializedName("N/A")
    NA("N/A");

    private final String typeName;

    IssueType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getTypeIndex() {
        if (this == IssueType.BUG) {
            return 1;
        } else if (this == IssueType.VULNERABILITY) {
            return 2;
        } else if (this == IssueType.CODE_SMELL) {
            return 3;
        } else {
            return 0; // N/A
        }
    }

    public static IssueType getIssueTypeOf(String typeName) {
        for (IssueType type : values()) {
            if (type.getTypeName().equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported issue type string : " + typeName);
    }

    @Override
    public String toString() {
        return getTypeName();
    }
}
