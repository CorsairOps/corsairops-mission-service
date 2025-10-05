CREATE TABLE IF NOT EXISTS assigned_missions (
    user_id VARCHAR(255) NOT NULL,
    mission_id INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, mission_id)
);