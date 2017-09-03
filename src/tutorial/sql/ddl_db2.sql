-- for DB2
CREATE TABLE Contact (
    id        INTEGER        NOT NULL,
    firstName VARGRAPHIC(20) NOT NULL,
    lastName  VARGRAPHIC(20) NOT NULL,
    birthday  DATE               NULL,

    PRIMARY KEY(id)
);
