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

package com.sastix.cms.server.services.content.impl;

import com.sastix.cms.server.domain.entities.Tenant;
import com.sastix.cms.server.domain.repositories.TenantRepository;
import com.sastix.cms.server.services.content.HashedDirectoryService;
import com.sastix.cms.server.services.content.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    HashedDirectoryService hashedDirectoryService;

    @Autowired
    TenantRepository tenantRepository;

    @PostConstruct
    private void init() {
        checkTenants();
    }

    @Override
    public void checkTenants() {
        final Iterable<Tenant> tenants = tenantRepository.findAll();

        for (final Tenant tenant : tenants) {
            final String tenantID = tenant.getTenantId();
            final String tenantChecksum = tenant.getChecksum();
            String volumeChecksum;
            try {
                volumeChecksum = new String(hashedDirectoryService.getChecksum(tenantID));
            } catch (IOException e) {
                throw new RuntimeException("Checksum file for [" + tenantID + "] is missing");
            }

            if (!tenantChecksum.equals(volumeChecksum)) {
                throw new RuntimeException("Corrupted VOLUME for [" + tenantID + "]. Expected checksum: [" + tenantChecksum + "] Actual: [" + volumeChecksum + "]");
            }
        }
    }

    public void createTenantChecksum(final String tenantID) throws IOException {

        final Tenant dbTenant = tenantRepository.findByTenantId(tenantID);
        if (dbTenant != null) {
            //Tenant Checksum file already exist
            return;
        }

        //Create Directories
        hashedDirectoryService.createTenantDirectory(tenantID);

        final String uuid = UUID.randomUUID().toString() + tenantID;
        final String hashedUUID = hashedDirectoryService.hashText(uuid);


        //Create checksum file.
        hashedDirectoryService.storeChecksum(tenantID, hashedUUID);

        //Create new Tenant
        final Tenant tenant = new Tenant();
        tenant.setTenantId(tenantID);
        tenant.setVolume(hashedDirectoryService.getVolume());
        tenant.setChecksum(hashedUUID);

        //Save Tenant to db
        tenantRepository.save(tenant);
    }

}
