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
package com.samsungsds.analyst.code.python.radon.result;

import java.util.List;

public class RadonComplexityItem {
    private String type;
    private String rank;
    private int lineno;
    private String name;
    private int complexity;
    private List<RadonComplexityItem> methods;

    public RadonComplexityItem(String type, String rank, int lineno, String name, int complexity) {
        this.type = type;
        this.rank = rank;
        this.lineno = lineno;
        this.name = name;
        this.complexity = complexity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getLineno() {
        return lineno;
    }

    public void setLineno(int lineno) {
        this.lineno = lineno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public List<RadonComplexityItem> getMethods() {
        return methods;
    }

    public void setMethods(List<RadonComplexityItem> methods) {
        this.methods = methods;
    }
}
