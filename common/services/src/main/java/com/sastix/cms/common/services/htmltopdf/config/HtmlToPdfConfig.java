/*
 * Copyright(c) 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sastix.cms.common.services.htmltopdf.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class HtmlToPdfConfig {

    private String wkhtmltopdfCommand;

    /**
     * Attempts to find the `wkhtmltopdf` executable in the system path.
     *
     * @return
     */
    public String findExecutable() {
        String ret = null;
        try {
            String osname = System.getProperty("os.name").toLowerCase();

            String cmd;
            if (osname.contains("windows"))
                cmd = "where wkhtmltopdf";
            else cmd = "which wkhtmltopdf";

            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (sb.toString().isEmpty())
                throw new RuntimeException();

            ret = sb.toString();
        } catch (InterruptedException e) {
            log.error("InterruptedException while trying to find wkhtmltopdf executable",e);
        } catch (IOException e) {
            log.error("IOException while trying to find wkhtmltopdf executable", e);
        } catch (RuntimeException e) {
            log.error("RuntimeException while trying to find wkhtmltopdf executable", e);
        }
        return ret;
    }
}
