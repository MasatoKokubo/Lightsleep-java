-- for SQLServer
CREATE TABLE Contact (
    id        INT         NOT NULL,
    firstName VARCHAR(20) NOT NULL,
    lastName  VARCHAR(20) NOT NULL,
    birthday  DATE            NULL,

    PRIMARY KEY(id)
);
