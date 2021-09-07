package com.intech.utils.antispam.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories
        (basePackages = {"com.intech.utils.antispam.models.repositories"},
                entityManagerFactoryRef = "antispamDBEntityManagerFactory",
                transactionManagerRef = "antispamDBTransactionManager")
@EnableTransactionManagement
public class AntispamDatabaseConfiguration {

    @Bean
    @ConfigurationProperties("datasource.antispam")
    public DataSourceProperties antispamDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("datasource.antispam")
    public DataSource antispamDBDataSource() {
        return antispamDatasourceProperties().initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean antispamDBEntityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(false);
        LocalContainerEntityManagerFactoryBean builder = new LocalContainerEntityManagerFactoryBean();
        builder.setJpaVendorAdapter(vendorAdapter);
        builder.setDataSource(antispamDBDataSource());
        builder.setJpaProperties(additionalProperties());
        builder.setPackagesToScan("com.intech.utils.antispam.models");
        builder.setPersistenceUnitName("antispamDBPersistenceUnit");
        return builder;
    }

    @Bean
    public JpaTransactionManager antispamDBTransactionManager(
            @Qualifier("antispamDBEntityManagerFactory") final EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

    public Properties additionalProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "none");
        return hibernateProperties;
    }

}
