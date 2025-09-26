CREATE TABLE Business (
    id INTEGER PRIMARY KEY NOT NULL ,
    businessName TEXT UNIQUE NOT NULL,
    created INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
);

CREATE TABLE FieldConfig (
 fieldId         INTEGER  PRIMARY KEY NOT NULL ,
 bId             INTEGER  NOT NULL DEFAULT -1, -- bId=-1表示全局属性
 fieldName       TEXT     NOT NULL ,
 fieldType       TEXT     NOT NULL ,
 alias           TEXT     NOT NULL , -- 别名
 width           INTEGER  NOT NULL DEFAULT 1,
 options         TEXT , -- 选项列表，用于 CheckBox/Radio/Table等
 weight          INTEGER NOT NULL DEFAULT 0,
 validationRule  TEXT   NOT NULL DEFAULT "",
 forced          INTEGER NOT NULL DEFAULT 0, --是否必须，bId=-1 才有效，针对全局属性是否必输
 created         INTEGER  NOT NULL DEFAULT (strftime('%s', 'now'))
);

CREATE TABLE FieldValue (
 fieldValueId           INTEGER  NOT NULL ,
 uid          INTEGER  NOT NULL REFERENCES  User(id),
 fieldId      INTEGER  NOT NULL REFERENCES FieldConfig(fieldId),
 fieldValue   TEXT     NOT NULL ,
 created      INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
 PRIMARY KEY (uid,fieldId)
);

CREATE TABLE RelBizTpl (
    id INTEGER NOT NULL ,
    bId INTEGER NOT NULL REFERENCES Business(id),
    tId INTEGER NOT NULL REFERENCES Template(id),
    created INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
    PRIMARY KEY (bId,tId)
);


-- 在这个表中，看不出那个字段是全局的，只有通过fieldId去查询
CREATE TABLE RelFieldTplField (
 id              INTEGER  NOT NULL ,
 fieldId         INTEGER  REFERENCES FieldConfig(fieldId),
 tFieldId        INTEGER  NOT NULL REFERENCES TplField(id),
 bId             INTEGER  NOT NULL REFERENCES Business(id), -- 这里的bId表示在这个业务项下的映射关系
 isFixed         INTEGER NOT NULL ,
 fixedValue      TEXT NOT NULL DEFAULT "",
 created INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
 PRIMARY KEY (fieldId,tFieldId)
);

CREATE TABLE SystemConfig (
key     TEXT PRIMARY KEY UNIQUE NOT NULL,
value   TEXT NOT NULL,
created         INTEGER  NOT NULL DEFAULT (strftime('%s', 'now'))
);

CREATE TABLE Template (
    id INTEGER PRIMARY KEY NOT NULL ,
    templateName TEXT NOT NULL,
    filePath TEXT NOT NULL,
    fileType TEXT NOT NULL,
    created INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
);

CREATE TABLE TplField (
    id INTEGER PRIMARY KEY NOT NULL ,
    templateId INTEGER NOT NULL,
    formFieldName TEXT NOT NULL,
    alias TEXT NOT NULL ,
    formFieldType TEXT NOT NULL DEFAULT "TEXT",
    created INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
);

CREATE TABLE User (
    id INTEGER PRIMARY KEY NOT NULL ,
    created INTEGER NOT NULL DEFAULT (strftime('%s','now')) -- 时间戳格式
);