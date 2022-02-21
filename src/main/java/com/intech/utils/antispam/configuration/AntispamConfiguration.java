package com.intech.utils.antispam.configuration;

import com.intech.utils.antispam.annotation.ChecksAspect;
import com.intech.utils.antispam.model.repository.BlockedRepository;
import com.intech.utils.antispam.model.repository.QueryLogRepository;
import com.intech.utils.antispam.service.AntispamService;
import com.intech.utils.antispam.service.BlockedService;
import com.intech.utils.antispam.service.QueryLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AntispamConfiguration {


    @PostConstruct
    public void printConfigurationMessage() {
        log.info("Module 'Antispam' initialized");
    }

    @Bean
    public BlockedService blockedService(BlockedRepository blockedRepository) {
        return new BlockedService(blockedRepository);
    }

    @Bean
    public QueryLogService queryLogService(QueryLogRepository queryLogRepository){
        return new QueryLogService(queryLogRepository);
    }

    @Bean
    public AntispamService antispamService(BlockedService blockedService, QueryLogService queryLogService) {
        return new AntispamService(blockedService, queryLogService);
    }

    @Bean
    public ChecksAspect checkAspect(AntispamService antispamService) {
        return new ChecksAspect(antispamService);
    }

}
