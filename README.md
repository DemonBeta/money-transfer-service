# Money Transfer Service

### Third party libraries

- jOOQ - SQL queries builder
- Lombok - code generation library
- Flyway - database migration tool
- H2 - embedded database
- Guice - lightweight DI framework
- Javalin - lightweight web framework

### How to run
Build jar file by executing mvn command from project root directory
```sh
mvn clean generate-sources package
```
and then run jar file
```sh
java -jar -Dport=8080 ./target/transfer.jar
```
Application starts on port 8080 if no specified. Database already contain 2 account entries.
### API
| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| PUT | http://localhost:8080/account/create?balance=500 | creates new account with specified balance and returns account id | 
| GET | http://localhost:8080/account/get-balance?id=1 | returns balance of account by id | 
| POST | http://localhost:8080/account/transfer?fromId=1&toId=2&amount=200 | transfer specified amount of money between two accounts | 
| DELETE | http://localhost:8080/account/delete?id=1 | deletes account by id | 
