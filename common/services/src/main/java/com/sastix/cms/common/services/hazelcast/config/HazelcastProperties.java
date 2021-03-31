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

package com.sastix.cms.common.services.hazelcast.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HazelcastProperties {

    public String configurationName;

    public String groupName;

    public String groupPass;

    public int networkPort;

    public boolean portAutoIncrement;

    public boolean multicastEnabled;

    public boolean managementEnabled;

    public String managementUrl;

    public String multicastGroup;

    public int multicastPort;

    public int multicastTimeout;

    public int multicastTTL;

    public boolean tcpIpEnabled;

    public List<String> tcpIpMembers;

    public String mapName;

    public int backupCount;

    public int maxIdleSeconds;

    public int timeToLiveSeconds;

    public int maxSize;

    public int evictionPercentage;

    public boolean readBackupData;

    public String evictionPolicy;

    public String mergePolicy;

    public int customTimeToLiveSeconds;

}
