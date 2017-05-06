-- (C) 2016 Masato Kokubo

-- for Oracle

-- Numbering
DROP TABLE Numbering;
CREATE TABLE Numbering (
	tableName   VARCHAR2(32 BYTE) NOT NULL,
	nextId      NUMBER ( 9)       NOT NULL,

	PRIMARY KEY(tableName)
);

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
	id          NUMBER  ( 9)      NOT NULL,
	familyName  VARCHAR2(20 CHAR) NOT NULL,
	givenName   VARCHAR2(20 CHAR) NOT NULL,
	birthday    DATE                  NULL,
	addressId   NUMBER  ( 9)          NULL,

	updateCount NUMBER  ( 9)      NOT NULL,
	created     TIMESTAMP         NOT NULL,
	updated     TIMESTAMP         NOT NULL,

	PRIMARY KEY(id)
);

-- Address
DROP TABLE Address;
CREATE TABLE Address (
	id          NUMBER  ( 9)      NOT NULL,
	postCode    VARCHAR2(10 BYTE)     NULL,
	address1    VARCHAR2(20 CHAR)     NULL,
	address2    VARCHAR2(20 CHAR)     NULL,
	address3    VARCHAR2(20 CHAR)     NULL,
	address4    VARCHAR2(20 CHAR)     NULL,

	updateCount NUMBER ( 9)       NOT NULL,
	created     TIMESTAMP         NOT NULL,
	updated     TIMESTAMP         NOT NULL,

	PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
	id          NUMBER  ( 9)      NOT NULL,
	contactId   NUMBER  ( 9)      NOT NULL,
	phoneNumber VARCHAR2(12 BYTE) NOT NULL,

	updateCount NUMBER  ( 9)      NOT NULL,
	created     TIMESTAMP         NOT NULL,
	updated     TIMESTAMP         NOT NULL,

	PRIMARY KEY(id)
);

-- Product
DROP TABLE Product;
CREATE TABLE Product (
	id          NUMBER  ( 9)      NOT NULL,
	productName VARCHAR2(20 CHAR) NOT NULL,
	price       NUMBER  ( 9)      NOT NULL,
	productSize CHAR    ( 2 BYTE)     NULL,
	color       VARCHAR2(20 CHAR)     NULL,

	updateCount NUMBER ( 9)       NOT NULL,
	created     TIMESTAMP         NOT NULL,
	updated     TIMESTAMP         NOT NULL,

	PRIMARY KEY(id)
);

-- Sale
DROP TABLE Sale;
CREATE TABLE Sale (
	id          NUMBER  ( 9)      NOT NULL,
	contactId   NUMBER  ( 9)      NOT NULL,
	saleDate    DATE              NOT NULL,
	taxRate     NUMBER  ( 4)      NOT NULL,

	updateCount NUMBER  ( 9)      NOT NULL,
	created     TIMESTAMP         NOT NULL,
	updated     TIMESTAMP         NOT NULL,

	PRIMARY KEY(id)
);

-- SaleItem
DROP TABLE SaleItem;
CREATE TABLE SaleItem (
	saleId      NUMBER  ( 9)      NOT NULL,
	itemIndex   NUMBER  ( 4)      NOT NULL,
	productId   NUMBER  ( 9)      NOT NULL,
	quantity    NUMBER  ( 4)      NOT NULL,

	PRIMARY KEY(saleId, itemIndex)
);



-- Various
DROP TABLE Various;
CREATE TABLE Various (
	id               NUMBER  ( 9)      NOT NULL,

	booleanPValue    NUMBER( 1)      DEFAULT 0   NOT NULL ,
	char1PValue      CHAR  ( 1 CHAR) DEFAULT ' ' NOT NULL ,
	tinyIntPValue    NUMBER( 3)      DEFAULT 0   NOT NULL ,
	smallIntPValue   NUMBER( 5)      DEFAULT 0   NOT NULL ,
	intPValue        NUMBER(10)      DEFAULT 0   NOT NULL ,
	bigIntPValue     NUMBER(19)      DEFAULT 0   NOT NULL ,
	floatPValue      BINARY_FLOAT    DEFAULT 0   NOT NULL ,
	doublePValue     BINARY_DOUBLE   DEFAULT 0   NOT NULL ,

	booleanValue     NUMBER( 1)     ,
	char1Value       CHAR  ( 1 CHAR),
	tinyIntValue     NUMBER( 3)     ,
	smallIntValue    NUMBER( 5)     ,
	intValue         NUMBER(10)     ,
	bigIntValue      NUMBER(19)     ,
	floatValue       BINARY_FLOAT   ,
	doubleValue      BINARY_DOUBLE  ,

	decimalValue     NUMBER(12,2)   ,

	dateValue        DATE           ,
	timeValue        DATE           , -- instead of TIME type
	timeTZValue      DATE           , -- instead of TIME WITH TIME ZONE type
	dateTimeValue    TIMESTAMP      , -- instead of DATETIME type
	timestampValue   TIMESTAMP      ,
--	timestampTZValue TIMESTAMP WITH TIME ZONE,
	timestampTZValue TIMESTAMP      ,

	longDate         NUMBER(19)     , -- since 1.8.0
	longTime         NUMBER(19)     , -- since 1.8.0
	longTimestamp    NUMBER(19)     , -- since 1.8.0

	charValue        CHAR    (20)   ,
	varCharValue     VARCHAR2(40)   ,

	binaryValue      BLOB           , -- instead of BINARY   (20) type
	varBinaryValue   BLOB           , -- instead of VARBINARY(40) type

	textValue        CLOB           ,
	blobValue        BLOB           ,
	jsonValue        CLOB           ,

	PRIMARY KEY(id)
);
