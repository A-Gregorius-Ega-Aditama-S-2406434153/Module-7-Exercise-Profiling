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

## Reflection

### 1. Difference between performance testing with JMeter and profiling with IntelliJ Profiler

JMeter measures application behavior from the outside by sending HTTP requests and recording response time, throughput, errors, and other request-level metrics. It is useful for understanding how users experience the system under load.

IntelliJ Profiler measures the application from the inside while the code is running. It shows which methods consume CPU time, total time, and other runtime resources. In this project, JMeter helps confirm that endpoints such as `/all-student`, `/all-student-name`, and `/highest-gpa` are slow from the client perspective, while the profiler helps identify which Java methods are responsible for that slowness.

### 2. How profiling helps identify weak points

Profiling makes performance problems easier to locate because it shows the execution cost of each method. Instead of guessing which part of the code is slow, I can inspect the flame graph, timeline, and method list to find expensive methods. For example, if one service method dominates CPU time, that method becomes the main candidate for optimization.

### 3. Effectiveness of IntelliJ Profiler

IntelliJ Profiler is effective because it is integrated directly into the development workflow. It can connect runtime behavior to the source code, so bottlenecks are easier to inspect and refactor. The flame graph is useful for seeing call hierarchy, while the method list is useful for comparing CPU time and total time more precisely.

### 4. Main challenges in performance testing and profiling

The main challenges are preparing representative test data, keeping test runs consistent, and separating real bottlenecks from noise caused by startup time, JIT warmup, database state, or machine load. I overcome these issues by warming up the application before measuring, using the same dataset for before-and-after comparisons, repeating measurements, and comparing both JMeter results and profiler results before deciding what to optimize.

### 5. Main benefits of IntelliJ Profiler

The main benefit is visibility into what the application is doing internally. It helps reveal inefficient loops, repeated database calls, expensive string operations, and methods that spend too much time waiting on child calls. This makes optimization more targeted because the changes can focus on the highest-cost code instead of changing unrelated parts of the application.

### 6. Handling inconsistent JMeter and profiler results

If JMeter and IntelliJ Profiler show different conclusions, I compare what each tool is actually measuring. JMeter includes network, HTTP handling, serialization, database waits, and server load from the client perspective. The profiler focuses on runtime behavior inside the application process. I use JMeter to validate user-visible improvement and the profiler to explain the source-code reason behind the result. If the results conflict, I repeat the test with controlled data, warmup runs, and similar load conditions.

### 7. Optimization strategies after analysis

After analyzing the results, I optimize the parts with the highest measured cost first. Common strategies include reducing repeated database queries, using repository queries that let the database perform filtering or sorting, replacing repeated string concatenation with `StringBuilder` or stream joining, and avoiding unnecessary object creation. To ensure functionality is not affected, I keep changes small, run automated tests, manually test the affected endpoints, and compare the output before and after refactoring.
