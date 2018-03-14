package com.samsungsds.analyst.code.test;

import java.util.Date;

public class SquidS3010 {
    static Date dateOfBirth;
    static int expectedFingers;

    public SquidS3010(Date birthday) {
        dateOfBirth = birthday; // Noncompliant; now everyone has this birthday
        expectedFingers = 10;   // Noncompliant
    }
}