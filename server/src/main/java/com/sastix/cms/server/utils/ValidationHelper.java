/*
 * Copyright(c) 2017 the original author or authors.
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

package com.sastix.cms.server.utils;

import com.google.common.base.Joiner;
import com.sastix.cms.common.content.exceptions.ContentValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Component
public class ValidationHelper {
    
    private Logger LOG = (Logger) LoggerFactory.getLogger(ValidationHelper.class);
    
    public String createMessage(List<FieldError> errors) {
        String message = null;
        if (errors != null && errors.size() > 0) {
            List<String> messages = new ArrayList<String>();
            for (FieldError error : errors) {
                messages.add(error.getField() + " " + error.getDefaultMessage());
            }
            Joiner joiner = Joiner.on(", ").skipNulls();
            message = joiner.join(messages);
        }
        return message;
    }

    public void validate(BindingResult result) throws ContentValidationException {
        if (result == null) {
            return;
        }
        if (!result.hasErrors()) {
            return;
        }
        String message = createMessage(result.getFieldErrors());
        LOG.trace("Field errors: " + message);
        throw new ContentValidationException(message);
    }
}
