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

package com.mc.hibernate.memcached;

import java.util.Map;

public class PropertiesHelper {

    private Map properties;

    public PropertiesHelper(Map properties) {
        this.properties = properties;
    }

    public String get(String key) {
        return (String) properties.get(key);
    }

    public String get(String key, String defaultVal) {
        String val = get(key);
        return val == null ? defaultVal : val;
    }

    public String findValue(String... keys) {
        for (String key : keys) {
            String value = get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        String val = get(key);
        return val == null ? defaultVal : Boolean.parseBoolean(val);
    }

    public long getLong(String key, long defaultVal) {
        String val = get(key);
        return val == null ? defaultVal : Long.parseLong(val);
    }

    public int getInt(String key, int defaultVal) {
        return (int) getLong(key, defaultVal);
    }

    public double getDouble(String key, double defaultVal) {
        String val = get(key);
        return val == null ? defaultVal : Double.parseDouble(val);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> type, T defaultValue) {
        String val = get(key);
        return val == null ? defaultValue : Enum.valueOf(type, val);
    }
}
