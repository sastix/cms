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

package com.sastix.cms.common.dataobjects;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The specific object holds all the information related to Version.
 */
@Getter @Setter @NoArgsConstructor
public class VersionDTO {

    /**
     * Minimum version of API supported, initial should be "1.0".
     */
    private double minVersion;

    /**
     * Maximum version of API.
     */
    private double maxVersion;

    private Map<String, String> versionContexts;
    
    public VersionDTO withMinVersion(double min) {
    	minVersion = min;
    	return this;
    }
    
    public VersionDTO withMaxVersion(double max) {
    	maxVersion = max;
    	return this;
    }

    public VersionDTO withVersionContext(double version, String contextPrefix) {
    	if (null == versionContexts) {
    		versionContexts = new HashMap<String,String>();
    	}
    	versionContexts.put(Double.toString(version), contextPrefix);
    	return this;
    }
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VersionDTO [minVersion=").append(minVersion)
				.append(", maxVersion=").append(maxVersion)
				.append(", versionContexts=").append(versionContexts)
				.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(maxVersion);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minVersion);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((versionContexts == null) ? 0 : versionContexts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VersionDTO))
			return false;
		VersionDTO other = (VersionDTO) obj;
		if (Double.doubleToLongBits(maxVersion) != Double
				.doubleToLongBits(other.maxVersion))
			return false;
		if (Double.doubleToLongBits(minVersion) != Double
				.doubleToLongBits(other.minVersion))
			return false;
		if (versionContexts == null) {
			if (other.versionContexts != null)
				return false;
		} else if (!versionContexts.equals(other.versionContexts))
			return false;
		return true;
	}
	
}
