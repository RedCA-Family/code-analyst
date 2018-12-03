package com.samsungsds.analyst.code.ckmetrics;

import com.samsungsds.analyst.code.ckmetrics.library.CK;
import com.samsungsds.analyst.code.ckmetrics.library.CKNumber;
import com.samsungsds.analyst.code.ckmetrics.library.CKReport;
import com.samsungsds.analyst.code.main.MeasuredResult;
import com.samsungsds.analyst.code.util.FindFileUtils;
import com.samsungsds.analyst.code.util.IOAndFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CkMetricsAnalysisLauncher implements CkMetricsAnalysis {
    private static final Logger LOGGER = LogManager.getLogger(CkMetricsAnalysisLauncher.class);

    private String targetSrc = null;

    @Override
    public void setTarget(String directory) {
        this.targetSrc = IOAndFileUtils.getNormalizedPath(directory);
    }

    @Override
    public void run(String instanceKey) {
        for (String src : targetSrc.split(FindFileUtils.COMMA_SPLITTER)) {
            LOGGER.info("CK Metrics Dir. : {}", src);
            CKReport report = new CK().calculate(src, instanceKey);

            for (CKNumber ckNumber : report.all()) {
                MeasuredResult.getInstance(instanceKey).addCkMetricsResultList(new CkMetricsResult(ckNumber));
            }
        }

        LOGGER.info("CK Metrics Total Result : {}", MeasuredResult.getInstance(instanceKey).getCkMetricsResultList().size());
    }
}
