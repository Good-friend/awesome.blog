CREATE TABLE user
(
  username VARCHAR(256) PRIMARY KEY,
  password VARCHAR(256),
  email VARCHAR(256),
  age INT,
  enabled  TINYINT(1)
);

CREATE TABLE authority
(
  id        INT AUTO_INCREMENT PRIMARY KEY,
  username  VARCHAR(256),
  authority VARCHAR(256),
  CONSTRAINT fk_authority_user FOREIGN KEY (username) REFERENCES user (username)
);

CREATE UNIQUE INDEX ix_auth_username
  ON authority (username, authority);

