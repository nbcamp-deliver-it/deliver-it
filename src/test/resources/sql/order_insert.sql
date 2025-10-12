INSERT INTO p_order (order_id, user_id, restaurant_id, ordered_at, created_at, updated_at, order_status, address,
                     total_price, version)
VALUES ('00000000-0000-0000-0000-000000000001', 1, '11111111-1111-1111-1111-111111111111',
        TIMESTAMP '2025-10-01 12:00:00.000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CREATED', '서울시 중구 어딘가 1-1', 40000,
        0);

INSERT INTO p_order (order_id, user_id, restaurant_id, ordered_at, created_at, updated_at, order_status, address,
                     total_price, version)
VALUES ('00000000-0000-0000-0000-000000000002', 1, '11111111-1111-1111-1111-111111111111',
        TIMESTAMP '2025-10-08 12:00:00.000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CREATED', '서울시 중구 어딘가 1-1', 70000,
        0);

INSERT INTO p_order (order_id, user_id, restaurant_id, ordered_at, created_at, updated_at, order_status, address,
                     total_price, version)
VALUES ('00000000-0000-0000-0000-000000000003', 1, '11111111-1111-1111-1111-111111111111',
        TIMESTAMP '2025-10-10 12:00:00.000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'PAYMENT_COMPLETED',
        '서울시 중구 어딘가 1-1', 70000, 0);

INSERT INTO p_order (order_id, user_id, restaurant_id, ordered_at, created_at, updated_at, order_status, address,
                     total_price, version)
VALUES ('00000000-0000-0000-0000-000000000004', 1, '11111111-1111-1111-1111-111111111111',
        TIMESTAMP '2025-10-10 12:00:00.000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'PAYMENT_COMPLETED',
        '서울시 중구 어딘가 1-1', 70000, 0);

INSERT INTO p_order (order_id, user_id, restaurant_id, ordered_at, created_at, updated_at, order_status, address,
                     total_price, version)
VALUES ('00000000-0000-0000-0000-000000000005', 1, '11111111-1111-1111-1111-111111111111',
        TIMESTAMP '2025-10-10 12:00:00.000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CONFIRMED',
        '서울시 중구 어딘가 1-1', 70000, 0);