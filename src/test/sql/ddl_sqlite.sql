-- (C) 2016 Masato Kokubo

-- for SQLite

-- Numbering
DROP TABLE IF EXISTS Numbering;
CREATE TABLE Numbering (
    tableName   TEXT    NOT NULL,
    nextId      INTEGER NOT NULL,

    PRIMARY KEY(tableName)
);

-- Contact
DROP TABLE IF EXISTS Contact;
CREATE TABLE Contact (
    id          INTEGER NOT NULL,
    firstName   TEXT    NOT NULL,
    lastName    TEXT    NOT NULL,
    birthday    TEXT        NULL,
    addressId   INTEGER     NULL,

    updateCount INTEGER NOT NULL,
    created     TEXT    NOT NULL,
    updated     TEXT    NOT NULL,

    PRIMARY KEY(id)
);

-- Address
DROP TABLE IF EXISTS Address;
CREATE TABLE Address (
    id          INTEGER NOT NULL,
    postCode    TEXT        NULL,
    address1    TEXT        NULL,
    address2    TEXT        NULL,
    address3    TEXT        NULL,
    address4    TEXT        NULL,

    updateCount INTEGER NOT NULL,
    created     TEXT    NOT NULL,
    updated     TEXT    NOT NULL,

    PRIMARY KEY(id)
);

-- Phone
DROP TABLE IF EXISTS Phone;
CREATE TABLE Phone (
    id          INTEGER NOT NULL,
    contactId   INTEGER NOT NULL,
    phoneNumber TEXT    NOT NULL,

    updateCount INTEGER NOT NULL,
    created     TEXT    NOT NULL,
    updated     TEXT    NOT NULL,

    PRIMARY KEY(id)
);

-- Product
DROP TABLE IF EXISTS Product;
CREATE TABLE Product (
    id          INTEGER NOT NULL,
    productName TEXT    NOT NULL,
    price       INTEGER NOT NULL,
    productSize TEXT        NULL,
    color       TEXT        NULL,

    updateCount INTEGER NOT NULL,
    created     TEXT    NOT NULL,
    updated     TEXT    NOT NULL,

    PRIMARY KEY(id)
);

-- Sale
DROP TABLE IF EXISTS Sale;
CREATE TABLE Sale (
    id          INTEGER NOT NULL,
    contactId   INTEGER NOT NULL,
    saleDate    TEXT    NOT NULL,
    taxRate     INTEGER NOT NULL,

    updateCount INTEGER NOT NULL,
    created     TEXT    NOT NULL,
    updated     TEXT    NOT NULL,

    PRIMARY KEY(id)
);

-- SaleItem
DROP TABLE IF EXISTS SaleItem;
CREATE TABLE SaleItem (
    saleId      INTEGER NOT NULL,
    itemIndex   INTEGER NOT NULL,
    productId   INTEGER NOT NULL,
    quantity    INTEGER NOT NULL,

    PRIMARY KEY(saleId, itemIndex)
);



-- Various
DROP TABLE IF EXISTS Various;
CREATE TABLE Various (
	id               INTEGER  NOT NULL,

	booleanPValue    INTEGER  NOT NULL DEFAULT 0,
	char1PValue      TEXT     NOT NULL DEFAULT ' ',
	tinyIntPValue    INTEGER  NOT NULL DEFAULT 0,
	smallIntPValue   INTEGER  NOT NULL DEFAULT 0,
	intPValue        INTEGER  NOT NULL DEFAULT 0,
	bigIntPValue     INTEGER  NOT NULL DEFAULT 0,
	floatPValue      REAL     NOT NULL DEFAULT 0,
	doublePValue     REAL     NOT NULL DEFAULT 0,

	booleanValue     INTEGER      ,
	char1Value       TEXT         ,
	tinyIntValue     INTEGER      ,
	smallIntValue    INTEGER      ,
	intValue         INTEGER      ,
	bigIntValue      INTEGER      ,
	floatValue       REAL         ,
	doubleValue      REAL         ,

	decimalValue     REAL         ,

	dateValue        TEXT         ,
	timeValue        TEXT         ,
	timeTZValue      TEXT         , -- instead of TIME WITH TIME ZONE type
	dateTimeValue    TEXT         ,
	timestampValue   TEXT         ,
	timestampTZValue TEXT         , -- instead of DATETIME  WITH TIME ZONE type

	longDate         INTEGER      , -- since 1.8.0
	longTime         INTEGER      , -- since 1.8.0
	longTimestamp    INTEGER      , -- since 1.8.0

	charValue        TEXT         ,
	varCharValue     TEXT         ,

	binaryValue      BLOB         ,
	varBinaryValue   BLOB         ,

	textValue        TEXT         ,
	blobValue        BLOB         ,

	PRIMARY KEY(id)
);
