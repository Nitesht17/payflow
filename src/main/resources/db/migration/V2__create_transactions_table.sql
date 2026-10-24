CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              merchant_id BIGINT NOT NULL REFERENCES merchants(id),
                              amount NUMERIC(12, 2) NOT NULL,
                              currency VARCHAR(3) NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP NOT NULL
);