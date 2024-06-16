USE uTube;

CREATE TABLE Role
(
    role_code INT PRIMARY KEY,
    role_name VARCHAR(50)  NOT NULL,
    role_desc VARCHAR(100) NOT NULL
);

INSERT INTO Role (role_code, role_name, role_desc)
VALUES (1, 'ROOT', 'Root Role'),
       (2, 'ADMIN', 'Admin Role'),
       (3, 'USER', 'User Role');

CREATE TABLE User
(
    user_id       INT PRIMARY KEY AUTO_INCREMENT,
    user_username VARCHAR(50) NOT NULL,
    user_email    VARCHAR(50) NOT NULL,
    user_password VARCHAR(50) NOT NULL,
    user_role     INT         NOT NULL,
    FOREIGN KEY (user_role) REFERENCES Role (role_code)
) AUTO_INCREMENT = 1000;

CREATE TABLE User_Information
(
    user_id       INT PRIMARY KEY,
    user_fullname VARCHAR(50) NOT NULL,
    user_dob      DATE        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User (user_id)
);

CREATE TABLE Session
(
    session_id     INT PRIMARY KEY AUTO_INCREMENT,
    session_user   INT          NOT NULL,
    session_time   TIMESTAMP    NOT NULL,
    session_device VARCHAR(255) NOT NULL,
    FOREIGN KEY (session_user) REFERENCES User (user_id)
) AUTO_INCREMENT = 0;


CREATE TABLE Video
(
    video_id          VARCHAR(255) PRIMARY KEY,
    video_title       VARCHAR(255) NOT NULL,
    video_description VARCHAR(255) NOT NULL,
    video_date        TIMESTAMP    NOT NULL,
    video_status      BOOLEAN      NOT NULL
);

CREATE TABLE Video_Like
(
    user_id  INT          NOT NULL,
    video_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, video_id),
    FOREIGN KEY (user_id) REFERENCES User (user_id),
    FOREIGN KEY (video_id) REFERENCES Video (video_id)
);

CREATE TABLE Video_Dislike
(
    user_id  INT          NOT NULL,
    video_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, video_id),
    FOREIGN KEY (user_id) REFERENCES User (user_id),
    FOREIGN KEY (video_id) REFERENCES Video (video_id)
);

CREATE TABLE Video_View
(
    video_id   VARCHAR(255) PRIMARY KEY,
    video_view LONG NOT NULL,
    FOREIGN KEY (video_id) REFERENCES Video (video_id)
);

CREATE TABLE Upload
(
    video_id VARCHAR(255) PRIMARY KEY,
    user_id  INT NOT NULL,
    FOREIGN KEY (video_id) REFERENCES Video (video_id),
    FOREIGN KEY (user_id) REFERENCES User (user_id)
);

CREATE TABLE User_History
(
    user_id    INT          NOT NULL,
    video_id   VARCHAR(255) NOT NULL,
    track_date TIMESTAMP    NOT NULL,
    track_time VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, video_id),
    FOREIGN KEY (user_id) REFERENCES User (user_id),
    FOREIGN KEY (video_id) REFERENCES Video (video_id)
);

CREATE TABLE Password_OTP
(
    user_id INT NOT NULL,
    otp     INT NOT NULL,
    PRIMARY KEY (user_id, otp),
    FOREIGN KEY (user_id) REFERENCES User (user_id)
);