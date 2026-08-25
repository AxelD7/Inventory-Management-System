CREATE TABLE asset_circulations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    asset_id BIGINT NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    borrower_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    checked_out_by BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    checked_in_by BIGINT REFERENCES users(id) ON DELETE CASCADE,
    due_date TIMESTAMPTZ NOT NULL,
    returned_at TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL,
    is_damaged BOOLEAN,
    notes TEXT
);