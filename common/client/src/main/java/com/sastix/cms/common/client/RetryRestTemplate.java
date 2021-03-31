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

import org.springframework.http.HttpMethod;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Map;

/**
 * Extension for RestTemplate to support retries transparently.
 */
@Slf4j
public class RetryRestTemplate extends RestTemplate {

    private RetryTemplate retryTemplate;

    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Map<String, ?> urlVariables) throws RestClientException {
        log.trace("Injecting execute(String, HttpMethod, RequestCallback, ResponseExtractor, Map) method. Applying retry template.");
        final long start = System.currentTimeMillis();
        T t = retryTemplate.execute(retryContext -> super.execute(url, method, requestCallback, responseExtractor, urlVariables));
        log.info("[API]:" + url + " took\t" + (System.currentTimeMillis() - start) + "ms");
        return t;
    }

    @Override
    public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        log.trace("Injecting execute(URI, HttpMethod, RequestCallback, ResponseExtractor) method. Applying retry template.");
        final long start = System.currentTimeMillis();
        T t = retryTemplate.execute(retryContext -> super.execute(url, method, requestCallback, responseExtractor));
        log.info("[API]:" + url + " took\t" + (System.currentTimeMillis() - start) + "ms");
        return t;
    }

    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Object... urlVariables) throws RestClientException {
        log.trace("Injecting execute(String, HttpMethod, RequestCallback, ResponseExtractor, Object) method. Applying retry template.");
        final long start = System.currentTimeMillis();
        T t = retryTemplate.execute(retryContext -> super.execute(url, method, requestCallback, responseExtractor, urlVariables));
        log.info("[API]:" + url + " took\t" + (System.currentTimeMillis() - start) + "ms");
        return t;
    }

    public void setRetryTemplate(final RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }
}

