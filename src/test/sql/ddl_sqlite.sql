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
	id               INTEGER NOT NULL,

	booleanPValue    INTEGER DEFAULT 0   NOT NULL,
	char1PValue      TEXT    DEFAULT ' ' NOT NULL,
	tinyIntPValue    INTEGER DEFAULT 0   NOT NULL,
	smallIntPValue   INTEGER DEFAULT 0   NOT NULL,
	intPValue        INTEGER DEFAULT 0   NOT NULL,
	bigIntPValue     INTEGER DEFAULT 0   NOT NULL,
	floatPValue      REAL    DEFAULT 0   NOT NULL,
	doublePValue     REAL    DEFAULT 0   NOT NULL,

	booleanValue     INTEGER,
	char1Value       TEXT   ,
	tinyIntValue     INTEGER,
	smallIntValue    INTEGER,
	intValue         INTEGER,
	bigIntValue      INTEGER,
	floatValue       REAL   ,
	doubleValue      REAL   ,
    decimalValue     REAL   ,

    longDate         INTEGER, -- since 1.8.0
    longTime         INTEGER, -- since 1.8.0
    longTimestamp    INTEGER, -- since 1.8.0

    charValue        TEXT   ,
    varCharValue     TEXT   ,

    binaryValue      BLOB   ,
    varBinaryValue   BLOB   ,

    textValue        TEXT   ,
    blobValue        BLOB   ,

    PRIMARY KEY(id)
);


-- DateAndTime since 3.0.0
DROP TABLE IF EXISTS DateAndTime;
CREATE TABLE DateAndTime (
    id               INT  NOT NULL,

    dateValue        DATE,
    timeValue        TIME,
    timestampValue   DATETIME,
    timestampTZValue DATETIME,

    PRIMARY KEY(id)
);
