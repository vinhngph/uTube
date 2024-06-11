USE uTube;

CREATE TABLE Role
(
    role_code INT PRIMARY KEY,
    role_name VARCHAR(50)  NOT NULL,
    role_desc VARCHAR(100) NOT NULL
);

INSERT INTO Role (role_code, role_name, role_desc)
VALUES (1, 'ADMIN', 'Admin Role'),
       (2, 'STAFF', 'Staff Role'),
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

INSERT INTO User (user_username, user_email, user_password, user_role)
VALUES ('admin', 'admin@gmail.com', 'admin', 1),
       ('staff', 'staff@gmail.com', 'staff', 2),
       ('user', 'user@gmail.com', 'user', 3);

INSERT INTO User_Information (user_id, user_fullname, user_dob)
VALUES (1000, 'Admin', '2024-06-01'),
       (1001, 'Staff', '2024-06-01'),
       (1002, 'User', '2024-06-01');

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

SELECT v.video_id,
       v.video_title,
       v.video_description,
       v.video_date,
       v.video_status,
       v.video_id                                                                               AS video_thumbnail,
       CAST((SELECT COUNT(*) FROM Video_Like vl WHERE vl.video_id = v.video_id) AS UNSIGNED)    AS video_like,
       CAST((SELECT COUNT(*) FROM Video_Dislike vd WHERE vd.video_id = v.video_id) AS UNSIGNED) AS video_dislike,
       CAST(vv.video_view AS UNSIGNED)                                                          AS video_views,
       ui.user_fullname                                                                         AS video_channel_name,
       u.user_id                                                                                AS video_channel_id
FROM Video v
         JOIN
     Video_View vv ON v.video_id = vv.video_id
         JOIN
     Upload u ON v.video_id = u.video_id
         JOIN
     User_Information ui ON u.user_id = ui.user_id
WHERE video_title LIKE ?
ORDER BY vv.video_view DESC;