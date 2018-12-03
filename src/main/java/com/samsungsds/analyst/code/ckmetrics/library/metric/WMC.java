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
package com.samsungsds.analyst.code.ckmetrics.library.metric;

import com.samsungsds.analyst.code.ckmetrics.library.CKNumber;
import com.samsungsds.analyst.code.ckmetrics.library.CKReport;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.*;

public class WMC extends ASTVisitor implements Metric {

	protected int cc = 0;
	
    public boolean visit(MethodDeclaration node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(ForStatement node) {
    	increaseCc();
    	
    	return super.visit(node);
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(ConditionalExpression node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(DoStatement node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(WhileStatement node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(SwitchCase node) {
    	if(!node.isDefault())
    		increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(Initializer node) {
    	increaseCc();
    	return super.visit(node);
    }


    @Override
    public boolean visit(CatchClause node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    public boolean visit(IfStatement node) {
		String expr = node.getExpression().toString().replace("&&", "&").replace("||", "|");
		int ands = StringUtils.countMatches(expr, "&");
		int ors = StringUtils.countMatches(expr, "|");
		
		increaseCc(ands + ors);
    	increaseCc();
    	
    	return super.visit(node);
    }
    
    private void increaseCc() {
    	increaseCc(1);
    }

    protected void increaseCc(int qtd) {
    	cc += qtd;
    }

	@Override
	public void execute(CompilationUnit cu, CKNumber number, CKReport report) {
		cu.accept(this);
	}

	@Override
	public void setResult(CKNumber result) {
		result.setWmc(cc);
	}

}
