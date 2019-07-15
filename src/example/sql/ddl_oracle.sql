-- (C) 2016 Masato Kokubo

-- for Oracle

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
	id          NUMBER  (  9)      NOT NULL,
	firstName   VARCHAR2( 20 CHAR)     NULL,
	lastName    VARCHAR2( 20 CHAR)     NULL,
	birthday    DATE                   NULL,
	birthday2   NUMBER  ( 18)          NULL,

	updateCount NUMBER  (  9)      NOT NULL,
	createdTime TIMESTAMP(9) WITH TIME ZONE NOT NULL,
	updatedTime TIMESTAMP(9) WITH TIME ZONE NOT NULL,

	PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
	contactId    NUMBER  (  9)      NOT NULL,
	featureIndex NUMBER  (  4)      NOT NULL,
	label        VARCHAR2( 10 CHAR) NOT NULL,
	content      VARCHAR2( 20 BYTE) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- E-Mail
DROP TABLE Email;
CREATE TABLE Email (
	contactId    NUMBER  (  9)      NOT NULL,
	featureIndex NUMBER  (  4)      NOT NULL,
	label        VARCHAR2( 10 CHAR) NOT NULL,
	content      VARCHAR2(256 BYTE) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- URL
DROP TABLE Url;
CREATE TABLE Url (
	contactId    NUMBER  (  9)      NOT NULL,
	featureIndex NUMBER  (  4)      NOT NULL,
	label        VARCHAR2( 10 CHAR) NOT NULL,
	content      VARCHAR2(256 BYTE) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- Address
DROP TABLE Address;
CREATE TABLE Address (
	contactId    NUMBER  (  9)      NOT NULL,
	featureIndex NUMBER  (  4)      NOT NULL,
	label        VARCHAR2( 10 CHAR) NOT NULL,
	postCode     VARCHAR2( 10 BYTE)     NULL,
	content      VARCHAR2( 40 CHAR) NOT NULL,
	content1     VARCHAR2( 40 CHAR)     NULL,
	content2     VARCHAR2( 40 CHAR)     NULL,
	content3     VARCHAR2( 40 CHAR)     NULL,

	PRIMARY KEY(contactId, featureIndex)
);
