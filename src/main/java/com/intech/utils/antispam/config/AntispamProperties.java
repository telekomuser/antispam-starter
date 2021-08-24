package com.intech.utils.antispam.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "intech.settings")
public class AntispamProperties {
    private boolean userIdEnable = false;
    private boolean ipEnable = false;
}
