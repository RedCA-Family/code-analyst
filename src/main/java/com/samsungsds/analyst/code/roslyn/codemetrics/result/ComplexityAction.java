package com.samsungsds.analyst.code.roslyn.codemetrics.result;

import com.samsungsds.analyst.code.pmd.ComplexityResult;

import java.util.ArrayList;
import java.util.List;

public class ComplexityAction implements AdditionalAction {
    private List<ComplexityResult> list = new ArrayList<>();

    @Override
    public void doAction(MetricsResult result) {
        if (result.getType().equals("method")) {
            list.add(new ComplexityResult(result.getFilePath(), result.getLine(), result.getName(), result.getCyclomaticComplexity()));
        }
    }

    public List<ComplexityResult> getList() {
        return list;
    }
}
