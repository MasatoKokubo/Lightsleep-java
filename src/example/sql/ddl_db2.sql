-- (C) 2016 Masato Kokubo

-- for DB2

-- Contact
DROP TABLE IF EXISTS Contact;
CREATE TABLE Contact (
	id          INTEGER         NOT NULL,
	firstName   VARGRAPHIC( 20)     NULL,
	lastName    VARGRAPHIC( 20)     NULL,
	birthday    DATE                NULL,
	birthday2   BIGINT              NULL,

	updateCount INTEGER         NOT NULL,
	createdTime TIMESTAMP       NOT NULL,
	updatedTime TIMESTAMP       NOT NULL,

	PRIMARY KEY(id)
);

-- Phone
DROP TABLE IF EXISTS Phone;
CREATE TABLE Phone (
	contactId   INTEGER         NOT NULL,
	childIndex  SMALLINT        NOT NULL,
	label       VARGRAPHIC( 10) NOT NULL,
	content     VARCHAR   ( 20) NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- E-Mail
DROP TABLE IF EXISTS Email;
CREATE TABLE Email (
	contactId   INTEGER         NOT NULL,
	childIndex  SMALLINT        NOT NULL,
	label       VARGRAPHIC( 10) NOT NULL,
	content     VARCHAR   (256) NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- URL
DROP TABLE IF EXISTS Url;
CREATE TABLE Url (
	contactId   INTEGER         NOT NULL,
	childIndex  SMALLINT        NOT NULL,
	label       VARGRAPHIC( 10) NOT NULL,
	content     VARCHAR   (256) NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- Address
DROP TABLE IF EXISTS Address;
CREATE TABLE Address (
	contactId   INTEGER         NOT NULL,
	childIndex  SMALLINT        NOT NULL,
	label       VARGRAPHIC( 10) NOT NULL,
	postCode    VARCHAR   ( 10)     NULL,
	content0    VARGRAPHIC( 40) NOT NULL,
	content1    VARGRAPHIC( 40)     NULL,
	content2    VARGRAPHIC( 40)     NULL,
	content3    VARGRAPHIC( 40)     NULL,

	PRIMARY KEY(contactId, childIndex)
);
