package com.samsungsds.analyst.code.roslyn.codemetrics.result;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class MetricsResult implements Serializable {
    private static final long serialVersionUID = 3314978555441763883L;

    private final String projectName;
    private final String namespaceName;
    private final String className;
    private final String type;
    private final String name;
    private final String filePath;
    private final int line;

    private int maintainabilityIndex = 0;
    private int cyclomaticComplexity = 0;
    private int classCoupling = 0;
    private int linesOfCode = 0;

    public MetricsResult(String projectName, String namespaceName, String className, String type, String name, String filePath, int line) {
        this.projectName = projectName;
        this.namespaceName = namespaceName;
        this.className = className;
        this.type = type;
        this.name = name;
        this.filePath = filePath;
        this.line = line;
    }

    public void setMetricsValues(int maintainabilityIndex, int cyclomaticComplexity, int classCoupling, int linesOfCode) {
        this.maintainabilityIndex = maintainabilityIndex;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.classCoupling = classCoupling;
        this.linesOfCode = linesOfCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getNamespaceName() {
        return namespaceName;
    }

    public String getClassName() {
        return className;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getLine() {
        return line;
    }

    public int getMaintainabilityIndex() {
        return maintainabilityIndex;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public int getClassCoupling() {
        return classCoupling;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MetricsResult{");
        sb.append("projectName='").append(projectName).append('\'');
        sb.append(", namespaceName='").append(namespaceName).append('\'');
        sb.append(", className='").append(className).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", filePath='").append(filePath).append('\'');
        sb.append(", line=").append(line);
        sb.append(", maintainabilityIndex=").append(maintainabilityIndex);
        sb.append(", cyclomaticComplexity=").append(cyclomaticComplexity);
        sb.append(", classCoupling=").append(classCoupling);
        sb.append(", linesOfCode=").append(linesOfCode);
        sb.append('}');
        return sb.toString();
    }
}
