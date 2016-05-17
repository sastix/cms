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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HtmlToPdfConfig {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlToPdfConfig.class);

    private String wkhtmltopdfCommand;

    public HtmlToPdfConfig(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }

    public HtmlToPdfConfig() {

    }

    public String getWkhtmltopdfCommand() {
        return wkhtmltopdfCommand;
    }

    public void setWkhtmltopdfCommand(String wkhtmltopdfCommand) {
        this.wkhtmltopdfCommand = wkhtmltopdfCommand;
    }

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
            LOG.error("InterruptedException while trying to find wkhtmltopdf executable",e);
        } catch (IOException e) {
            LOG.error("IOException while trying to find wkhtmltopdf executable", e);
        } catch (RuntimeException e) {
            LOG.error("RuntimeException while trying to find wkhtmltopdf executable", e);
        }
        return ret;
    }
}
