package ru.gnidenko.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest5_client.Rest5ClientTransport;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;

import java.io.IOException;
import java.net.URI;

public class ElasticConfig {
    public static ElasticsearchClient getElasticsearchClient() {
        try (Rest5Client restClient = Rest5Client
            .builder(URI.create("http://localhost:9200"))
            .build();
             Rest5ClientTransport transport = new Rest5ClientTransport(
                 restClient, new JacksonJsonpMapper()
             );
             ElasticsearchClient elasticsearchClient = new ElasticsearchClient(transport)
        ) {
            return elasticsearchClient;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
