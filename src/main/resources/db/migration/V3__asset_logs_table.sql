CREATE TABLE asset_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    asset_id BIGINT NOT NULL REFERENCES assets(id) ON DELETE CASCADE,
    employee_id BIGINT NOT NULL REFERENCES users(id),             
    patron_id BIGINT REFERENCES users(id),          
    action VARCHAR(50) NOT NULL,                              
    notes TEXT,                                               
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);