package top.haidong556.metric.persistence.es;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.transform.Settings;
import org.junit.jupiter.api.*;
import top.haidong556.metric.infrastructure.persistence.elasticSearch.ESClient;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ESClientTest {

    private static ESClient esClient;
    private static final String TEST_INDEX = "test_index";
    private static final String DOCUMENT_ID = "1";

    @BeforeAll
    static void setup() throws IOException {
        esClient = new ESClient("127.0.0.1", 9200, "http");
    }

    @Test
    @Order(1)
    void createIndex() throws IOException {
        IndexSettings setting = new IndexSettings.Builder().build();
        TypeMapping  mapping= new TypeMapping.Builder().build();
        boolean created = esClient.createIndex(TEST_INDEX,setting,mapping);
        assertTrue(created, "索引创建失败");
    }

    @Test
    @Order(2)
    void getIndex() throws IOException {
        Map<String, ?> indexInfo = esClient.getIndex(TEST_INDEX);
        assertNotNull(indexInfo, "索引信息为空");
        assertTrue(indexInfo.containsKey(TEST_INDEX), "索引不存在");
    }

    @Test
    @Order(3)
    void createDocument() throws IOException {
        TestDocument document = new TestDocument("测试文档", 100);
        boolean created = esClient.createDocument(TEST_INDEX, DOCUMENT_ID, document.toString());
        assertTrue(created, "文档创建失败");
    }

    @Test
    @Order(4)
    void getDocument() throws IOException {
        TestDocument document = esClient.getDocument(TEST_INDEX, DOCUMENT_ID, TestDocument.class);
        assertNotNull(document, "获取文档失败");
        assertEquals("测试文档", document.getName(), "文档内容不匹配");
    }

    @Test
    @Order(5)
    void updateDocument() throws IOException {
        TestDocument updatedDocument = new TestDocument("更新后的文档", 200);
        boolean updated = esClient.updateDocument(TEST_INDEX, DOCUMENT_ID, updatedDocument);
        assertTrue(updated, "文档更新失败");
    }

    @Test
    @Order(6)
    void deleteDocument() throws IOException {
        boolean deleted = esClient.deleteDocument(TEST_INDEX, DOCUMENT_ID);
        assertTrue(deleted, "文档删除失败");
    }

    @Test
    @Order(7)
    void deleteIndex() throws IOException {
        boolean deleted = esClient.deleteIndex(TEST_INDEX);
        assertTrue(deleted, "索引删除失败");
    }

    static class TestDocument {
        private String name;
        private int value;

        public TestDocument() {}

        public TestDocument(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}