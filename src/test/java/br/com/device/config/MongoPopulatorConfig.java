package br.com.device.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import java.io.IOException;

@RequiredArgsConstructor
public class MongoPopulatorConfig {

    private final ObjectMapper mapper;

    private final ResourcePatternResolver resolver;

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean mongoPopulatorFactory() throws IOException {
        final Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(this.resolver.getResources("classpath*:*.json"));
        factory.setMapper(this.mapper);
        return factory;
    }
}
