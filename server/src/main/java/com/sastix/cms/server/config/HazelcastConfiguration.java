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

package com.sastix.cms.server.config;

import com.hazelcast.core.HazelcastInstance;
import com.sastix.cms.common.services.hazelcast.HazelcastServiceImpl;
import com.sastix.cms.common.services.hazelcast.config.HazelcastProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan("com.sastix.cms")
public class HazelcastConfiguration {

    @Value("${hazelcast.config.name}")
    private String configurationName;

    @Value("${hazelcast.config.group.name}")
    private String groupName;

    @Value("${hazelcast.config.group.pass}")
    private String groupPass;

    @Value("${hazelcast.config.network.port}")
    private int networkPort;

    @Value("${hazelcast.config.network.port.auto.increment}")
    private boolean portAutoIncrement;

    @Value("${hazelcast.config.network.multicast.enabled}")
    private boolean multicastEnabled;

    @Value("${hazelcast.config.management.enabled}")
    private boolean managementEnabled;

    @Value("${hazelcast.config.management.url}")
    private String managementUrl;

    @Value("${hazelcast.config.network.multicast.group}")
    private String multicastGroup;

    @Value("${hazelcast.config.network.multicast.port}")
    private int multicastPort;

    @Value("${hazelcast.config.network.multicast.timeout}")
    private int multicastTimeout;

    @Value("${hazelcast.config.network.multicast.time.to.live}")
    private int multicastTTL;

    @Value("${hazelcast.config.network.tcpip.enabled}")
    private boolean tcpIpEnabled;

    @Value("${hazelcast.config.network.tcpip.members}")
    private List<String> tcpIpMembers;

    @Value("${hazelcast.config.map.name}")
    private String mapName;

    @Value("${hazelcast.config.map.backupCount}")
    private int backupCount;

    @Value("${hazelcast.config.map.maxIdleSeconds}")
    private int maxIdleSeconds;

    @Value("${hazelcast.config.map.timeToLiveSeconds}")
    private int timeToLiveSeconds;

    @Value("${hazelcast.config.map.maxSize}")
    private int maxSize;

    @Value("${hazelcast.config.map.evictionPercentage}")
    private int evictionPercentage;

    @Value("${hazelcast.config.map.readBackupData}")
    private boolean readBackupData;

    @Value("${hazelcast.config.map.evictionPolicy}")
    private String evictionPolicy;

    @Value("${hazelcast.config.map.mergePolicy}")
    private String mergePolicy;
    
    @Value("${hazelcast.config.map.customTimeToLiveSeconds}")
    private int customTimeToLiveSeconds;

    @Autowired
    HazelcastProperties hazelcastProperties;

    @Bean(name = "hazelcastInstance")
    public HazelcastInstance hazelcastInstance(){
        return (new HazelcastServiceImpl(hazelcastProperties)).hazelcastInstance();
    }

    @Bean
    HazelcastProperties hazelcastProperties(){
        HazelcastProperties properties = new HazelcastProperties(
                configurationName,
                groupName,groupPass,
                networkPort,portAutoIncrement,
                multicastEnabled,
                managementEnabled,managementUrl,
                multicastGroup,multicastPort,multicastTimeout,multicastTTL,
                tcpIpEnabled,tcpIpMembers,
                mapName,backupCount,maxIdleSeconds,
                timeToLiveSeconds,maxSize,evictionPercentage,readBackupData,
                evictionPolicy,mergePolicy, customTimeToLiveSeconds);
        return properties;
    }

}
