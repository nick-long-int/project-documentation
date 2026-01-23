package ru.gnidenko.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

public class ElasticConfig {
    public static ElasticsearchClient getElasticsearchClient() {

            RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")
            ).build();
            RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
            );
        return new ElasticsearchClient(transport);


    }
}
