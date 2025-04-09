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
package io.trino.plugin.elasticsearch;

import com.google.common.collect.ImmutableMap;
import io.trino.testing.AbstractTestQueryFramework;
import io.trino.testing.QueryRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static io.trino.plugin.elasticsearch.ElasticsearchServer.ELASTICSEARCH_8_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class TestElasticsearchInternalColumnsVisibility
        extends AbstractTestQueryFramework
{
    private ElasticsearchServer elasticsearch;

    @Override
    protected QueryRunner createQueryRunner()
            throws Exception
    {
        elasticsearch = closeAfterClass(new ElasticsearchServer(ELASTICSEARCH_8_IMAGE));

        return ElasticsearchQueryRunner.builder(elasticsearch)
                .addConnectorProperties(ImmutableMap.of("elasticsearch.hide-internal-columns", "false"))
                .setInitialTables(REQUIRED_TPCH_TABLES)
                .build();
    }

    @Test
    public void testInternalColumnsVisible()
    {
        // Verify that internal columns like _id, _source, and _score are visible in the metadata
        assertThat(computeActual("SELECT column_name FROM information_schema.columns WHERE table_schema = 'tpch' AND table_name = 'nation'")
                .getOnlyColumnAsSet())
                .contains("_id", "_source", "_score");
        
        // Test that we can query using internal columns
        assertThat(query("SELECT nationkey, _id FROM nation WHERE nationkey = 1").getRowCount()).isEqualTo(1);
        assertThat(query("SELECT nationkey, _id FROM nation LIMIT 1").getRowCount()).isEqualTo(1);
    }
}