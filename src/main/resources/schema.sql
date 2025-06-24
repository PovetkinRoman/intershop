CREATE TABLE IF NOT EXISTS item (
                                    id BIGSERIAL PRIMARY KEY,
                                    title VARCHAR(32) UNIQUE,
                                    description VARCHAR(64),
                                    img_path VARCHAR(64),
                                    count INTEGER,
                                    price DECIMAL(19,2)
);