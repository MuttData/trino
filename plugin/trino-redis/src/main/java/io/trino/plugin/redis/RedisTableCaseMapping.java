/*
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
package io.trino.plugin.redis;
import io.airlift.log.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Locale;

public final class RedisTableCaseMapping {
    private static final Logger log = Logger.get(RedisTableCaseMapping.class);
    private static final ConcurrentHashMap<String, String> lowercaseToOriginalTableNames = new ConcurrentHashMap<>();
    
    private RedisTableCaseMapping() {}
    
    public static void registerTableName(String schemaName, String originalTableName) {
        String key = createKey(schemaName, originalTableName.toLowerCase(Locale.ENGLISH));
        lowercaseToOriginalTableNames.put(key, originalTableName);
    }
    
    public static String getOriginalTableName(String schemaName, String lowercaseTableName) {
        String key = createKey(schemaName, lowercaseTableName);
        return lowercaseToOriginalTableNames.getOrDefault(key, lowercaseTableName);
    }
    
    private static String createKey(String schemaName, String tableName) {
        return schemaName.toLowerCase(Locale.ENGLISH) + ":" + tableName.toLowerCase(Locale.ENGLISH);
    }
}