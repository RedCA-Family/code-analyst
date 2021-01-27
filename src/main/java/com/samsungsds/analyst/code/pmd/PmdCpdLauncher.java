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
package com.samsungsds.analyst.code.pmd;

import com.samsungsds.analyst.code.api.Language;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.sonar.DuplicationResult;
import com.samsungsds.analyst.code.util.FileLineFinder;
import net.sourceforge.pmd.cpd.CPDCommandLineInterface;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PmdCpdLauncher implements PmdCpd {
    private static final Logger LOGGER = LogManager.getLogger(PmdCpdLauncher.class);

    private List<String> arg = new ArrayList<>();

    @Override
    public void addOption(String option, String value) {
        arg.add(option);

        if (value != null && !value.equals("")) {
            arg.add(value);
        }
    }

    @Override
    public void run(String instanceKey) {
        File csvFile = createPmdCpdCSVFile();

        LOGGER.debug("Pmd CPD Result File : {}", csvFile.toString());

        System.setProperty(CPDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
        PrintStream originalOut = System.out;
        try (PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(csvFile)), true)) {
            System.setOut(ps);

            CPDCommandLineInterface.main(arg.toArray(new String[0]));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (!System.getProperty(CPDCommandLineInterface.STATUS_CODE_PROPERTY).equals("0")) {
            throw new RuntimeException("PMD CPD has Error with status code " +  System.getProperty(CPDCommandLineInterface.STATUS_CODE_PROPERTY));
        }

        System.setOut(originalOut);

        processResultCSVFile(csvFile, instanceKey);
    }

    private void processResultCSVFile(File csvFile, String instanceKey) {
        try (Reader in = new FileReader(csvFile)) {

            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

            try (FileLineFinder finder = new FileLineFinder()) {
                for (CSVRecord record : records) {
                    if (record.get(0).equals("")) {
                        continue;
                    }

                    int lines = Integer.parseInt(record.get(0));
                    //int tokens = Integer.parseInt(record.get(1));
                    int occurrences = Integer.parseInt(record.get(2));
                    int startLine = Integer.parseInt(record.get(3));
                    String path = record.get(4);

                    // Python의 경우 주석으로 시작하는 부분은 제외
                    if (MeasuredResult.getInstance(instanceKey).getLanguageType() == Language.PYTHON) {
                        String line = finder.getLine(path, startLine);
                        if (line.trim().startsWith("'''") || line.trim().startsWith("\"\"\"")) {
                            continue;
                        }
                    }

                    boolean added = false;
                    boolean hasOccurrences = false;
                    for (int i = 5; i < 5 + (occurrences - 1) * 2; i += 2) {
                        hasOccurrences = true;
                        int targetStartLine = Integer.parseInt(record.get(i));
                        String targetPath = record.get(i + 1);

                        String[] data = new String[6];

                        data[0] = path;
                        data[1] = String.valueOf(startLine);
                        data[2] = String.valueOf(startLine + lines - 1);

                        data[3] = targetPath;
                        data[4] = String.valueOf(targetStartLine);
                        data[5] = String.valueOf(targetStartLine + lines - 1);

                        // MeasuredResult 부분에서 include/exclude 처리 됨 (모두 포함되어야 중복으로 처리)
                        if (addResult(data, instanceKey)) {
                            added = true;
                        }
                    }

                    if (added && hasOccurrences) {
                        MeasuredResult.getInstance(instanceKey).addDuplicatedBlocks();
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private boolean addResult(String[] data, String instanceKey) {
        DuplicationResult result;

        data[0] = PmdResult.getConvertedFilePath(data[0], instanceKey).replaceAll("^\\./", "");
        data[3] = PmdResult.getConvertedFilePath(data[3], instanceKey).replaceAll("^\\./", "");

        if (data[3].equals(data[0])) {
            data[3] = DuplicationResult.DUPLICATED_FILE_SAME_MARK;
        }

        if (data.length == 6) {
            result = new DuplicationResult(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                    data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5]));
        } else if (data.length == 5) {
            result = new DuplicationResult(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                    DuplicationResult.DUPLICATED_FILE_SAME_MARK, Integer.parseInt(data[3]), Integer.parseInt(data[4]));
        } else {
            throw new RuntimeException("Duplication Process Error : " + Arrays.toString(data));
        }

        return MeasuredResult.getInstance(instanceKey).addDuplicationResult(result);
    }

    private File createPmdCpdCSVFile() {
        File csvFile;

        try {
            csvFile = File.createTempFile("pmd-cpd", ".csv");
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        csvFile.deleteOnExit();

        return csvFile;
    }
}
