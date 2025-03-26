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

import java.util.concurrent.ConcurrentHashMap;
import java.util.Locale;

public final class RedisTableCaseMapping {
    private static final ConcurrentHashMap<String, String> lowercaseToOriginalSchemaNames = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> lowercaseToOriginalTableNames = new ConcurrentHashMap<>();
    
    private RedisTableCaseMapping() {}
    
    public static void registerNames(String originalSchemaName, String originalTableName) {
        String schemaKey = originalSchemaName.toLowerCase(Locale.ENGLISH);
        String tableKey = createKey(schemaKey, originalTableName.toLowerCase(Locale.ENGLISH));
        
        lowercaseToOriginalSchemaNames.put(schemaKey, originalSchemaName);
        lowercaseToOriginalTableNames.put(tableKey, originalTableName);
    }
    
    public static String getOriginalTableName(String lowercaseSchemaName, String lowercaseTableName) {
        String tableKey = createKey(lowercaseSchemaName, lowercaseTableName);
        return lowercaseToOriginalTableNames.getOrDefault(tableKey, lowercaseTableName);
    }
    
    public static String getOriginalSchemaName(String lowercaseSchemaName) {
        return lowercaseToOriginalSchemaNames.getOrDefault(lowercaseSchemaName, lowercaseSchemaName);
    }
    
    private static String createKey(String schemaName, String tableName) {
        return schemaName + ":" + tableName;
    }
}