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

package com.sastix.cms.common.client;

import com.sastix.cms.common.exception.BusinessException;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.Collections;
import java.util.Map;

public class CmsRetryPolicy extends SimpleRetryPolicy {

    private static final long serialVersionUID = 1L;

    private BinaryExceptionClassifier businessExceptionClassifier = new BinaryExceptionClassifier(false);

    public CmsRetryPolicy() {
        this(DEFAULT_MAX_ATTEMPTS, Collections
                .<Class<? extends Throwable>, Boolean>singletonMap(Exception.class, true));
    }

    public CmsRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
        this(maxAttempts, retryableExceptions, false);
    }

    public CmsRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions, boolean traverseCauses) {
        super(maxAttempts, retryableExceptions, traverseCauses);
        this.businessExceptionClassifier = new BinaryExceptionClassifier(Collections
                .<Class<? extends Throwable>, Boolean>singletonMap(BusinessException.class, true));
        this.businessExceptionClassifier.setTraverseCauses(false);
    }

    public CmsRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions, Map<Class<? extends Throwable>, Boolean> businessExceptions) {
        super(maxAttempts, retryableExceptions);
        this.businessExceptionClassifier = new BinaryExceptionClassifier(businessExceptions, true);
        this.businessExceptionClassifier.setTraverseCauses(false);
    }


    @Override
    public boolean canRetry(RetryContext context) {
        final Throwable t = context.getLastThrowable();
        return (t == null) || !(businessExceptionClassifier.classify(t)) && super.canRetry(context);
    }
}
