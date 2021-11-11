# krg
 Demo project for KRG

#### Configure Mysql: new user and db
```
mysql -u root -p
create database krg_db;
CREATE USER 'krg'@'localhost' IDENTIFIED BY 'krg';
GRANT ALL PRIVILEGES ON krg_db . * TO 'krg'@'localhost';
FLUSH PRIVILEGES;
```

```
mysql -u krg -p (***)
show databases;
```

#### Configure Spring Datasource, JPA, Hibernate

- spring.datasource.username & spring.datasource.password properties are the same as your database installation.
- Spring Boot uses Hibernate for JPA implementation, we configure MySQL5InnoDBDialect for MySQL.
- spring.jpa.hibernate.ddl-auto is used for database initialization. 
We set the value to update value so that a table will be created in the database automatically corresponding to defined data model. Any change to the model will also trigger an update to the table. For production, this property should be validate.

### Define Data Model

- **@Entity** annotation indicates that the class is a persistent Java class.
- **@Table** annotation provides the table that maps this entity.
- **@Id** annotation is for the primary key.
- **@GeneratedValue** annotation is used to define generation strategy for the primary key.
- **@Column** annotation is used to define the column in database that maps annotated field.

### Create Repository Interface

- Create a repository to interact with model from the database.

### Create Spring Rest APIs Controller

- UserController 

### Start the app

```sh
$ cd krg
$ mvn package 
$ java -jar target/demo-0.0.1-SNAPSHOT.jar
```
