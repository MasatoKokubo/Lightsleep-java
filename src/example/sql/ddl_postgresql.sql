-- (C) 2016 Masato Kokubo

-- for PostgreSQL

-- Contact
DROP TABLE IF EXISTS Contact;
CREATE TABLE Contact (
	id          INT          NOT NULL,
	lastName    VARCHAR( 20)     NULL,
	firstName   VARCHAR( 20)     NULL,
	birthday    DATE             NULL,
	birthday2   BIGINT           NULL,

	updateCount INT          NOT NULL DEFAULT 0,
	createdTime TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updatedTime TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

	PRIMARY KEY(id)
);

-- Phone
DROP TABLE IF EXISTS Phone;
CREATE TABLE Phone (
	contactId   INT          NOT NULL,
	childIndex  SMALLINT     NOT NULL,
	label       VARCHAR( 10) NOT NULL,
	content     VARCHAR( 20) NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- E-Mail
DROP TABLE IF EXISTS Email;
CREATE TABLE Email (
	contactId   INT          NOT NULL,
	childIndex  SMALLINT     NOT NULL,
	label       VARCHAR( 10) NOT NULL,
	content     VARCHAR(256) NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- URL
DROP TABLE IF EXISTS Url;
CREATE TABLE Url (
	contactId   INT          NOT NULL,
	childIndex  SMALLINT     NOT NULL,
	label       VARCHAR( 10) NOT NULL,
	content     VARCHAR(256) NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- Address
DROP TABLE IF EXISTS Address;
CREATE TABLE Address (
	contactId   INT          NOT NULL,
	childIndex  SMALLINT     NOT NULL,
	label       VARCHAR( 10) NOT NULL,
	postCode    VARCHAR( 10)     NULL,
	content0    VARCHAR( 40) NOT NULL,
	content1    VARCHAR( 40)     NULL,
	content2    VARCHAR( 40)     NULL,
	content3    VARCHAR( 40)     NULL,

	PRIMARY KEY(contactId, childIndex)
);
