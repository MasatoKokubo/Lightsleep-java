-- (C) 2016 Masato Kokubo

-- for SQL Server

-- Numbering
DROP TABLE Numbering;
CREATE TABLE Numbering (
	tableName   VARCHAR(32)  NOT NULL,
	nextId      INT          NOT NULL,

	PRIMARY KEY(tableName)
);

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
	id          INT          NOT NULL,
	familyName  VARCHAR(40)  NOT NULL, -- 2 times
	givenName   VARCHAR(40)  NOT NULL, -- 2 times
	birthday    DATE             NULL,
	addressId   INT              NULL,

	updateCount INT          NOT NULL,
	created     DATETIME2(3) NOT NULL,
	updated     DATETIME2(3) NOT NULL,

	PRIMARY KEY(id)
);

-- Address
DROP TABLE Address;
CREATE TABLE Address (
	id          INT          NOT NULL,
	postCode    VARCHAR(10)      NULL,
	address1    VARCHAR(40)      NULL, -- 2 times
	address2    VARCHAR(40)      NULL, -- 2 times
	address3    VARCHAR(40)      NULL, -- 2 times
	address4    VARCHAR(40)      NULL, -- 2 times

	updateCount INT          NOT NULL,
	created     DATETIME2(3) NOT NULL,
	updated     DATETIME2(3) NOT NULL,

	PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
	id          INT          NOT NULL,
	contactId   INT          NOT NULL,
	phoneNumber VARCHAR(12)  NOT NULL,

	updateCount INT          NOT NULL,
	created     DATETIME2(3) NOT NULL,
	updated     DATETIME2(3) NOT NULL,

	PRIMARY KEY(id)
);

-- Product
DROP TABLE Product;
CREATE TABLE Product (
	id          INT          NOT NULL,
	productName VARCHAR(40)  NOT NULL, -- 2 times
	price       INT          NOT NULL,
	productSize CHAR   ( 2)      NULL,
	color       VARCHAR(20)      NULL,

	updateCount INT          NOT NULL,
	created     DATETIME2(3) NOT NULL,
	updated     DATETIME2(3) NOT NULL,

	PRIMARY KEY(id)
);

-- Sale
DROP TABLE Sale;
CREATE TABLE Sale (
	id          INT          NOT NULL,
	contactId   INT          NOT NULL,
	saleDate    DATE         NOT NULL,
	taxRate     SMALLINT     NOT NULL,

	updateCount INT          NOT NULL,
	created     DATETIME2(3) NOT NULL,
	updated     DATETIME2(3) NOT NULL,

	PRIMARY KEY(id)
);

-- SaleItem
DROP TABLE SaleItem;
CREATE TABLE SaleItem (
	saleId      INT          NOT NULL,
	itemIndex   SMALLINT     NOT NULL,
	productId   INT          NOT NULL,
	quantity    SMALLINT     NOT NULL,

	PRIMARY KEY(saleId, itemIndex)
);



-- Various
DROP TABLE Various;
CREATE TABLE Various (
	id               INT          NOT NULL,

	booleanPValue    BIT       NOT NULL DEFAULT 0,
	char1PValue      CHAR(2)   NOT NULL DEFAULT ' ', -- 2 times
	tinyIntPValue    TINYINT   NOT NULL DEFAULT 0,
	smallIntPValue   SMALLINT  NOT NULL DEFAULT 0,
	intPValue        INT       NOT NULL DEFAULT 0,
	bigIntPValue     BIGINT    NOT NULL DEFAULT 0,
	floatPValue      FLOAT(24) NOT NULL DEFAULT 0,
	doublePValue     FLOAT(53) NOT NULL DEFAULT 0,

	booleanValue     BIT          ,
	char1Value       CHAR(2)      , -- 2 times
	tinyIntValue     TINYINT      ,
	smallIntValue    SMALLINT     ,
	intValue         INT          ,
	bigIntValue      BIGINT       ,
	floatValue       FLOAT(24)    ,
	doubleValue      FLOAT(53)    ,

	decimalValue     DECIMAL(12,2),

	dateValue        DATE         ,
	timeValue        TIME         ,
	timeTZValue      TIME         , -- instead of TIME WITH TIME ZONE type
	dateTimeValue    DATETIME2(3) ,
	timestampValue   DATETIME2(3) ,
	timestampTZValue DATETIME2(3) , -- instead of DATETIME2(3) WITH TIME ZONE type

	charValue        CHAR     (40), -- 2 times
	varCharValue     VARCHAR  (80), -- 2 times

	longDate         BIGINT       , -- since 1.8.0
	longTime         BIGINT       , -- since 1.8.0
	longTimestamp    BIGINT       , -- since 1.8.0

	binaryValue      BINARY   (20),
	varBinaryValue   VARBINARY(40),

	textValue        TEXT         ,
	blobValue        IMAGE        ,
	jsonValue        TEXT         ,

	PRIMARY KEY(id)
);
