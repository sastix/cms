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

package com.sastix.cms.common.dataobjects.htmltopdf;

import java.util.ArrayList;
import java.util.List;

public class Params {
    private List<Param> params;

    public Params() {
        this.params = new ArrayList<>();
    }

    public void add(Param param) {
        params.add(param);
    }

    public void add(Param... params) {
        for (Param param : params) {
            add(param);
        }
    }

    public List<String> getParamsAsStringList() {
        List<String> commandLine = new ArrayList<String>();

        for (Param p : params) {
            commandLine.add(p.getKey());

            String value = p.getValue();

            if (value != null) {
                commandLine.add(p.getValue());
            }
        }

        return commandLine;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Param param : params) {
            sb.append(param);
        }
        return sb.toString();
    }
}
