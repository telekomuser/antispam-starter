package com.intech.utils.antispam.configuration;

import com.intech.utils.antispam.annotation.ChecksAspect;
import com.intech.utils.antispam.model.repository.*;
import com.intech.utils.antispam.service.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;

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
