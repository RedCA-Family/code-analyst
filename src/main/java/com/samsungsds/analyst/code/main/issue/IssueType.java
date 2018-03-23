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
