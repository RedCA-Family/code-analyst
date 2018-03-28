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
