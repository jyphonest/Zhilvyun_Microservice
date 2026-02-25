-- 创建travel_plan_detail表
CREATE TABLE IF NOT EXISTS travel_plan_detail (
    detail_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL,
    day BIGINT NOT NULL,
    travel_description TEXT,
    travel_route JSON,
    travel_images JSON,
    transportation_description TEXT,
    dining_description TEXT,
    dining_images JSON,
    accommodation_description TEXT,
    accommodation_images JSON,
    FOREIGN KEY (plan_id) REFERENCES travel_plan_message(plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci; 