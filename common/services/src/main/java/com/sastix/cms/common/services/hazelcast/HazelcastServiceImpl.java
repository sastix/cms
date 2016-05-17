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

package com.sastix.cms.common.services.hazelcast;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.sastix.cms.common.services.hazelcast.config.HazelcastProperties;

import java.util.HashMap;
import java.util.Map;


public class HazelcastServiceImpl implements HazelcastService{
    /**
     * The properties to initialize a hazelcast instance
     * */
    private HazelcastProperties properties;

    public HazelcastServiceImpl(HazelcastProperties properties) {
        this.properties = properties;
    }

    @Override
    public HazelcastInstance hazelcastInstance() {
        // retrieves the hazelcast instance
        HazelcastInstance hazelcastInstance = Hazelcast.getHazelcastInstanceByName(properties.getConfigurationName());

        if ( hazelcastInstance == null) {
            Config config = new Config(properties.getConfigurationName());

            // creates and sets group configuration
            config.setGroupConfig(new GroupConfig(properties.getGroupName(), properties.getGroupPass()));

            // creates and sets network configuration
            NetworkConfig networkConfig = new NetworkConfig();
            networkConfig.setPort(properties.getNetworkPort());
            networkConfig.setPortAutoIncrement(properties.isPortAutoIncrement());

            // creates and sets multicast configuration
            MulticastConfig multicastConfig = new MulticastConfig();
            multicastConfig.setEnabled(properties.isMulticastEnabled());

            multicastConfig.setMulticastGroup(properties.getMulticastGroup());
            multicastConfig.setMulticastPort(properties.getMulticastPort());
            multicastConfig.setMulticastTimeoutSeconds(properties.getMulticastTimeout());
            multicastConfig.setMulticastTimeToLive(properties.getMulticastTTL());

            // creates and sets tcp/ip configuration
            TcpIpConfig tcpIpConfig = new TcpIpConfig();
            tcpIpConfig.setEnabled(properties.isTcpIpEnabled());
            tcpIpConfig.setMembers(properties.getTcpIpMembers());

            // creates and initializes the join configuration
            JoinConfig joinConfig = new JoinConfig();
            joinConfig.setMulticastConfig(multicastConfig);
            joinConfig.setTcpIpConfig(tcpIpConfig);

            // sets the network configuration
            networkConfig.setJoin(joinConfig);
            config.setNetworkConfig(networkConfig);

            // creates and sets management center config
            if(properties.isManagementEnabled()) {
                ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
                managementCenterConfig.setEnabled(properties.isManagementEnabled());
                managementCenterConfig.setUrl(properties.getManagementUrl());
                config.setManagementCenterConfig(managementCenterConfig);
            }

            MapConfig mapConfig = new MapConfig();
            mapConfig.setName(properties.getMapName());
            mapConfig.setBackupCount(properties.getBackupCount());
            mapConfig.setMaxIdleSeconds(properties.getMaxIdleSeconds());
            mapConfig.setTimeToLiveSeconds(properties.getTimeToLiveSeconds());
            MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
            maxSizeConfig.setSize(properties.getMaxSize());
            mapConfig.setMaxSizeConfig(maxSizeConfig);
            mapConfig.setEvictionPercentage(properties.getEvictionPercentage());
            mapConfig.setReadBackupData(properties.isReadBackupData());
            mapConfig.setEvictionPolicy(EvictionPolicy.valueOf(properties.getEvictionPolicy()));
            mapConfig.setMergePolicy(properties.getMergePolicy());

            MapConfig mapConfigWithTTL = new MapConfig();
            //mapConfigWithTTL.setName(CacheNames.UNIT_CONTENT);
            mapConfigWithTTL.setTimeToLiveSeconds(properties.getCustomTimeToLiveSeconds());

            Map<String, MapConfig> mapConfigs = new HashMap<>();
            mapConfigs.put(properties.getMapName(), mapConfig);
            //mapConfigs.put(CacheNames.UNIT_CONTENT, mapConfigWithTTL);

            config.setMapConfigs(mapConfigs);

            // creates a new HazelcastInstance (a new node in a cluster)
            hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        }

        return hazelcastInstance;
    }
}
