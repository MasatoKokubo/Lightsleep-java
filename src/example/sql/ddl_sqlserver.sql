-- (C) 2016 Masato Kokubo

-- for SQL Server

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
	id          INT               NOT NULL,
	firstName   VARCHAR( 20)          NULL,
	lastName    VARCHAR( 20)          NULL,
	birthday    DATE                  NULL,
	birthday2   BIGINT                NULL,

	updateCount INT               NOT NULL,
	createdTime DATETIMEOFFSET(7) NOT NULL,
	updatedTime DATETIMEOFFSET(7) NOT NULL,

	PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
	contactId    INT          NOT NULL,
	featureIndex SMALLINT     NOT NULL,
	label        VARCHAR( 20) NOT NULL, -- double length
	content      VARCHAR( 20) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- E-Mail
DROP TABLE Email;
CREATE TABLE Email (
	contactId    INT          NOT NULL,
	featureIndex SMALLINT     NOT NULL,
	label        VARCHAR( 20) NOT NULL, -- double length
	content      VARCHAR(256) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- URL
DROP TABLE Url;
CREATE TABLE Url (
	contactId    INT          NOT NULL,
	featureIndex SMALLINT     NOT NULL,
	label        VARCHAR( 20) NOT NULL, -- double length
	content      VARCHAR(256) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- Address
DROP TABLE Address;
CREATE TABLE Address (
	contactId   INT          NOT NULL,
	featureIndex SMALLINT     NOT NULL,
	label       VARCHAR( 20) NOT NULL, -- double length
	postCode    VARCHAR( 10)     NULL,
	content     VARCHAR( 40) NOT NULL,
	content1    VARCHAR( 40)     NULL,
	content2    VARCHAR( 40)     NULL,
	content3    VARCHAR( 40)     NULL,

	PRIMARY KEY(contactId, featureIndex)
);
