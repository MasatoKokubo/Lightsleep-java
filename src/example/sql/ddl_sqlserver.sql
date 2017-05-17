-- (C) 2016 Masato Kokubo

-- for SQL Server

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
	id          INT          NOT NULL,
	familyName  VARCHAR(20)      NULL,
	givenName   VARCHAR(20)      NULL,
	birthday    DATE             NULL,

	updateCount INT          NOT NULL,
	createdTime DATETIME2(3) NOT NULL,
	updatedTime DATETIME2(3) NOT NULL,

	PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
	contactId   INT          NOT NULL,
	childIndex  SMALLINT     NOT NULL,
	label       VARCHAR(20)  NOT NULL, -- double length
	content     VARCHAR(20)  NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- E-Mail
DROP TABLE Email;
CREATE TABLE Email (
	contactId   INT          NOT NULL,
	childIndex  SMALLINT     NOT NULL,
	label       VARCHAR( 20) NOT NULL, -- double length
	content     VARCHAR(256) NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- URL
DROP TABLE Url;
CREATE TABLE Url (
	contactId   INT          NOT NULL,
	childIndex  SMALLINT     NOT NULL,
	label       VARCHAR( 20) NOT NULL, -- double length
	content     VARCHAR(256) NOT NULL,

	PRIMARY KEY(contactId, childIndex)
);

-- Address
DROP TABLE Address;
CREATE TABLE Address (
	contactId   INT         NOT NULL,
	childIndex  SMALLINT    NOT NULL,
	label       VARCHAR(20) NOT NULL, -- double length
	postCode    VARCHAR(10)     NULL,
	content0    VARCHAR(40) NOT NULL,
	content1    VARCHAR(40)     NULL,
	content2    VARCHAR(40)     NULL,
	content3    VARCHAR(40)     NULL,

	PRIMARY KEY(contactId, childIndex)
);
