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

package com.sastix.cms.common.services.web;

import com.sastix.cms.common.content.exceptions.ResourceNotFound;
import com.sastix.cms.common.exception.CommonException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler({CommonException.class})
    public void handleBadRequests(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        log.error("Bad request: {} from {}, Exception: {} {}",
                request.getRequestURI(),
                request.getRemoteHost(),
                e.getStackTrace()[0].toString(),
                e.getLocalizedMessage());

        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(ResourceNotFound.class)
    public void handleNoTFound() {
        // Nothing to do
    }
}
