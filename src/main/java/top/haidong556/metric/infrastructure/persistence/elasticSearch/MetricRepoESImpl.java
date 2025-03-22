package top.haidong556.metric.infrastructure.persistence.elasticSearch;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.haidong556.metric.domain.model.metricAggregate.MetricAggregateRoot;
import top.haidong556.metric.domain.model.metricAggregate.MetricRepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MetricRepoESImpl implements MetricRepo {

    final String INDEX_NAME = "metrics_index";
    @Autowired
    private ESClient esClient;


    public boolean createIndex() throws IOException {
        IndexSettings.Builder settingsBuilder = new IndexSettings.Builder();
        IndexSettings settings = settingsBuilder.numberOfShards("1").numberOfReplicas("1").build();


        TypeMapping mappings = TypeMapping.of(m -> m
                .properties("metricAggregateId", p -> p.object(k -> k
                        .properties("metricAggregateId",p1->p1.keyword(n->n))))
                .properties("machineIdentification", p -> p.object(o -> o
                        .properties("machineIdentification", p1 -> p1.keyword(k -> k)) // 内部字段 machineIdentification，类型为 keyword
                ))
                .properties("metricJson", p -> p.text(t -> t))
                .properties("timestamp", p -> p.date(d -> d.format("strict_date_optional_time||epoch_millis")))
                .properties("metadataVo", p -> p.object(o -> o
                        .properties("beat", p1 -> p1.keyword(k -> k))
                        .properties("type", p1 -> p1.keyword(k -> k))
                        .properties("version", p1 -> p1.keyword(k -> k))
                ))
                .properties("serviceVo", p -> p.object(o -> o
                        .properties("type", p1 -> p1.keyword(k -> k))
                ))
                .properties("processEntity", p -> p.object(o -> o
                        .properties("commandLine", p1 -> p1.text(t -> t))
                        .properties("executable", p1 -> p1.keyword(k -> k))
                        .properties("name", p1 -> p1.keyword(k -> k))
                        .properties("pid", p1 -> p1.long_(l -> l))
                        .properties("state", p1 -> p1.keyword(k -> k))
                        .properties("workingDirectory", p1 -> p1.keyword(k -> k))
                        .properties("pgId", p1 -> p1.long_(l -> l))
                        .properties("args", p1 -> p1.keyword(k -> k))
                        .properties("parent", p1 -> p1.object(o1 -> o1
                                .properties("pid", p2 -> p2.long_(l -> l))
                        ))
                        .properties("cpu", p1 -> p1.object(o1 -> o1
                                .properties("startTime", p2 -> p2.date(d -> d.format("strict_date_optional_time||epoch_millis")))
                                .properties("pct", p2 -> p2.double_(d -> d))
                        ))
                        .properties("memory", p1 -> p1.object(o1 -> o1
                                .properties("pct", p2 -> p2.double_(d -> d))
                        ))
                ))
                .properties("systemEntity", p -> p.object(o -> o
                        .properties("state", p1 -> p1.keyword(k -> k))
                        .properties("network", p1 -> p1.object(o1 -> o1
                                .properties("name", p2 -> p2.keyword(k -> k))
                                .properties("in", p2 -> p2.object(o2 -> o2
                                        .properties("packets", p3 -> p3.long_(l -> l))
                                        .properties("errors", p3 -> p3.long_(l -> l))
                                        .properties("dropped", p3 -> p3.long_(l -> l))
                                        .properties("bytes", p3 -> p3.long_(l -> l))
                                ))
                                .properties("out", p2 -> p2.object(o2 -> o2
                                        .properties("packets", p3 -> p3.long_(l -> l))
                                        .properties("errors", p3 -> p3.long_(l -> l))
                                        .properties("dropped", p3 -> p3.long_(l -> l))
                                        .properties("bytes", p3 -> p3.long_(l -> l))
                                ))
                        ))
                ))
                .properties("process", p1 -> p1.object(o1 -> o1
                        .properties("state", p2 -> p2.keyword(k -> k))
                        .properties("num_threads", p2 -> p2.long_(l -> l))
                        .properties("cmdline", p2 -> p2.text(t -> t))
                        .properties("cgroup", p2 -> p2.object(o2 -> o2
                                .properties("id", p3 -> p3.keyword(k -> k))
                                .properties("path", p3 -> p3.keyword(k -> k))
                                .properties("cgroupsVersion", p3 -> p3.long_(l -> l))
                        ))
                        .properties("memory", p2 -> p2.object(o2 -> o2
                                .properties("share", p3 -> p3.long_(l -> l))
                                .properties("size", p3 -> p3.long_(l -> l))
                                .properties("rss", p3 -> p3.object(o3 -> o3
                                        .properties("bytes", p4 -> p4.long_(l -> l))
                                        .properties("pct", p4 -> p4.double_(d -> d))
                                ))
                        ))
                        .properties("cpu", p2 -> p2.object(o2 -> o2
                                .properties("startTime", p3 -> p3.date(d -> d.format("strict_date_optional_time||epoch_millis")))
                                .properties("total", p3 -> p3.object(o3 -> o3
                                        .properties("value", p4 -> p4.long_(l -> l))
                                        .properties("pct", p4 -> p4.double_(d -> d))
                                        .properties("norm", p4 -> p4.object(o4 -> o4
                                                .properties("pct", p5 -> p5.double_(d -> d))
                                        ))
                                ))
                        ))
                        .properties("fd", p2 -> p2.object(o2 -> o2
                                .properties("open", p3 -> p3.long_(l -> l))
                                .properties("limit", p3 -> p3.object(o3 -> o3
                                        .properties("hard", p4 -> p4.long_(l -> l))
                                        .properties("soft", p4 -> p4.long_(l -> l))
                                ))
                        ))
                ))
                .properties("elasticCommonSchemaVo", p -> p.object(o -> o
                        .properties("version", p1 -> p1.keyword(k -> k))
                ))
                .properties("hostEntity", p -> p.object(o -> o
                        .properties("ip", p1 -> p1.ip(i -> i))
                        .properties("mac", p1 -> p1.keyword(k -> k))
                        .properties("name", p1 -> p1.keyword(k -> k))
                        .properties("hostname", p1 -> p1.keyword(k -> k))
                        .properties("architecture", p1 -> p1.keyword(k -> k))
                        .properties("id", p1 -> p1.keyword(k -> k))
                        .properties("containerized", p1 -> p1.boolean_(b -> b))
                        .properties("os", p1 -> p1.object(o1 -> o1.enabled(false)))
                ))
                .properties("agentEntity", p -> p.object(o -> o
                        .properties("type", p1 -> p1.keyword(k -> k))
                        .properties("version", p1 -> p1.keyword(k -> k))
                        .properties("ephemeralId", p1 -> p1.keyword(k -> k))
                        .properties("id", p1 -> p1.keyword(k -> k))
                        .properties("name", p1 -> p1.keyword(k -> k))
                ))
                .properties("eventVo", p -> p.object(o -> o
                        .properties("duration", p1 -> p1.long_(l -> l))
                        .properties("dataset", p1 -> p1.keyword(k -> k))
                        .properties("module", p1 -> p1.keyword(k -> k))
                ))
                .properties("metricSetVo", p -> p.object(o -> o
                        .properties("name", p1 -> p1.keyword(k -> k))
                        .properties("period", p1 -> p1.long_(l -> l))
                ))
        );
        CreateIndexRequest request = new CreateIndexRequest.Builder().index(INDEX_NAME).settings(settings).mappings(mappings).build();

        CreateIndexResponse response = esClient.getElasticsearchClient().indices().create(request);
        return response.acknowledged();
    }

    public boolean deleteIndex() {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest.Builder().index(INDEX_NAME).build();

            DeleteIndexResponse response = esClient.getElasticsearchClient().indices().delete(request);
            return response.acknowledged();

        } catch (ElasticsearchException e) {
            // 处理索引不存在的情况
            if (e.error().type().equals("index_not_found_exception")) {
                return true; // 索引不存在时视为删除成功
            }
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public MetricAggregateRoot save(MetricAggregateRoot metricAggregateRoot) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            esClient.createDocument(INDEX_NAME, metricAggregateRoot.getMetricAggregateId().getMetricAggregateRootId(), metricAggregateRoot);

            return metricAggregateRoot;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MetricAggregateRoot findById(String id) {
        try {
            return esClient.getDocument(INDEX_NAME, id, MetricAggregateRoot.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<MetricAggregateRoot> findAll() {
        try {
            SearchRequest request = SearchRequest.of(s -> s.index(INDEX_NAME).size(1000) // 控制返回最大数量
                    .query(q -> q.matchAll(m -> m)));

            SearchResponse<MetricAggregateRoot> response = esClient.getElasticsearchClient().search(request, MetricAggregateRoot.class);

            List<MetricAggregateRoot> results = new ArrayList<>();
            for (Hit<MetricAggregateRoot> hit : response.hits().hits()) {
                results.add(hit.source());
            }
            return results;

        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean deleteById(String id) {
        try {
            return esClient.deleteDocument(INDEX_NAME, id);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public long count() {
        try {
            SearchResponse<MetricAggregateRoot> response = esClient.getElasticsearchClient().search(s -> s.index(INDEX_NAME).size(0), MetricAggregateRoot.class);
            return response.hits().total().value();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<MetricAggregateRoot> findByTimestampRange(String startTime, String endTime) {
        try {
            // 构建范围查询
            DateRangeQuery rangeQuery = new DateRangeQuery.Builder().field("timestamp").gte(startTime).lte(endTime).build();


            // 构建搜索请求
            SearchRequest request = SearchRequest.of(s -> s.index(INDEX_NAME).size(1000).query(rangeQuery._toRangeQuery()._toQuery()));

            SearchResponse<MetricAggregateRoot> response = esClient.getElasticsearchClient().search(request, MetricAggregateRoot.class);

            List<MetricAggregateRoot> results = new ArrayList<>();
            for (Hit<MetricAggregateRoot> hit : response.hits().hits()) {
                results.add(hit.source());
            }
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean saveIfNotFound(MetricAggregateRoot metricAggregateRoot) {
        try {
            MetricAggregateRoot existing = esClient.getDocument(INDEX_NAME, metricAggregateRoot.getMetricAggregateId().getMetricAggregateRootId(), MetricAggregateRoot.class);
            if (existing == null) {
                esClient.createDocument(INDEX_NAME, metricAggregateRoot.getMetricAggregateId().getMetricAggregateRootId(), metricAggregateRoot);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
