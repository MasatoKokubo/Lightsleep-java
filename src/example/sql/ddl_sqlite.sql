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

    updateCount INTEGER NOT NULL DEFAULT 0,
    createdTime TEXT    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedTime TEXT    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
    contactId   INTEGER NOT NULL,
    childIndex  INTEGER NOT NULL,
    label       TEXT    NOT NULL,
    content     TEXT    NOT NULL,

    PRIMARY KEY(contactId, childIndex)
);

-- E-Mail
DROP TABLE IF EXISTS Email;
CREATE TABLE Email (
    contactId   INTEGER NOT NULL,
    childIndex  INTEGER NOT NULL,
    label       TEXT    NOT NULL,
    content     TEXT    NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- URL
DROP TABLE IF EXISTS Url;
CREATE TABLE Url (
    contactId   INTEGER NOT NULL,
    childIndex  INTEGER NOT NULL,
    label       TEXT    NOT NULL,
    content     TEXT    NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- Address
DROP TABLE IF EXISTS Address;
CREATE TABLE Address (
	contactId   INTEGER NOT NULL,
	childIndex  INTEGER NOT NULL,
	label       TEXT    NOT NULL,
	postCode    TEXT        NULL,
	content0    TEXT    NOT NULL,
	content1    TEXT        NULL,
	content2    TEXT        NULL,
	content3    TEXT        NULL,

	PRIMARY KEY(contactId, childIndex)
);
