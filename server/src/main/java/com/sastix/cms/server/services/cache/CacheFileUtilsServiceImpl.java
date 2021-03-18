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

import com.sastix.cms.common.cache.CacheDTO;

import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Service
public class CacheFileUtilsServiceImpl implements CacheFileUtilsService{
    @Override
    public byte[] downloadResource(URL url) throws IOException {
        //create buffer with capacity in bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ByteBuffer bufIn = ByteBuffer.allocate(1024);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            int bytesRead;
            while ((bytesRead = rbc.read(bufIn)) > 0) {
                baos.write(bufIn.array(), 0, bytesRead);
                bufIn.rewind();
            }
            bufIn.clear();
            return baos.toByteArray();
        }finally {
            baos.close();
        }
    }

    @Override
    public boolean isExpiredCachedResource(CacheDTO cacheDTO) {
        return cacheDTO != null && cacheDTO.getCacheExpirationTime() != null
                && cacheDTO.getCacheExpirationTime().isBeforeNow();
    }
}
