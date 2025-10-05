CREATE TABLE IF NOT EXISTS assigned_assets (
    asset_id VARCHAR(255) NOT NULL,
    mission_id INT NOT NULL REFERENCES missions(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (asset_id, mission_id)
);