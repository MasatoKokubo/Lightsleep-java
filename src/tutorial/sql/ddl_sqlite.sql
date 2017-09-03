-- for SQLite
CREATE TABLE Contact (
    id        INTEGER     NOT NULL,
    firstName VARCHAR(20) NOT NULL,
    lastName  VARCHAR(20) NOT NULL,
    birthday  TEXT            NULL,

    PRIMARY KEY(id)
);
