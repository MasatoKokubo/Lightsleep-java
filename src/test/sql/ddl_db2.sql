-- (C) 2016 Masato Kokubo

-- for DB2

-- Numbering
DROP TABLE Numbering;
CREATE TABLE Numbering (
    tableName   VARCHAR   (32) NOT NULL,
    nextId      INTEGER        NOT NULL,

    PRIMARY KEY(tableName)
);

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
    id          INTEGER        NOT NULL,
    firstName   VARGRAPHIC(20) NOT NULL,
    lastName    VARGRAPHIC(20) NOT NULL,
    birthday    DATE               NULL,
    addressId   INTEGER            NULL,

    updateCount INTEGER        NOT NULL,
    created     TIMESTAMP(9)   NOT NULL,
    updated     TIMESTAMP(9)   NOT NULL,

    PRIMARY KEY(id)
);

-- Address
DROP TABLE Address;
CREATE TABLE Address (
    id          INTEGER        NOT NULL,
    postCode    VARCHAR   (10)     NULL,
    address1    VARGRAPHIC(20)     NULL,
    address2    VARGRAPHIC(20)     NULL,
    address3    VARGRAPHIC(20)     NULL,
    address4    VARGRAPHIC(20)     NULL,

    updateCount INTEGER        NOT NULL,
    created     TIMESTAMP(9)   NOT NULL,
    updated     TIMESTAMP(9)   NOT NULL,

    PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
    id          INTEGER        NOT NULL,
    contactId   INTEGER        NOT NULL,
    phoneNumber VARCHAR   (12) NOT NULL,

    updateCount INTEGER        NOT NULL,
    created     TIMESTAMP(9)   NOT NULL,
    updated     TIMESTAMP(9)   NOT NULL,

    PRIMARY KEY(id)
);

-- Product
DROP TABLE Product;
CREATE TABLE Product (
    id          INTEGER        NOT NULL,
    productName VARGRAPHIC(20) NOT NULL,
    price       INTEGER        NOT NULL,
    productSize CHAR      ( 2)     NULL,
    color       VARGRAPHIC(20)     NULL,

    updateCount INTEGER        NOT NULL,
    created     TIMESTAMP(9)   NOT NULL,
    updated     TIMESTAMP(9)   NOT NULL,

    PRIMARY KEY(id)
);

-- Sale
DROP TABLE Sale;
CREATE TABLE Sale (
    id          INTEGER        NOT NULL,
    contactId   INTEGER        NOT NULL,
    saleDate    DATE           NOT NULL,
    taxRate     SMALLINT       NOT NULL,

    updateCount INTEGER        NOT NULL,
    created     TIMESTAMP(9)   NOT NULL,
    updated     TIMESTAMP(9)   NOT NULL,

    PRIMARY KEY(id)
);

-- SaleItem
DROP TABLE SaleItem;
CREATE TABLE SaleItem (
    saleId      INTEGER        NOT NULL,
    itemIndex   SMALLINT       NOT NULL,
    productId   INTEGER        NOT NULL,
    quantity    SMALLINT       NOT NULL,

    PRIMARY KEY(saleId, itemIndex)
);




-- Various
DROP TABLE Various;
CREATE TABLE Various (
    id               INTEGER        NOT NULL,

    booleanPValue    BOOLEAN        DEFAULT FALSE NOT NULL,
    char1PValue      GRAPHIC    (1) DEFAULT ' '   NOT NULL,
    tinyIntPValue    SMALLINT       DEFAULT 0     NOT NULL, -- instead of TINYINT type
    smallIntPValue   SMALLINT       DEFAULT 0     NOT NULL,
    intPValue        INTEGER        DEFAULT 0     NOT NULL,
    bigIntPValue     BIGINT         DEFAULT 0     NOT NULL,
    floatPValue      REAL           DEFAULT 0     NOT NULL,
    doublePValue     DOUBLE         DEFAULT 0     NOT NULL,

    booleanValue     BOOLEAN       ,
    char1Value       GRAPHIC    (1),
    tinyIntValue     SMALLINT      , -- instead of TINYINT type
    smallIntValue    SMALLINT      ,
    intValue         INTEGER       ,
    bigIntValue      BIGINT        ,
    floatValue       REAL          ,
    doubleValue      DOUBLE        ,
    decimalValue     DECIMAL (12,2),

    longDate         BIGINT        , -- since 1.8.0
    longTime         BIGINT        , -- since 1.8.0
    longTimestamp    BIGINT        , -- since 1.8.0

--  charValue        GRAPHIC   (20),
    charValue        GRAPHIC   (21),
    varCharValue     VARGRAPHIC(40),

    binaryValue      BLOB      (20), -- instead of BINARY   (20) type
    varBinaryValue   BLOB      (40), -- instead of VARBINARY(40) type

    textValue        CLOB     (16M),
    blobValue        BLOB     (16M),
    jsonValue        CLOB     (16M),

    PRIMARY KEY(id)
);

-- DateAndTime since 3.0.0
DROP TABLE DateAndTime;
CREATE TABLE DateAndTime (
    id               INTEGER NOT NULL,

    dateValue        DATE        ,
    timeValue        TIME        ,
    timestampValue   TIMESTAMP(9),
    timestampTZValue TIMESTAMP(9),

    PRIMARY KEY(id)
);
