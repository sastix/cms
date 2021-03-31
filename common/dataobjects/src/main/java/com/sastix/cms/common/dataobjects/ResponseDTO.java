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

package com.sastix.cms.common.dataobjects;

import java.io.InputStream;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseDTO {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String RANGE = "Range";

    private InputStream inputStream;
    private byte[] response;
    private Map<String, String> headers;

    private Integer statusCode;

    public ResponseDTO(InputStream inputStream, Map<String, String> headers) {
        this.inputStream = inputStream;
        this.headers = headers;
    }

    public ResponseDTO(InputStream inputStream, Map<String, String> headers, Integer statusCode) {
        this.inputStream = inputStream;
        this.headers = headers;
        this.statusCode = statusCode;
    }

    public ResponseDTO(byte[] response, Map<String, String> headers, Integer statusCode) {
        this.response = response;
        this.headers = headers;
        this.statusCode = statusCode;
    }

}
