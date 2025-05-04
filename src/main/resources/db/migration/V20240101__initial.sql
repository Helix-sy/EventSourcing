DROP SEQUENCE IF EXISTS domain_event_entry_seq;
DROP TABLE IF EXISTS domain_event_entry CASCADE;
DROP TABLE IF EXISTS snapshot_event_entry CASCADE;
DROP TABLE IF EXISTS token_entry CASCADE;
DROP TABLE IF EXISTS association_value_entry CASCADE;
DROP TABLE IF EXISTS saga_entry CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS events CASCADE;

CREATE SEQUENCE domain_event_entry_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE domain_event_entry (
    global_index         BIGINT       NOT NULL,
    sequence_number      BIGINT       NOT NULL,
    aggregate_identifier VARCHAR(255) NOT NULL,
    event_identifier     VARCHAR(255) NOT NULL UNIQUE,
    payload_revision     VARCHAR(255),
    payload_type         VARCHAR(255) NOT NULL,
    time_stamp           VARCHAR(255) NOT NULL,
    type                 VARCHAR(255),
    meta_data            OID,
    payload              OID          NOT NULL,
    PRIMARY KEY (global_index),
    UNIQUE (aggregate_identifier, sequence_number)
);

CREATE TABLE snapshot_event_entry (
    sequence_number      BIGINT       NOT NULL,
    aggregate_identifier VARCHAR(255) NOT NULL,
    event_identifier     VARCHAR(255) NOT NULL UNIQUE,
    payload_revision     VARCHAR(255),
    payload_type         VARCHAR(255) NOT NULL,
    time_stamp           VARCHAR(255) NOT NULL,
    type                 VARCHAR(255) NOT NULL,
    meta_data            OID,
    payload              OID          NOT NULL,
    PRIMARY KEY (sequence_number, aggregate_identifier, type)
);

CREATE TABLE token_entry (
    segment        INTEGER      NOT NULL,
    owner          VARCHAR(255),
    processor_name VARCHAR(255) NOT NULL,
    timestamp      VARCHAR(255) NOT NULL,
    token_type     VARCHAR(255),
    token          OID,
    PRIMARY KEY (segment, processor_name)
);

CREATE TABLE association_value_entry (
    id                BIGINT       NOT NULL,
    association_key   VARCHAR(255) NOT NULL,
    association_value VARCHAR(255),
    saga_id           VARCHAR(255) NOT NULL,
    saga_type         VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE saga_entry (
    revision        VARCHAR(255),
    saga_id         VARCHAR(255) NOT NULL,
    saga_type       VARCHAR(255),
    serialized_saga OID,
    PRIMARY KEY (saga_id)
);

CREATE TABLE products (
    product_id BIGINT PRIMARY KEY,
    brand VARCHAR(255),
    price DECIMAL(10, 2),
    category_id BIGINT,
    category_code VARCHAR(255)
);

CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    event_time TIMESTAMP NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    product_id BIGINT REFERENCES products(product_id),
    user_id BIGINT,
    user_session VARCHAR(255)
);
