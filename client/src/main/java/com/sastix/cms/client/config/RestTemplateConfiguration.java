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

package com.sastix.cms.client.config;

import com.sastix.cms.common.cache.exceptions.CacheValidationException;
import com.sastix.cms.common.cache.exceptions.DataNotFound;
import com.sastix.cms.common.client.CmsRetryPolicy;
import com.sastix.cms.common.client.RetryRestTemplate;
import com.sastix.cms.common.client.exception.CommonExceptionHandler;
import com.sastix.cms.common.client.exception.ExceptionHandler;
import com.sastix.cms.common.content.exceptions.*;
import com.sastix.cms.common.lock.exceptions.LockNotAllowed;
import com.sastix.cms.common.lock.exceptions.LockNotFound;
import com.sastix.cms.common.lock.exceptions.LockNotHeld;
import com.sastix.cms.common.lock.exceptions.LockValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Profile("production")
@Configuration
public class RestTemplateConfiguration {

    @Value("${cms.retry.backOffPeriod:5000}")
    private String backOffPeriod;

    @Value("${cms.retry.maxAttempts:3}")
    private String maxAttempts;

    private static final ConcurrentHashMap<String, ExceptionHandler> SUPPORTED_EXCEPTIONS = new ConcurrentHashMap<>();

    static {
        SUPPORTED_EXCEPTIONS.put(GeneralResourceException.class.getName(), GeneralResourceException::new);
        SUPPORTED_EXCEPTIONS.put(ResourceAccessError.class.getName(), ResourceAccessError::new);
        SUPPORTED_EXCEPTIONS.put(ResourceNotFound.class.getName(), ResourceNotFound::new);
        SUPPORTED_EXCEPTIONS.put(ResourceNotOwned.class.getName(), ResourceNotOwned::new);
        SUPPORTED_EXCEPTIONS.put(ContentValidationException.class.getName(), ContentValidationException::new);
        SUPPORTED_EXCEPTIONS.put(DataNotFound.class.getName(), DataNotFound::new);
        SUPPORTED_EXCEPTIONS.put(CacheValidationException.class.getName(), CacheValidationException::new);
        SUPPORTED_EXCEPTIONS.put(LockValidationException.class.getName(), LockValidationException::new);
        SUPPORTED_EXCEPTIONS.put(LockNotAllowed.class.getName(), LockNotAllowed::new);
        SUPPORTED_EXCEPTIONS.put(LockNotHeld.class.getName(), LockNotHeld::new);
        SUPPORTED_EXCEPTIONS.put(LockNotFound.class.getName(), LockNotFound::new);
    }

    /**
     * Configure and return the retry template.
     */
    public RetryTemplate getRetryTemplate(){
        //Create RetryTemplate
        final RetryTemplate retryTemplate = new RetryTemplate();
        
        //Create Fixed back policy
        final FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        
        //Set backOffPeriod
        fixedBackOffPolicy.setBackOffPeriod(Long.valueOf(backOffPeriod));
        
        //Set the backoff policy
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        //Create Simple Retry Policy
        final CmsRetryPolicy retryPolicy = new CmsRetryPolicy(Integer.valueOf(maxAttempts), Collections
                .<Class<? extends Throwable>, Boolean> singletonMap(RestClientException.class, true), false);

        //Set retry policy
        retryTemplate.setRetryPolicy(retryPolicy);
        
        //Return the RetryTemplate
        return retryTemplate;
    }

    /**
     * Configure and return the Rest Template.
     */
    @Bean(name = "CmsRestTemplate")
    public RetryRestTemplate getRestTemplate() {

        //Creates the restTemplate instance
        final RetryRestTemplate retryRestTemplate = new RetryRestTemplate();

        //Create Custom Exception Handler
        final CommonExceptionHandler exceptionHandler = new CommonExceptionHandler();

        //Set Supported Exceptions
        exceptionHandler.setSupportedExceptions(SUPPORTED_EXCEPTIONS);

        //Set the custom exception handler ar default        
        retryRestTemplate.setErrorHandler(exceptionHandler);

        //Set Retry Template
        retryRestTemplate.setRetryTemplate(getRetryTemplate());

        //Return the template instance
        return retryRestTemplate;
    }

}
