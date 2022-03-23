CREATE SCHEMA IF NOT EXISTS antispam;
CREATE TABLE IF NOT EXISTS antispam.query_log(
     id              BIGSERIAL PRIMARY KEY,
     user_id         VARCHAR(255) NOT NULL,
     query_type      VARCHAR(255) NOT NULL,
     result          VARCHAR(255) NOT NULL,
     date_added      TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE IF NOT EXISTS antispam.blocked(
     id              BIGSERIAL PRIMARY KEY,
     user_id         VARCHAR(255) NOT NULL,
     query_type      VARCHAR(255) NOT NULL,
     date_added      VARCHAR(255) NOT NULL,
     block_period    INT NOT NULL,
     block_time_unit VARCHAR(255) NOT NULL,
     block_start     TIMESTAMP WITH TIME ZONE NOT NULL,
     block_end       TIMESTAMP WITH TIME ZONE NOT NULL,
     repeat          BOOLEAN NOT NULL
);