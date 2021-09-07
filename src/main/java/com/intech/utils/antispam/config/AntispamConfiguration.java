package com.intech.utils.antispam.config;

import com.intech.utils.antispam.annotations.CheckAspect;
import com.intech.utils.antispam.models.repositories.BlockedRepository;
import com.intech.utils.antispam.models.repositories.QueryLogRepository;
import com.intech.utils.antispam.services.AntispamService;
import com.intech.utils.antispam.services.BlockedService;
import com.intech.utils.antispam.services.QueryLogService;
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
    public CheckAspect checkAspect(AntispamService antispamService) {
        return new CheckAspect(antispamService);
    }

}
