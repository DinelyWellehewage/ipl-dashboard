package io.javapro.ipl_dashboard.data;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration

public class DataSourceConfig {
     @Bean
     @Primary
    public DataSourceTransactionManager customTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
