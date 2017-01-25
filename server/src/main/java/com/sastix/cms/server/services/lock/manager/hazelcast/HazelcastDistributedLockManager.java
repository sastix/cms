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

package com.sastix.cms.server.services.lock.manager.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import com.sastix.cms.server.services.lock.manager.DistributedLockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class HazelcastDistributedLockManager implements DistributedLockManager {
    private volatile ConcurrentMap<String, IMap> locks = new ConcurrentHashMap<>();
    private volatile ConcurrentMap<String, IdGenerator> idGenerators = new ConcurrentHashMap<String, IdGenerator>();

    @Autowired
    @Qualifier(value = "hazelcastInstance")
    private HazelcastInstance hazelcastInstance;


    @Override
    public <K, V> ConcurrentMap<K, V> getLockMap(String region) {
        if (locks.get(region) == null) {
            synchronized (this) {
                if (locks.get(region) == null) {
                    final IMap<K, V> map = this.hazelcastInstance.getMap(region);
                    locks.putIfAbsent(region, map);

                    return map;
                }
            }
        }

        return locks.get(region);
    }

    @Override
    public IdGenerator getIdGenerator(String value) {
        if (idGenerators.get(value) == null) {
            synchronized (this) {
                if (idGenerators.get(value) == null) {
                    return this.hazelcastInstance.getIdGenerator(value);
                }
            }
        }
        return idGenerators.get(value);
    }
}
