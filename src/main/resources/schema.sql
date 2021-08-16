DROP TABLE IF EXISTS Enrollment;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Course;

CREATE TABLE User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL
);


CREATE TABLE Course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(500)
);


CREATE TABLE Enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enrollment_date datetime NOT NULL,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    foreign key (user_id) REFERENCES User(id),
    foreign key (course_id) REFERENCES Course(id)
);
