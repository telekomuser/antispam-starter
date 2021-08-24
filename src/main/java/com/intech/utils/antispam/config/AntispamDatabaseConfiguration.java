package com.intech.utils.antispam.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(DataSource.class)
public class AntispamDatabaseConfiguration {

    @Bean
    @ConditionalOnBean(name = "antispamDataSource")
    @ConditionalOnMissingBean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.baeldung.autoconfiguration.example");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        return em;
    }

    @Bean
    @ConditionalOnProperty(
            name = "intech",
            value = "antispam")
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("");

        return dataSource;
    }

}
