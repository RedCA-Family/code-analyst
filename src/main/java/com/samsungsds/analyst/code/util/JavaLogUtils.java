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
package com.samsungsds.analyst.code.util;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JavaLogUtils {
    public static void unsetDebugLevel() {
        Logger.getGlobal().setLevel(Level.INFO);
    }

    public static void setDebugLevel() {
        Logger.getGlobal().setLevel(Level.FINE);
    }

    public static Level getCurrentGlobalLevel() {
        return Logger.getGlobal().getLevel();
    }

    public static void setGlobalLevel(Level level) {
        Logger.getGlobal().setLevel(level);
    }

    public static void setPmdLogLevelFilter(Level level) {
        String[] loggerNames = {
            "net.sourceforge.pmd.RuleSetFactory",
            "net.sourceforge.pmd.PMD"
        };

        PmdFilter filter = new PmdFilter(level, loggerNames);

        //Logger.getGlobal().setFilter(filter);

        for (String loggerName : loggerNames) {
            Logger.getLogger(loggerName).setFilter(filter);
        }
    }
}

class PmdFilter implements Filter {
    private Level level;
    private String[] filteredLoggerNames = new String[0];

    public PmdFilter(Level level, String[] filteredLoggerNames) {
        this.level = level;
        this.filteredLoggerNames = filteredLoggerNames;
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        for (String loggerName : filteredLoggerNames) {
            if (record.getLoggerName().equals(loggerName)) {
                if (record.getLevel().intValue() <= this.level.intValue()) {
                    return false;
                }
            }
        }

        return true;
    }
}
