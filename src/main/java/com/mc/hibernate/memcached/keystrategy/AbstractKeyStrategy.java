/* Copyright 2015, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mc.hibernate.memcached.keystrategy;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KeyStrategy base class that handles concatenation, cleaning, and truncating the final cache key.
 * <p>
 * Concatenates the three key components; regionName, clearIndex and key.<br>
 * Subclasses are responsible for transforming the Key object into something identifyable.
 *
 * @author Ray Krueger
 */
public abstract class AbstractKeyStrategy implements KeyStrategy {

    private static final int MAX_KEY_LENGTH = 250;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final Pattern CLEAN_PATTERN = Pattern.compile("\\s");

    public String toKey(String regionName, long clearIndex, Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        String keyString = concatenateKey(regionName, clearIndex, transformKeyObject(key));

        if (keyString.length() > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException("Key is longer than " + MAX_KEY_LENGTH + " characters, try using the Sha1KeyStrategy: " + keyString);
        }

        String finalKey = CLEAN_PATTERN.matcher(keyString).replaceAll("");
        log.debug("Final cache key: [{}]", finalKey);
        return finalKey;
    }

    protected abstract String transformKeyObject(Object key);

    protected String concatenateKey(String regionName, long clearIndex, Object key) {
        return regionName + ":" + clearIndex + ":" + String.valueOf(key);
    }
}
