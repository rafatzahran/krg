# krg
 Demo project for KRG

```
mysql -u root -p
create database krg_db;
CREATE USER 'krg'@'localhost' IDENTIFIED BY 'krg';
GRANT ALL PRIVILEGES ON krg_db . * TO 'krg'@'localhost';
FLUSH PRIVILEGES;

CTRL + d

mysql -u krg -p
show databases;
```