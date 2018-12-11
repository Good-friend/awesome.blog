delete from authority;
delete from user;

INSERT INTO user (username, password, age, email, enabled) VALUES ('admin', 'password', 26, '13005454311@qq.com', TRUE);
INSERT INTO user (username, password, age, email, enabled) VALUES ('lucifer', 'password', 26, '13005454311@qq.com', TRUE);

INSERT INTO authority (username, authority) VALUES ('admin', 'ADMIN');
INSERT INTO authority (username, authority) VALUES ('admin', 'USER');
INSERT INTO authority (username, authority) VALUES ('lucifer', 'USER');