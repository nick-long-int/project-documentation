package ru.gnidenko.repo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import ru.gnidenko.config.ElasticConfig;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ElasticRepo {
    private final ElasticsearchClient client;

    public ElasticRepo() {
        client = ElasticConfig.getElasticsearchClient();
    }

    public <T> void addToIndex(String index, String id, T obj){
        try {
            IndexResponse response = client.index(
                iBuilder ->
                    iBuilder
                        .index(index)
                        .id(id)
                        .document(obj)
            );
        } catch (IOException | ElasticsearchException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<?> searchIntoIndex(String index, String field, String query, T obj){
        try {
            var searchResponse = client.search(search -> search
                    .index(index)
                    .query(q -> q
                        .match(t -> t
                            .field(field)
                            .query(query))), obj.getClass());
            return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeClient() throws IOException {
        client.close();
    }
}
