# Module 07 - Profiling

## Project Setup

The application is configured to use PostgreSQL through `src/main/resources/application.properties`.
Before running the application, make sure the configured database exists and the username/password match the local PostgreSQL installation.

Useful commands:

```powershell
.\backup\apache-maven-3.9.15\bin\mvn.cmd install
.\backup\apache-maven-3.9.15\bin\mvn.cmd spring-boot:run
```

After the application starts, seed the data using:

```text
http://localhost:8080/seed-data-master
http://localhost:8080/seed-student-course
```
