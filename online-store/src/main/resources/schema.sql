CREATE TABLE IF NOT EXISTS item (
                                    id BIGSERIAL PRIMARY KEY,
                                    title VARCHAR(32) UNIQUE,
                                    description VARCHAR(64),
                                    img_path VARCHAR(64),
                                    count INTEGER,
                                    price DECIMAL(19,2)
);

CREATE TABLE IF NOT EXISTS orders (
                                      id BIGSERIAL PRIMARY KEY,
                                      is_paid BOOLEAN NOT NULL DEFAULT false,
                                      uuid UUID NOT NULL DEFAULT gen_random_uuid()
);

CREATE TABLE IF NOT EXISTS order_item (
                                          id BIGSERIAL PRIMARY KEY,
                                          order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                                          item_id BIGINT NOT NULL REFERENCES item(id),
                                          price DECIMAL(19, 2) NOT NULL,
                                          count INTEGER NOT NULL,
                                          UNIQUE (order_id, item_id)
);

CREATE INDEX IF NOT EXISTS idx_order_item_order_id ON order_item(order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_item_id ON order_item(item_id);

CREATE TABLE IF NOT EXISTS users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       role VARCHAR(200) NOT NULL DEFAULT 'USER'
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Ownership for orders and per-user carts
ALTER TABLE IF EXISTS orders
    ADD COLUMN IF NOT EXISTS user_id BIGINT REFERENCES users(id);

CREATE TABLE IF NOT EXISTS cart_item (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    item_id BIGINT NOT NULL REFERENCES item(id),
    count INTEGER NOT NULL,
    UNIQUE (user_id, item_id)
);

CREATE INDEX IF NOT EXISTS idx_cart_item_user_id ON cart_item(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_item_item_id ON cart_item(item_id);