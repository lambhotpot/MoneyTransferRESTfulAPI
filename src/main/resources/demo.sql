CREATE TABLE User (UserId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
 UserName VARCHAR(30) NOT NULL,
 EmailAddress VARCHAR(30) NOT NULL);

INSERT INTO User (UserName, EmailAddress) VALUES ('yangluo','yangluo@gmail.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('qinfran','qinfran@gmail.com');

CREATE TABLE Account (AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
UserName VARCHAR(30),
Balance DECIMAL(20,4),
CurrencyCode VARCHAR(30)
);

INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('yangluo',100.0000,'USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('qinfran',200.0000,'USD');

