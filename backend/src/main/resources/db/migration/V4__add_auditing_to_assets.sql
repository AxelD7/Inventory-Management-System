ALTER TABLE assets 
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS created_by_id BIGINT,
    ADD COLUMN IF NOT EXISTS updated_by_id BIGINT;

ALTER TABLE assets 
    ADD CONSTRAINT fk_assets_created_by 
    FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE assets 
    ADD CONSTRAINT fk_assets_updated_by 
    FOREIGN KEY (updated_by_id) REFERENCES users(id) ON DELETE SET NULL;