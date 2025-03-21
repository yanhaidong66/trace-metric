package top.haidong556.metric.infrastructure.persistence.elasticSearch;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
@Component
public class ESClient {

    private final ElasticsearchClient elasticsearchClient;
    private final String host;
    private final int port;
    private final String http;
    @Autowired
    public ESClient(ElasticsearchClient elasticsearchClient) {
        this.host = "127.0.0.1";
        this.port = 9200;
        this.http = "http";
        this.elasticsearchClient = elasticsearchClient;
    }

    public ESClient(String host, int port, String http) throws IOException {
        this.host = host;
        this.port = port;
        this.http = http;
        this.elasticsearchClient = this.getElasticsearchClient();
    }

    /**
     * 获取 Elasticsearch 客户端
     *
     * @return {@link ElasticsearchClient}
     * @throws IOException IOException
     */
    ElasticsearchClient getElasticsearchClient() throws IOException {
        RestClient restClient = RestClient.builder(
                new HttpHost(host, port, http)).build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    /**
     * 创建索引（带完整配置）
     *
     * @param indexName 索引名称
     * @param settings  索引设置（如分片、副本数）
     * @param mappings  索引映射（字段类型、格式等）
     * @return 创建结果
     * @throws IOException IO 异常
     */
    public boolean createIndex(String indexName, IndexSettings settings, TypeMapping mappings) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index(indexName)
                .settings(settings)
                .mappings(mappings)
                .build();

        CreateIndexResponse response = elasticsearchClient.indices().create(request);
        return response.acknowledged();
    }

    /**
     * 获取索引信息
     *
     * @param indexName 索引名称
     * @return 索引信息
     * @throws IOException IOException
     */
    public Map<String, IndexState> getIndex(String indexName) throws IOException {
        GetIndexResponse getIndexResponse = elasticsearchClient.indices()
                .get(s -> s.index(indexName));
        return getIndexResponse.result();
    }

    /**
     * 删除索引
     *
     * @param indexName 索引名称
     * @return 删除结果
     * @throws IOException IOException
     */
    public boolean deleteIndex(String indexName) throws IOException {
        DeleteIndexResponse deleteIndexResponse = elasticsearchClient.indices()
                .delete(s -> s.index(indexName));
        return deleteIndexResponse.acknowledged();
    }

    /**
     * 创建文档
     *
     * @param indexName 索引名称
     * @param documentId 文档ID
     * @param documentContent 文档内容
     * @return 创建结果
     * @throws IOException IOException
     */
    public boolean createDocument(String indexName, String documentId, String documentContent) throws IOException {
        IndexRequest<Object> indexRequest = new IndexRequest.Builder<>()
                .index(indexName)
                .id(documentId)
                .document(documentContent)
                .build();

        IndexResponse response = elasticsearchClient.index(indexRequest);
        return response.result() == Result.Created;
    }

    /**
     * 获取文档
     *
     * @param indexName 索引名称
     * @param documentId 文档ID
     * @param documentClass 文档类类型
     * @return 文档内容
     * @throws IOException IOException
     */
    public <T> T getDocument(String indexName, String documentId, Class<T> documentClass) throws IOException {
        GetRequest getRequest = new GetRequest.Builder()
                .index(indexName)
                .id(documentId)
                .build();

        GetResponse<T> response = elasticsearchClient.get(getRequest, documentClass);
        if (response.found()) {
            return response.source(); // 返回文档内容
        } else {
            return null; // 文档未找到
        }
    }

    /**
     * 更新文档
     *
     * @param indexName 索引名称
     * @param documentId 文档ID
     * @param updateContent 更新内容
     * @return 更新结果
     * @throws IOException IOException
     */
    public boolean updateDocument(String indexName, String documentId, Object updateContent) throws IOException {
        // 创建更新请求，包含索引、文档ID以及更新的内容
        UpdateRequest<Object, Object> updateRequest = new UpdateRequest.Builder<>()
                .index(indexName)
                .id(documentId)
                .doc(updateContent)  // 这里传入更新的文档内容
                .build();

        // 使用 Elasticsearch 客户端执行更新操作
        UpdateResponse response = elasticsearchClient.update(updateRequest, Object.class);

        // 判断更新是否成功，返回结果
        return response.result()==Result.Updated;
    }

    /**
     * 删除文档
     *
     * @param indexName 索引名称
     * @param documentId 文档ID
     * @return 删除结果
     * @throws IOException IOException
     */
    public boolean deleteDocument(String indexName, String documentId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest.Builder()
                .index(indexName)
                .id(documentId)
                .build();

        DeleteResponse response = elasticsearchClient.delete(deleteRequest);
        return response.result() == Result.Deleted;
    }
}
