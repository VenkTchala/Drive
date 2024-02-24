package com.example.Configuration;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties
@Component
public class WebClientConfig {
    @Bean
    @Primary
    @LoadBalanced
    public WebClient webClientBuilder1(){
        HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
        return  WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

}
