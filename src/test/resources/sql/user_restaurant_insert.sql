INSERT INTO users (id, username, password, name, phone, role, created_at)
VALUES (1, 'tester1', '{noop}pw1234', '홍길동', '010-1000-1000', 'CUSTOMER', CURRENT_TIMESTAMP);
INSERT INTO users (id, username, password, name, phone, role, created_at)
VALUES (2, 'tester2', '{noop}pw4321', '이순신', '010-1234-5678', 'OWNER', CURRENT_TIMESTAMP);

INSERT INTO p_restaurant (restaurant_id, name, user_id, phone, address, longitude, latitude, description, status,
                          deleted)
VALUES ('11111111-1111-1111-1111-111111111111', '맛있는집', 2, '02-123-4567', '서울시 중구 어딘가 1-1', 126.9780, 37.5665,
        '김치찌개 전문', 'OPEN', FALSE);

