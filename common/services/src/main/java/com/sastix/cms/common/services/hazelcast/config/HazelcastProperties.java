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

    public HazelcastProperties() {
    }

    public HazelcastProperties(String configurationName, String groupName, String groupPass, int networkPort,
                               boolean portAutoIncrement, boolean multicastEnabled, boolean managementEnabled, String managementUrl,
                               String multicastGroup, int multicastPort, int multicastTimeout, int multicastTTL, boolean tcpIpEnabled,
                               List<String> tcpIpMembers, String mapName, int backupCount, int maxIdleSeconds, int timeToLiveSeconds,
                               int maxSize, int evictionPercentage, boolean readBackupData, String evictionPolicy, String mergePolicy,
                               int customTimeToLiveSeconds) {
        this.configurationName = configurationName;
        this.groupName = groupName;
        this.groupPass = groupPass;
        this.networkPort = networkPort;
        this.portAutoIncrement = portAutoIncrement;
        this.multicastEnabled = multicastEnabled;
        this.managementEnabled = managementEnabled;
        this.managementUrl = managementUrl;
        this.multicastGroup = multicastGroup;
        this.multicastPort = multicastPort;
        this.multicastTimeout = multicastTimeout;
        this.multicastTTL = multicastTTL;
        this.tcpIpEnabled = tcpIpEnabled;
        this.tcpIpMembers = tcpIpMembers;
        this.mapName = mapName;
        this.backupCount = backupCount;
        this.maxIdleSeconds = maxIdleSeconds;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.maxSize = maxSize;
        this.evictionPercentage = evictionPercentage;
        this.readBackupData = readBackupData;
        this.evictionPolicy = evictionPolicy;
        this.mergePolicy = mergePolicy;
        this.customTimeToLiveSeconds = customTimeToLiveSeconds;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPass() {
        return groupPass;
    }

    public void setGroupPass(String groupPass) {
        this.groupPass = groupPass;
    }

    public int getNetworkPort() {
        return networkPort;
    }

    public void setNetworkPort(int networkPort) {
        this.networkPort = networkPort;
    }

    public boolean isPortAutoIncrement() {
        return portAutoIncrement;
    }

    public void setPortAutoIncrement(boolean portAutoIncrement) {
        this.portAutoIncrement = portAutoIncrement;
    }

    public boolean isMulticastEnabled() {
        return multicastEnabled;
    }

    public void setMulticastEnabled(boolean multicastEnabled) {
        this.multicastEnabled = multicastEnabled;
    }

    public boolean isManagementEnabled() {
        return managementEnabled;
    }

    public void setManagementEnabled(boolean managementEnabled) {
        this.managementEnabled = managementEnabled;
    }

    public String getManagementUrl() {
        return managementUrl;
    }

    public void setManagementUrl(String managementUrl) {
        this.managementUrl = managementUrl;
    }

    public String getMulticastGroup() {
        return multicastGroup;
    }

    public void setMulticastGroup(String multicastGroup) {
        this.multicastGroup = multicastGroup;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(int multicastPort) {
        this.multicastPort = multicastPort;
    }

    public int getMulticastTimeout() {
        return multicastTimeout;
    }

    public void setMulticastTimeout(int multicastTimeout) {
        this.multicastTimeout = multicastTimeout;
    }

    public int getMulticastTTL() {
        return multicastTTL;
    }

    public void setMulticastTTL(int multicastTTL) {
        this.multicastTTL = multicastTTL;
    }

    public boolean isTcpIpEnabled() {
        return tcpIpEnabled;
    }

    public void setTcpIpEnabled(boolean tcpIpEnabled) {
        this.tcpIpEnabled = tcpIpEnabled;
    }

    public List<String> getTcpIpMembers() {
        return tcpIpMembers;
    }

    public void setTcpIpMembers(List<String> tcpIpMembers) {
        this.tcpIpMembers = tcpIpMembers;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getBackupCount() {
        return backupCount;
    }

    public void setBackupCount(int backupCount) {
        this.backupCount = backupCount;
    }

    public int getMaxIdleSeconds() {
        return maxIdleSeconds;
    }

    public void setMaxIdleSeconds(int maxIdleSeconds) {
        this.maxIdleSeconds = maxIdleSeconds;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getEvictionPercentage() {
        return evictionPercentage;
    }

    public void setEvictionPercentage(int evictionPercentage) {
        this.evictionPercentage = evictionPercentage;
    }

    public boolean isReadBackupData() {
        return readBackupData;
    }

    public void setReadBackupData(boolean readBackupData) {
        this.readBackupData = readBackupData;
    }

    public String getEvictionPolicy() {
        return evictionPolicy;
    }

    public void setEvictionPolicy(String evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
    }

    public String getMergePolicy() {
        return mergePolicy;
    }

    public void setMergePolicy(String mergePolicy) {
        this.mergePolicy = mergePolicy;
    }

    public int getCustomTimeToLiveSeconds() {
        return customTimeToLiveSeconds;
    }

    public void setCustomTimeToLiveSeconds(int customTimeToLiveSeconds) {
        this.customTimeToLiveSeconds = customTimeToLiveSeconds;
    }

}
