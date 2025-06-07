--liquibase formatted sql

--changeset 1.1:drop-udfvalues-from-skill.sql splitStatements:false runOnChange:true
ALTER TABLE IF EXISTS ${database.defaultSchemaName}.skill DROP COLUMN IF EXISTS udfvalues;
