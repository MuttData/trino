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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.trino.spi.connector.ColumnHandle;
import io.trino.spi.connector.ConnectorTableHandle;
import io.trino.spi.connector.SchemaTableName;
import io.trino.spi.predicate.TupleDomain;

import static java.util.Objects.requireNonNull;

/**
 * Redis specific {@link ConnectorTableHandle}.
 *
 * @param schemaName The schema name for this table. Is set through configuration and read
 * using {@link RedisConnectorConfig#getDefaultSchema()}. Usually 'default'.
 * @param tableName The table name used by Trino.
 */
public record RedisTableHandle(
        String schemaName,
        String tableName,
        String keyDataFormat,
        String valueDataFormat,
        String keyName,
        TupleDomain<ColumnHandle> constraint,
        String originalTableName)
        implements ConnectorTableHandle
{
    @JsonCreator
    public RedisTableHandle(
            @JsonProperty("schemaName") String schemaName,
            @JsonProperty("tableName") String tableName,
            @JsonProperty("keyDataFormat") String keyDataFormat,
            @JsonProperty("valueDataFormat") String valueDataFormat,
            @JsonProperty("keyName") String keyName,
            @JsonProperty("constraint") TupleDomain<ColumnHandle> constraint,
            @JsonProperty("originalTableName") String originalTableName)
    {
        this.schemaName = requireNonNull(schemaName, "schemaName is null");
        this.tableName = requireNonNull(tableName, "tableName is null");
        this.keyDataFormat = requireNonNull(keyDataFormat, "keyDataFormat is null");
        this.valueDataFormat = requireNonNull(valueDataFormat, "valueDataFormat is null");
        this.constraint = requireNonNull(constraint, "constraint is null");
        this.originalTableName = originalTableName != null ? originalTableName : tableName;
        this.keyName = keyName;
    }

    // Constructor overload for backward compatibility
    public RedisTableHandle(
            String schemaName,
            String tableName,
            String keyDataFormat,
            String valueDataFormat,
            String keyName,
            TupleDomain<ColumnHandle> constraint)
    {
        this(schemaName, tableName, keyDataFormat, valueDataFormat, keyName, constraint,
             RedisTableCaseMapping.getOriginalTableName(schemaName, tableName));
    }

    public SchemaTableName toSchemaTableName()
    {
        return new SchemaTableName(schemaName, tableName);
    }

    @JsonProperty
    public String getOriginalTableName() {
        return originalTableName;
    }
}
