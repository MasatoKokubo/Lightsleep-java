-- (C) 2016 Masato Kokubo

-- for SQL Server

-- Numbering
DROP TABLE Numbering;
CREATE TABLE Numbering (
    tableName   VARCHAR (32) NOT NULL,
    nextId      INT          NOT NULL,

    PRIMARY KEY(tableName)
);

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
    id          INT          NOT NULL,
    firstName   VARCHAR (40) NOT NULL, -- 2 times
    lastName    VARCHAR (40) NOT NULL, -- 2 times
    birthday    DATE             NULL,
    addressId   INT              NULL,

    updateCount INT          NOT NULL,
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

    PRIMARY KEY(id)
);

-- Address
DROP TABLE Address;
CREATE TABLE Address (
    id          INT          NOT NULL,
    postCode    VARCHAR (10)     NULL,
    address1    VARCHAR (40)     NULL, -- 2 times
    address2    VARCHAR (40)     NULL, -- 2 times
    address3    VARCHAR (40)     NULL, -- 2 times
    address4    VARCHAR (40)     NULL, -- 2 times

    updateCount INT          NOT NULL,
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

    PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
    id          INT          NOT NULL,
    contactId   INT          NOT NULL,
    phoneNumber VARCHAR (12) NOT NULL,

    updateCount INT          NOT NULL,
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

    PRIMARY KEY(id)
);

-- Product
DROP TABLE Product;
CREATE TABLE Product (
    id          INT          NOT NULL,
    productName VARCHAR (40) NOT NULL, -- 2 times
    price       INT          NOT NULL,
    productSize CHAR     (2) NULL,
    color       VARCHAR (20) NULL,

    updateCount INT          NOT NULL,
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

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
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

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
    id               INT                   NOT NULL,

    booleanPValue    BIT       DEFAULT 0   NOT NULL,
    char1PValue      CHAR(2)   DEFAULT ' ' NOT NULL, -- 2 times
    tinyIntPValue    TINYINT   DEFAULT 0   NOT NULL,
    smallIntPValue   SMALLINT  DEFAULT 0   NOT NULL,
    intPValue        INT       DEFAULT 0   NOT NULL,
    bigIntPValue     BIGINT    DEFAULT 0   NOT NULL,
    floatPValue      FLOAT(24) DEFAULT 0   NOT NULL,
    doublePValue     FLOAT(53) DEFAULT 0   NOT NULL,

    booleanValue     BIT              ,
    char1Value       CHAR          (2), -- 2 times
    tinyIntValue     TINYINT          ,
    smallIntValue    SMALLINT         ,
    intValue         INT              ,
    bigIntValue      BIGINT           ,
    floatValue       FLOAT        (24),
    doubleValue      FLOAT        (53),
    decimalValue     DECIMAL    (12,2),

    longDate         BIGINT           , -- since 1.8.0
    longTime         BIGINT           , -- since 1.8.0
    longTimestamp    BIGINT           , -- since 1.8.0

    charValue        CHAR         (40), -- 2 times
    varCharValue     VARCHAR      (80), -- 2 times

    binaryValue      BINARY       (20),
    varBinaryValue   VARBINARY    (40),

    textValue        TEXT             ,
    blobValue        IMAGE            ,
    jsonValue        TEXT             ,

    PRIMARY KEY(id)
);

-- DateAndTime since 3.0.0
DROP TABLE DateAndTime;
CREATE TABLE DateAndTime (
    id               INT      NOT NULL,

    dateValue        DATE             ,
    timeValue        TIME          (7),
    timestampValue   DATETIME2     (7),
    timestampTZValue DATETIMEOFFSET(7),

    PRIMARY KEY(id)
);
