package com.intech.utils.antispam.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories
        (basePackages = {"com.intech.utils.antispam.model.repository"},
         entityManagerFactoryRef = "antispamDBEntityManagerFactory",
         transactionManagerRef = "antispamDBTransactionManager")
@EnableTransactionManagement
public class AntispamDatabaseConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties antispamDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource")
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
        builder.setPackagesToScan("com.intech.utils.antispam.model");
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
        hibernateProperties.setProperty("hibernate.jdbc.time_zone", "UTC");
        return hibernateProperties;
    }

}
