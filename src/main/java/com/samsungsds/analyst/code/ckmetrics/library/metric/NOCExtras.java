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

import java.util.HashMap;
import java.util.Map;

public class NOCExtras {

    private Map<String, Integer> toAdd;

    public NOCExtras() {
        toAdd = new HashMap<>();
    }

    public void plusOne(String clazz) {
        if (clazz.equals("java.lang.Object"))
            return;

        if (!toAdd.containsKey(clazz))
            toAdd.put(clazz, 0);

        toAdd.put(clazz, toAdd.get(clazz) + 1);
    }

    public void update(CKReport report) {
        for(Map.Entry<String, Integer> kv : toAdd.entrySet()) {
            CKNumber ck = report.getByClassName(kv.getKey());
            if(ck!=null) ck.incNoc(kv.getValue());
        }
    }
}
