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

package com.sastix.cms.server.services.cache;

import com.sastix.cms.server.CmsServer;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@ActiveProfiles({"production", "test"})
@SpringBootTest(classes = CmsServer.class)
public class UIDServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(UIDServiceTest.class);

    @Autowired
    CacheService cacheService;

    @Test
    public void createUIDsTest() {
        String region = "region0";
        String uid1 = cacheService.getUID(region);
        String uid2 = cacheService.getUID(region);
        String uid3 = cacheService.getUID(region);
        String uid4 = cacheService.getUID(region);

        assertNotNull(uid1);
        assertNotNull(uid2);
        assertNotNull(uid3);
        assertNotNull(uid4);
    }

    /**
     * This test will create some threads to ask in 'parallel' for new ids from 2 regions.
     * Based on module 2 operation, a region is selected in order to force repeated ids
     * from hazelcast. A helper map is used to validate that the service is eventually creating
     * unique ids. If we were using only the hazelcast idGenerator the map should have 250 items
     * because of the duplicates
     */

    static final int NTHREDS = 15;
    static final int numberOfTasks = 500;
    CountDownLatch latch = new CountDownLatch(numberOfTasks);
    boolean duplicateFound = false;
    Map<String, String> ids = new ConcurrentHashMap<>();
    Map<String, Map<String, String>> regionIdsMap = new ConcurrentHashMap<>();

    @Test
    public void massiveUIDCreatorTest() throws InterruptedException {

        String region1 = "r1";
        String region2 = "r2";
        regionIdsMap.put(region1, new HashMap<>());
        regionIdsMap.put(region2, new HashMap<>());
        ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
        for (int i = 0; i < numberOfTasks; i++) {
            String region = region1;
            if (i % 2 == 0) {
                region = region2;
            }
            Runnable worker = new UIDRunnable(region);
            executor.execute(worker);
        }

        try {
            latch.await();
        } catch (InterruptedException E) {
            // handle
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(numberOfTasks, ids.size());
        assertEquals(numberOfTasks / 2, regionIdsMap.get(region1).size());
        assertEquals(numberOfTasks / 2, regionIdsMap.get(region2).size());
        assertTrue(!duplicateFound);
        LOG.info("Finished all threads");
    }

    class UIDRunnable implements Runnable {
        String region;

        public UIDRunnable(String region) {
            this.region = region;
        }

        @Override
        public void run() {
            String uid = cacheService.getUID(region);
            if (!ids.containsKey(uid)) {
                ids.put(uid, uid);
                LOG.info("Adding: " + uid);

            } else {
                duplicateFound = true;
                LOG.info("All ready contained (region " + region + "): " + uid);
            }

            Map<String, String> regionMap = regionIdsMap.get(region);
            if (!regionMap.containsKey(uid)) {
                regionMap.put(uid, uid);
                LOG.info("Adding (region " + region + "): " + uid);

            } else {
                LOG.info("All ready contained (region " + region + "): " + uid);
            }
            latch.countDown();
        }
    }
}
