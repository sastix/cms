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

package com.sastix.cms.common.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.*;

/**
 * The specific object holds all the information related to a CacheDTO.
 */
@Getter @Setter @NoArgsConstructor
public class CacheDTO implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 4215735359530723728L;

    /**
     * The key of the object to be cached
     */
    private String cacheKey;

    /**
     * The cache blob binary
     */
    byte[] cacheBlobBinary;

    /**
     * The cache blob uri
     */
    String cacheBlobURI;

    /**
     * An optional cache region to be used to group related keys together.
     * If not specified, the default region is used, named “default”
     */
    String cacheRegion;

    /**
     * An optional cache expiration time for this entry.
     * The server does not return entries that are past
     * the expiration time and silently removes them from the cache
     */
    DateTime cacheExpirationTime;

    /**
     * Constructor with mandatory fields
     *
     * @param cacheKey
     * @param cacheBlobBinary
     */
    public CacheDTO(String cacheKey, byte[] cacheBlobBinary) {
        this.cacheKey = cacheKey;
        this.cacheBlobBinary = cacheBlobBinary;
    }

    public CacheDTO(String cacheKey, Serializable cacheBlobObject, String cacheRegion) {
        this.cacheKey = cacheKey;
        this.cacheRegion = cacheRegion;
        if (cacheBlobObject instanceof byte[]) {
            this.cacheBlobBinary = (byte[]) cacheBlobObject;
        } else {
            this.cacheBlobBinary = serialize(cacheBlobObject);
        }
    }

    @JsonIgnore
    private byte[] serialize(Serializable cacheBlobObject) {
        ObjectOutputStream oout = null;
        ByteArrayOutputStream bout = null;

        try {
            bout = new ByteArrayOutputStream(4096);
            oout = new ObjectOutputStream(bout);
            oout.writeObject(cacheBlobObject);
            oout.flush();
            byte[] result = bout.toByteArray();
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize object ", e);
        } finally {
            if (oout != null) {
                try {
                    oout.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
            if (bout != null) {
                try {
                    bout.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }


    }


    /**
     * Constructor with mandatory fields
     *
     * @param cacheKey
     * @param cacheBlobURI
     */
    public CacheDTO(String cacheKey, String cacheBlobURI) {
        this.cacheKey = cacheKey;
        this.cacheBlobURI = cacheBlobURI;
    }

    /**
     * A more generic method to retrieve the object directly from the carrier byte[]
     *
     * @param <T> the object type required
     * @return the return type
     */
    @JsonIgnore
    public <T> T getCacheBlobBinaryObject() {
        ByteArrayInputStream bis = new ByteArrayInputStream(getCacheBlobBinary());
        ObjectInput in = null;
        T object;
        try {
            in = new ObjectInputStream(bis);
            object = (T) in.readObject();
            return object;
        } catch (Exception e) {
            throw new RuntimeException("Unable to deserialize object ", e);
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

}
