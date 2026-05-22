-- 1. Sequence (generatorlar)
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS products_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS orders_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS order_items_seq START WITH 1 INCREMENT BY 50;

-- 2. Mustaqil jadvallar
CREATE TABLE users (
                       user_id    BIGINT NOT NULL DEFAULT nextval('users_seq'), -- Sequence bog'landi
                       full_name  VARCHAR(255),
                       username   VARCHAR(255) NOT NULL,
                       password   VARCHAR(255) NOT NULL,
                       role       VARCHAR(255),
                       created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT pk_users PRIMARY KEY (user_id),
                       CONSTRAINT uq_users_username UNIQUE (username)
);

CREATE TABLE products (
                          id         BIGINT NOT NULL DEFAULT nextval('products_seq'), -- Sequence bog'landi
                          name       VARCHAR(255),
                          price      NUMERIC(38, 2),
                          stock      INTEGER,
                          image_name VARCHAR(255) NOT NULL,
                          category   VARCHAR(255),
                          is_active  BOOLEAN,
                          created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT pk_products PRIMARY KEY (id)
);

-- 3. Bola jadvallar
CREATE TABLE orders (
                        id             BIGINT NOT NULL DEFAULT nextval('orders_seq'), -- Sequence bog'landi
                        customer_name  VARCHAR(255),
                        customer_email VARCHAR(255),
                        order_date     TIMESTAMP(6),
                        order_status   VARCHAR(255),
                        total_amount   NUMERIC(38, 2),
                        user_id        BIGINT,
                        CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE order_items (
                             id          BIGINT NOT NULL DEFAULT nextval('order_items_seq'), -- Sequence bog'landi
                             order_id    BIGINT NOT NULL,
                             product_id  BIGINT NOT NULL,
                             quantity    INTEGER,
                             unit_price  NUMERIC(38, 2),
                             total_price NUMERIC(38, 2),
                             CONSTRAINT pk_order_items PRIMARY KEY (id)
);

-- 4. Foreign Keys
ALTER TABLE orders ADD CONSTRAINT fk_orders_users FOREIGN KEY (user_id) REFERENCES users (user_id);
ALTER TABLE order_items ADD CONSTRAINT fk_order_items_orders FOREIGN KEY (order_id) REFERENCES orders (id);
ALTER TABLE order_items ADD CONSTRAINT fk_order_items_products FOREIGN KEY (product_id) REFERENCES products (id);