--liquibase formatted sql

--changeset 1.1:tenant-base-data-model splitStatements:false runOnChange:true

--Activity
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.activity
(
    id                          uuid PRIMARY KEY NOT NULL,
    lastchanged                 timestamp without time zone,
    earlieststartdatetime       timestamp without time zone,
    duedatetime                 timestamp without time zone,
    startdatetime               timestamp without time zone,
    executionstage              character varying,
    enddatetime                 timestamp without time zone,
    responsibles                uuid[]                      DEFAULT ARRAY []::uuid[],
    syncstatus                  character varying,
    durationinminutes           integer,
    planneddurationinminutes    integer,
    address                     uuid,
    servicecall                 uuid,
    equipment                   uuid,
    lastindexed                 timestamp without time zone DEFAULT now(),
    udfvalues                   jsonb                       DEFAULT '[]'::jsonb,
    businesspartner             uuid,
    travel_time_to_in_minutes   integer,
    travel_time_from_in_minutes integer,
    externalid                  varchar
);

-- Address
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.address
(
    id             uuid PRIMARY KEY NOT NULL,
    location       jsonb,
    lastchanged    timestamp without time zone,
    lastindexed    timestamp without time zone DEFAULT now(),
    objectId       uuid,
    objectType     character varying,
    type           character varying,
    defaultaddress boolean          NOT NULL   DEFAULT false
);

-- Equipment
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.equipment
(
    id          uuid PRIMARY KEY NOT NULL,
    lastchanged timestamp without time zone,
    lastindexed timestamp without time zone DEFAULT now(),
    udfvalues   jsonb                       DEFAULT '[]'::jsonb,
    externalid  varchar
);

-- Person
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.person
(
    id                          uuid PRIMARY KEY NOT NULL,
    locationlastuserchangeddate timestamp without time zone,
    location                    jsonb,
    externalid                  character varying,
    crowdtype                   character varying,
    type                        character varying,
    inactive                    boolean,
    plannableresource           boolean,
    refid                       uuid,
    lastchanged                 timestamp without time zone,
    lastindexed                 timestamp without time zone DEFAULT now(),
    udfvalues                   jsonb                       DEFAULT '[]'::jsonb
);

--PersonReservation
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.personreservation
(
    id          uuid PRIMARY KEY NOT NULL,
    persons     uuid[]                      DEFAULT ARRAY []::uuid[],
    startdate   timestamp without time zone,
    enddate     timestamp without time zone,
    exclusive   boolean,
    lastchanged timestamp without time zone,
    lastindexed timestamp without time zone DEFAULT now(),
    address     uuid
);

--PersonWorkTimePattern
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.personworktimepattern
(
    id              uuid PRIMARY KEY NOT NULL,
    worktimepattern uuid,
    person          uuid,
    startdate       timestamp without time zone,
    enddate         timestamp without time zone,
    lastchanged     timestamp without time zone,
    lastindexed     timestamp without time zone DEFAULT now()
);

--Requirement
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.requirement
(
    id          uuid PRIMARY KEY NOT NULL,
    mandatory   boolean,
    tag         uuid,
    activity    uuid,
    lastindexed timestamp without time zone DEFAULT now(),
    lastchanged timestamp without time zone
);

--ServiceCall
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.servicecall
(
    id              uuid PRIMARY KEY NOT NULL,
    priority        character varying,
    lastchanged     timestamp without time zone,
    lastindexed     timestamp without time zone DEFAULT now(),
    udfvalues       jsonb                       DEFAULT '[]'::jsonb,
    businesspartner uuid,
    externalid      varchar
);

--Skill
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.skill
(
    id          uuid PRIMARY KEY NOT NULL,
    tag         uuid,
    person      uuid,
    startdate   varchar,
    enddate     varchar,
    lastchanged timestamp without time zone,
    lastindexed timestamp without time zone DEFAULT now(),
    udfvalues   jsonb                       DEFAULT '[]'::jsonb,
    startTime   varchar,
    endTime     varchar,
    days        jsonb                       DEFAULT '[]'::jsonb
);

--Tag
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.tag
(
    id          uuid PRIMARY KEY NOT NULL,
    name        character varying,
    externalid  character varying,
    lastchanged timestamp without time zone,
    lastindexed timestamp without time zone DEFAULT now()
);

--UDF Meta
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.udfmeta
(
    id          uuid PRIMARY KEY NOT NULL,
    name        character varying,
    lastchanged timestamp without time zone,
    lastindexed timestamp without time zone DEFAULT now()
);

--Worktime
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.worktime
(
    id            uuid PRIMARY KEY NOT NULL,
    person        uuid,
    startdatetime timestamp without time zone,
    enddatetime   timestamp without time zone,
    lastchanged   timestamp without time zone,
    lastindexed   timestamp without time zone DEFAULT now()
);

-- WorkTimePattern
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.worktimepattern
(
    id          uuid PRIMARY KEY NOT NULL,
    weeks       jsonb,
    lastchanged timestamp without time zone,
    lastindexed timestamp without time zone DEFAULT now()
);

-- BusinessPartner
CREATE TABLE IF NOT EXISTS ${database.defaultSchemaName}.businesspartner
(
    id          uuid PRIMARY KEY NOT NULL,
    lastchanged timestamp without time zone,
    lastindexed timestamp without time zone DEFAULT now(),
    udfvalues   jsonb                       DEFAULT '[]'::jsonb,
    externalid  varchar
);

CREATE OR REPLACE FUNCTION ${database.defaultSchemaName}.trigger_last_indexed() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
BEGIN
    NEW.lastindexed = now();
    RETURN NEW;
END;
$$;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.activity;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.activity
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.address;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.address
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.equipment;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.equipment
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.person;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.person
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.personreservation;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.personreservation
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.personworktimepattern;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.personworktimepattern
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.requirement;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.requirement
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.servicecall;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.servicecall
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.skill;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.skill
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.tag;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.tag
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.udfmeta;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.udfmeta
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.worktime;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.worktime
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.worktimepattern;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.worktimepattern
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

BEGIN;
DROP TRIGGER IF EXISTS update_lastindexed on ${database.defaultSchemaName}.businesspartner;
CREATE TRIGGER update_lastindexed
    BEFORE UPDATE
    ON ${database.defaultSchemaName}.businesspartner
    FOR EACH ROW
EXECUTE PROCEDURE ${database.defaultSchemaName}.trigger_last_indexed();
COMMIT;

-- Index
CREATE INDEX IF NOT EXISTS requirement_activity ON ${database.defaultSchemaName}.requirement (activity);
CREATE INDEX IF NOT EXISTS idx_activity_date_boundaries ON ${database.defaultSchemaName}.activity (startdatetime, enddatetime) WHERE startdatetime IS NOT NULL AND enddatetime IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_activity_still_open ON ${database.defaultSchemaName}.activity (executionstage) WHERE executionstage NOT IN ('CLOSED', 'CANCELLED');
CREATE INDEX IF NOT EXISTS idx_activity_responsibles ON ${database.defaultSchemaName}.activity USING gin (responsibles);