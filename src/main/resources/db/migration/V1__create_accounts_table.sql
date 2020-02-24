CREATE TABLE IF NOT EXISTS accounts (
                          id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                          balance DECIMAL(19,2) NOT NULL DEFAULT 0
);

INSERT INTO accounts (balance) VALUES (1000);
INSERT INTO accounts (balance) VALUES (2000);