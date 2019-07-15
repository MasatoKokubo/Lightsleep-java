-- (C) 2016 Masato Kokubo

-- for DB2

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
	id          INTEGER         NOT NULL,
	firstName   VARGRAPHIC( 20)     NULL,
	lastName    VARGRAPHIC( 20)     NULL,
	birthday    DATE                NULL,
	birthday2   BIGINT              NULL,

	updateCount INTEGER         NOT NULL,
	createdTime TIMESTAMP(9)    NOT NULL,
	updatedTime TIMESTAMP(9)    NOT NULL,

	PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
	contactId    INTEGER         NOT NULL,
	featureIndex SMALLINT        NOT NULL,
	label        VARGRAPHIC( 10) NOT NULL,
	content      VARCHAR   ( 20) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- E-Mail
DROP TABLE Email;
CREATE TABLE Email (
	contactId    INTEGER         NOT NULL,
	featureIndex SMALLINT        NOT NULL,
	label        VARGRAPHIC( 10) NOT NULL,
	content      VARCHAR   (256) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- URL
DROP TABLE Url;
CREATE TABLE Url (
	contactId    INTEGER         NOT NULL,
	featureIndex SMALLINT        NOT NULL,
	label        VARGRAPHIC( 10) NOT NULL,
	content      VARCHAR   (256) NOT NULL,

	PRIMARY KEY(contactId, featureIndex)
);

-- Address
DROP TABLE Address;
CREATE TABLE Address (
	contactId    INTEGER         NOT NULL,
	featureIndex SMALLINT        NOT NULL,
	label        VARGRAPHIC( 10) NOT NULL,
	postCode     VARCHAR   ( 10)     NULL,
	content      VARGRAPHIC( 40) NOT NULL,
	content1     VARGRAPHIC( 40)     NULL,
	content2     VARGRAPHIC( 40)     NULL,
	content3     VARGRAPHIC( 40)     NULL,

	PRIMARY KEY(contactId, featureIndex)
);
