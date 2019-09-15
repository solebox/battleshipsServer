CREATE DATABASE USERS;
USE USERS;
CREATE TABLE Users(username varchar(1000), password varchar(1000), email varchar(1000), score int);
INSERT INTO Users values('abc', '32424', 'abd@gmail.com', 0);
INSERT INTO Users values('shimi', '123456', 'shimi@gmail.com', 0);
INSERT INTO Users values('kk', '123', 'kk@gmail.com', 0);


GRANT ALL PRIVILEGES ON *.* TO 'oren'@'%' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON *.* TO 'oren'@'localhost' IDENTIFIED BY '1234';

