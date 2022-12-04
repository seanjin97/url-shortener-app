CREATE TABLE IF NOT EXISTS url_mapping
(
    short_url
    VARCHAR
    PRIMARY
    KEY,
    long_url
    VARCHAR
    NOT
    NULL,
    created_at
    TIMESTAMP
    NOT
    NULL
);