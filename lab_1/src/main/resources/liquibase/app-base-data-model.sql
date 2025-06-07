--liquibase formatted sql

--changeset 1.1:app-base-data-model splitStatements:false runOnChange:true
COMMENT ON DATABASE "cloud-ai-read-model" IS 'maintainer:1111';

-- Tenant Configuration (not part of FSM model)
CREATE TABLE IF NOT EXISTS public.tenantconfiguration
(
    id              uuid PRIMARY KEY NOT NULL,
    lastchanged     timestamp without time zone,
    accountid       bigint,
    companyid       bigint,
    indexingenabled boolean,
    queryingenabled boolean
);

CREATE TABLE IF NOT EXISTS public.tenantconfigurationhistory
(
    id              uuid PRIMARY KEY            NOT NULL,
    createddate     timestamp without time zone NOT NULL,
    accountid       bigint                      NOT NULL,
    companyid       bigint                      NOT NULL,
    indexingenabled boolean                     NOT NULL,
    queryingenabled boolean                     NOT NULL
);
