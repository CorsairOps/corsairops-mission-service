package com.corsairops.mission.client.asset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AssetServiceClientConfig {
    @Value("${asset-service.url}")
    private String assetServiceUrl;

    @Bean
    public AssetServiceClient assetServiceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(assetServiceUrl)
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(AssetServiceClient.class);
    }
}