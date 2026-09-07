CREATE TABLE users (
    id     SERIAL PRIMARY KEY,
    login  VARCHAR(100) UNIQUE NOT NULL,
    passw  BYTEA NOT NULL
);

CREATE TABLE persons (
    id     SERIAL PRIMARY KEY,
    pname  VARCHAR(150) NOT NULL,
    phone  VARCHAR(50),
    email  VARCHAR(150)
);

CREATE TABLE teachers (
    id      SERIAL PRIMARY KEY,
    tname   VARCHAR(150) NOT NULL,
    subject VARCHAR(150),
    phone   VARCHAR(50)
);