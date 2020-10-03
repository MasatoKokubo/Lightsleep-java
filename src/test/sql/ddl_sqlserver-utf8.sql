-- (C) 2016 Masato Kokubo

-- for SQL Server (UTF-8)

-- Contact
DROP TABLE Contact;
CREATE TABLE Contact (
    id          INT IDENTITY(1,1) NOT NULL,
    firstName   VARCHAR (20)  NOT NULL,
    lastName    VARCHAR (20)  NOT NULL,
    birthday    DATE              NULL,
    addressId   INT               NULL,

    updateCount INT           NOT NULL,
    created     DATETIME2(7)  NOT NULL,
    updated     DATETIME2(7)  NOT NULL,

    PRIMARY KEY(id)
);

-- Address
DROP TABLE Address;
CREATE TABLE Address (
    id          INT IDENTITY(1,1) NOT NULL,
    postCode    VARCHAR (10)     NULL,
    address1    VARCHAR (20)     NULL,
    address2    VARCHAR (20)     NULL,
    address3    VARCHAR (20)     NULL,
    address4    VARCHAR (20)     NULL,

    updateCount INT          NOT NULL,
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

    PRIMARY KEY(id)
);

-- Phone
DROP TABLE Phone;
CREATE TABLE Phone (
    id          INT IDENTITY(1,1) NOT NULL,
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
    id          INT IDENTITY(1,1) NOT NULL,
    productName VARCHAR (20) NOT NULL,
    price       INT          NOT NULL,
    productSize CHAR     (2)     NULL,
    color       VARCHAR (20)     NULL,

    updateCount INT          NOT NULL,
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

    PRIMARY KEY(id)
);

-- Sale
DROP TABLE Sale;
CREATE TABLE Sale (
    id          INT IDENTITY(1,1) NOT NULL,
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
    id               INT       NOT NULL,

    booleanPValue    BIT       DEFAULT 0   NOT NULL,
    char1PValue      NCHAR(1)  DEFAULT ' ' NOT NULL,
    tinyIntPValue    TINYINT   DEFAULT 0   NOT NULL,
    smallIntPValue   SMALLINT  DEFAULT 0   NOT NULL,
    intPValue        INT       DEFAULT 0   NOT NULL,
    bigIntPValue     BIGINT    DEFAULT 0   NOT NULL,
    floatPValue      FLOAT(24) DEFAULT 0   NOT NULL,
    doublePValue     FLOAT(53) DEFAULT 0   NOT NULL,

    booleanValue     BIT              ,
    char1Value       NCHAR         (1),
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

    charValue        NCHAR        (21),
    varCharValue     VARCHAR      (40),

    binaryValue      BINARY       (20),
    varBinaryValue   VARBINARY    (40),

    textValue        VARCHAR     (MAX),
    blobValue        IMAGE            ,
    jsonValue        VARCHAR     (MAX),

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


-- Node since 4.0.0
DROP TABLE IF EXISTS Node;
CREATE TABLE Node (
    id          INT IDENTITY(1,1) NOT NULL,
    parentId    INT          NOT NULL,
    name        VARCHAR (32) NOT NULL,

    updateCount INT          NOT NULL,
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

    PRIMARY KEY(id)
);

-- Leaf since 4.0.0
DROP TABLE IF EXISTS Leaf;
CREATE TABLE Leaf (
    id          INT IDENTITY(1,1) NOT NULL,
    parentId    INT          NOT NULL,
    name        VARCHAR (32) NOT NULL,
    content     VARCHAR(MAX) NOT NULL,

    updateCount INT          NOT NULL,
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

    PRIMARY KEY(id)
);

-- Leaf2 since 4.0.0
DROP TABLE IF EXISTS Leaf2;
CREATE TABLE Leaf2 (
    id          INT IDENTITY(1,1) NOT NULL,
    parentId    INT          NOT NULL,
    name        VARCHAR (32) NOT NULL,
    content     VARCHAR(MAX) NOT NULL,

    updateCount INT          NOT NULL,
    created     DATETIME2(7) NOT NULL,
    updated     DATETIME2(7) NOT NULL,

    PRIMARY KEY(id)
);
