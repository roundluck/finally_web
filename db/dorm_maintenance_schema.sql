-- MySQL schema for Dormitory Maintenance Request Management System
CREATE DATABASE IF NOT EXISTS dorm_maintenance;
USE dorm_maintenance;

DROP TABLE IF EXISTS request_timeline;
DROP TABLE IF EXISTS maintenance_requests;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    phone VARCHAR(40),
    dorm VARCHAR(120),
    role VARCHAR(20) NOT NULL,
    active TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE maintenance_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    dorm VARCHAR(160) NOT NULL,
    room VARCHAR(40),
    category VARCHAR(80),
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    preferred_entry_time DATETIME NULL,
    completion_target DATETIME NULL,
    asset_tag VARCHAR(80),
    photo_url VARCHAR(255),
    student_id BIGINT NOT NULL,
    manager_id BIGINT NULL,
    technician_id BIGINT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES users(id),
    CONSTRAINT fk_manager FOREIGN KEY (manager_id) REFERENCES users(id),
    CONSTRAINT fk_technician FOREIGN KEY (technician_id) REFERENCES users(id)
);

CREATE TABLE request_timeline (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    created_by BIGINT NULL,
    status VARCHAR(20) NOT NULL,
    note TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES maintenance_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_actor FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Seed data (BCrypt hashes generated from Password!23)
INSERT INTO users (username, password, full_name, email, phone, dorm, role) VALUES
('dorm_manager', '$2a$10$J9pkmDT6vKrrO5gkP7Fpdeb18m0tHbdD.N14Q6gttxyPjdvVS.wna', 'Dorm Manager', 'manager@dorm.edu', '+65-1111-1111', 'North Hill', 'MANAGER'),
('tech_mario', '$2a$10$J9pkmDT6vKrrO5gkP7Fpdeb18m0tHbdD.N14Q6gttxyPjdvVS.wna', 'Mario Tan', 'mario.tan@dorm.edu', '+65-2222-2222', 'Nanyang Crescent', 'TECHNICIAN'),
('alicelee', '$2a$10$J9pkmDT6vKrrO5gkP7Fpdeb18m0tHbdD.N14Q6gttxyPjdvVS.wna', 'Alice Lee', 'alice.lee@u.edu', '+65-3333-3333', 'North Hill', 'STUDENT'),
('briantan', '$2a$10$J9pkmDT6vKrrO5gkP7Fpdeb18m0tHbdD.N14Q6gttxyPjdvVS.wna', 'Brian Tan', 'brian.tan@u.edu', '+65-4444-4444', 'North Hill', 'STUDENT');

INSERT INTO maintenance_requests (title, description, dorm, room, category, priority, status, preferred_entry_time, student_id, manager_id, technician_id)
VALUES
('Leaky bathroom tap', 'Water dripping for two days causing puddles', 'North Hill Block 5', '05-123', 'Plumbing', 'MEDIUM', 'ASSIGNED', DATE_ADD(NOW(), INTERVAL 1 DAY), 3, 1, 2),
('Corridor light flickering', 'Level 12 corridor light near study lounge flickers', 'North Hill Block 8', '12-Common', 'Electrical', 'HIGH', 'UNDER_REVIEW', NULL, 4, 1, NULL);
