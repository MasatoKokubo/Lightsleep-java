-- (C) 2016 Masato Kokubo

-- for SQLite

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
    id          INTEGER NOT NULL,
    firstName   TEXT    NOT NULL,
    lastName    TEXT    NOT NULL,
    birthday    TEXT        NULL,
	birthday2   INTEGER     NULL,

    updateCount INTEGER NOT NULL,
    createdTime TEXT    NOT NULL,
    updatedTime TEXT    NOT NULL,

    PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
    contactId    INTEGER NOT NULL,
    featureIndex INTEGER NOT NULL,
    label        TEXT    NOT NULL,
    content      TEXT    NOT NULL,

    PRIMARY KEY(contactId, featureIndex)
);

-- E-Mail
DROP TABLE IF EXISTS Email;
CREATE TABLE Email (
    contactId    INTEGER NOT NULL,
    featureIndex INTEGER NOT NULL,
    label        TEXT    NOT NULL,
    content      TEXT    NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- URL
DROP TABLE IF EXISTS Url;
CREATE TABLE Url (
    contactId    INTEGER NOT NULL,
    featureIndex INTEGER NOT NULL,
    label        TEXT    NOT NULL,
    content      TEXT    NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- Address
DROP TABLE IF EXISTS Address;
CREATE TABLE Address (
	contactId    INTEGER NOT NULL,
	featureIndex INTEGER NOT NULL,
	label        TEXT    NOT NULL,
	postCode     TEXT        NULL,
	content      TEXT    NOT NULL,
	content1     TEXT        NULL,
	content2     TEXT        NULL,
	content3     TEXT        NULL,

	PRIMARY KEY(contactId, featureIndex)
);
