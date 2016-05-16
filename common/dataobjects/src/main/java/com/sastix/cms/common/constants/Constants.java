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

package com.sastix.cms.common.constants;

/**
 * This is a constants interface that is SPECIFIC ONLY to the common package.
 * Other constants (e.g. CR/DL/DC) should be placed to their OWN constants
 * interface, otherwise you risk re-deploying everything just to refer to a new
 * constant.
 * 
 * @author tangelatos
 */
public interface Constants {

    String GET_API_VERSION = "/apiversion";
    
    String DEFAULT_LANGUAGE = "en";

}
