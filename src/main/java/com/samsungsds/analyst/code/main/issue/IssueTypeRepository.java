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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IssueTypeRepository {
    private static Properties prop = new Properties();

    static {

        try (InputStream input = IssueTypeRepository.class.getClassLoader().getResourceAsStream("rule-type-info.properties")) {
            prop.load(input);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static IssueType getIssueType(String repository, String key) {
        String property = prop.getProperty(repository + ":" + key, "N/A");

        return IssueType.getIssueTypeOf(property.trim());
    }
}
