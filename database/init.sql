CREATE DATABASE uTube;
use uTube;

CREATE TABLE Role (
    role_code INT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_desc VARCHAR(100) NOT NULL
);

INSERT INTO Role (role_code, role_name, role_desc) VALUES 
(100, 'ADMIN', 'Admin Role'),
(101, 'STAFF', 'Staff Role'),
(103, 'USER', 'User Role');

CREATE TABLE User (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_username VARCHAR(50) NOT NULL,
    user_email VARCHAR(50) NOT NULL,
    user_password VARCHAR(50) NOT NULL,
    user_role INT NOT NULL,
    FOREIGN KEY (user_role) REFERENCES Role(role_code)
) AUTO_INCREMENT=200;

CREATE TABLE Session (
    session_id INT PRIMARY KEY AUTO_INCREMENT,
    session_user INT NOT NULL,
    session_time TIMESTAMP NOT NULL,
    session_device VARCHAR(255) NOT NULL,
    FOREIGN KEY (session_user) REFERENCES User(user_id)
) AUTO_INCREMENT=300;

CREATE TABLE UserInformation (
    user_id INT PRIMARY KEY,
    user_fullname VARCHAR(50) NOT NULL,
    user_dob DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

INSERT INTO User (user_username, user_email, user_password, user_role) VALUES (?, ?, ?, ?);
INSERT INTO UserInformation (user_id, user_fullname, user_dob) VALUES (?, ?, ?);
INSERT INTO Session (session_user, session_time, session_device) VALUES (?, ?, ?);
