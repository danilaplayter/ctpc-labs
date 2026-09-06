CREATE TABLE departments (
    department_id CHAR(3) PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    location TEXT NOT NULL
);

INSERT INTO departments (department_id, department_name, location) VALUES
('A01', 'Инженеры', 'пр-т Независимости, 10, Минск, Беларусь'),
('A07', 'Тестировщики', 'ул. Ленина, 25, Гродно, Беларусь'),
('B04', 'Аналитики', 'ул. Московская, 17, Брест, Беларусь');

CREATE TABLE developers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) DEFAULT NULL,
    specialty VARCHAR(50) DEFAULT NULL,
    experience INTEGER DEFAULT NULL,
    department_id CHAR(3),
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
);

CREATE TABLE users (
    user_id INTEGER PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(30) NOT NULL,
    user_role VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES developers(id) ON DELETE CASCADE
);

INSERT INTO developers (name, specialty, experience, department_id) VALUES
('Алексей', 'Java-разработчик', 5, 'A01'),
('Екатерина', 'QA-инженер', 3, 'A07'),
('Игорь', 'Data Analyst', 4, 'B04'),
('Мария', 'DevOps', 2, 'A01');

INSERT INTO users (user_id, username, password, user_role) VALUES
(1, 'admin', 'admin', 'ROLE_ADMIN'),
(2, 'user', 'user', 'ROLE_USER');