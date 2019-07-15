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

	updateCount INT          NOT NULL,
	createdTime TIMESTAMP(6) WITH TIME ZONE NOT NULL,
	updatedTime TIMESTAMP(6) WITH TIME ZONE NOT NULL,

	PRIMARY KEY(id)
);

-- Phone
DROP TABLE IF EXISTS Phone;
CREATE TABLE Phone (
	contactId    INT          NOT NULL,
	featureIndex SMALLINT     NOT NULL,
	label        VARCHAR( 10) NOT NULL,
	content      VARCHAR( 20) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- E-Mail
DROP TABLE IF EXISTS Email;
CREATE TABLE Email (
	contactId    INT          NOT NULL,
	featureIndex SMALLINT     NOT NULL,
	label        VARCHAR( 10) NOT NULL,
	content      VARCHAR(256) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- URL
DROP TABLE IF EXISTS Url;
CREATE TABLE Url (
	contactId    INT          NOT NULL,
	featureIndex SMALLINT     NOT NULL,
	label        VARCHAR( 10) NOT NULL,
	content      VARCHAR(256) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- Address
DROP TABLE IF EXISTS Address;
CREATE TABLE Address (
	contactId    INT          NOT NULL,
	featureIndex SMALLINT     NOT NULL,
	label        VARCHAR( 10) NOT NULL,
	postCode     VARCHAR( 10)     NULL,
	content      VARCHAR( 40) NOT NULL,
	content1     VARCHAR( 40)     NULL,
	content2     VARCHAR( 40)     NULL,
	content3     VARCHAR( 40)     NULL,

	PRIMARY KEY(contactId, featureIndex)
);
