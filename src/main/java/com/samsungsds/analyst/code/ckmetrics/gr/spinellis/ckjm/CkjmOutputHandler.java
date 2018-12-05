/*
 * (C) Copyright 2005 Diomidis Spinellis, Julien Rentrop
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.samsungsds.analyst.code.ckmetrics.gr.spinellis.ckjm;

/**
 * Interface of output handlers
 * Use this interface to couple your tool to CKJM. Example implenations
 * which could use this tool are ant task writing, IDE integration,
 * GUI based interfaces etc.
 *
 * @author Julien Rentrop
 */
public interface CkjmOutputHandler {
    /**
     * Method called when metrics are generated
     * @param name Name of the class
     * @param c Value object that contains the corresponding metrics
     */
    void handleClass(String name, ClassMetrics c);
}
