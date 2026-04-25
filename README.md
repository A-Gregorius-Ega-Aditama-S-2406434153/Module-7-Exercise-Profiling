# Module 07 - Profiling
## Reflections and Self documentations for future self
## Project Setup

The application is configured to use PostgreSQL through `src/main/resources/application.properties`.
Before running the application, make sure the configured database exists and the 
username/password match the local PostgreSQL installation.

Useful commands:

```powershell
.\apache-maven-3.9.15\bin\mvn.cmd install
.\apache-maven-3.9.15\bin\mvn.cmd spring-boot:run
```

After the application starts, seed the data using:
```text
http://localhost:8080/seed-data-master
http://localhost:8080/seed-student-course
```

## Unoptimized JMeter Results

The following results were collected before optimization using 10 samples for each endpoint.
The raw JMeter logs, generated reports, and summary CSV files are saved under `Jmeter/Assets/Unoptimized results`.

| Endpoint | Samples | Average | Min | Max | Std. Dev. | Error % | Throughput | Avg. Bytes |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| `/all-student` | 10 | 119620 ms | 117211 ms | 120588 ms | 1183.80 | 0.000% | 0.08253/sec | 2801577.0 |
| `/all-student-name` | 10 | 4694 ms | 4496 ms | 4833 ms | 100.42 | 0.000% | 1.76647/sec | 312661.0 |
| `/highest-gpa` | 10 | 337 ms | 314 ms | 387 ms | 20.45 | 0.000% | 8.26446/sec | 279.0 |

Summary CSV: [unoptimized-summary.csv](Jmeter/Assets/Unoptimized%20results/csv/unoptimized-summary.csv)

Profiler snapshot: [TutorialApplication-before-optimization.jfr](Jmeter/Assets/Unoptimized%20results/profiling/TutorialApplication-before-optimization.jfr)

Profiler snapshot for unoptimized `/all-student-name`: [all-student-name-before-optimization.jfr](Jmeter/Assets/Unoptimized%20results/profiling/all-student-name-before-optimization.jfr)

Profiler snapshot for unoptimized `/highest-gpa`: [highest-gpa-before-optimization.jfr](Jmeter/Assets/Unoptimized%20results/profiling/highest-gpa-before-optimization.jfr)

### Screenshots

SUMMARY `/all-student`

![Unoptimized all-student](Jmeter/Assets/Unoptimized%20results/screenshots/all-student.png)

SUMMARY `/all-student-name`

![Unoptimized all-student-name](Jmeter/Assets/Unoptimized%20results/screenshots/all-student-name.png)

SUMMARY `/highest-gpa`

![Unoptimized highest-gpa](Jmeter/Assets/Unoptimized%20results/screenshots/highest-gpa.png)

### Raw Result Files

RAW RESULT `/all-student`

![Unoptimized all-student raw results](Jmeter/Assets/Unoptimized%20results/screenshots/all-student-raw-results.png)

RAW RESULT `/all-student-name`

![Unoptimized all-student-name raw results](Jmeter/Assets/Unoptimized%20results/screenshots/all-student-name-raw-results.png)

RAW RESULT `/highest-gpa`

![Unoptimized highest-gpa raw results](Jmeter/Assets/Unoptimized%20results/screenshots/highest-gpa-raw-results.png)

## Optimized JMeter Results

Profiler snapshot for optimizing method `getAllStudentsWithCourses()`: [all-student-after-optimization.jfr](Jmeter/Assets/Optimized%20results/profiling/all-student-after-optimization.jfr)

Profiler snapshot for optimized `/all-student-name`: [all-student-name-after-optimization.jfr](Jmeter/Assets/Optimized%20results/profiling/all-student-name-after-optimization.jfr)

Profiler snapshot for optimized `/highest-gpa`: [highest-gpa-after-optimization.jfr](Jmeter/Assets/Optimized%20results/profiling/highest-gpa-after-optimization.jfr)

| Endpoint | Method | Before | After | Decrease | Modified |
| --- | --- | ---: | ---: | ---: | --- |
| `/all-student` | `getAllStudentsWithCourses()` | 119620 ms | 364 ms | 99.70% | Replaced per-student repository calls with one fetch-join query that loads `StudentCourse`, `Student`, and `Course` together. |
| `/all-student-name` | `joinStudentNames()` | 4694 ms | 83 ms | 98.23% | Replaced repeated `String` concatenation with `StringBuilder`. |
| `/highest-gpa` | `findStudentWithHighestGpa()` | 337 ms | 13 ms | 96.14% | Replaced loading and scanning all students in Java with `findFirstByOrderByGpaDesc()`. |

Optimized summary CSVs:
[optimized-summary.csv](Jmeter/Assets/Optimized%20results/csv/optimized-summary.csv),
[all-student-summary.csv](Jmeter/Assets/Optimized%20results/csv/all-student-summary.csv),
[all-student-name-summary.csv](Jmeter/Assets/Optimized%20results/csv/all-student-name-summary.csv),
[highest-gpa-summary.csv](Jmeter/Assets/Optimized%20results/csv/highest-gpa-summary.csv)

### `/all-student` Optimization

Before optimization, `getAllStudentsWithCourses()` loaded all students, then queried student-course rows once per student. With 20,000 students, this produced an N+1 query pattern and made the endpoint very slow.

After optimization, `StudentCourseRepository.findAllWithStudentAndCourse()` uses a fetch join to retrieve the student-course rows with their related student and course data in one query.

### `/all-student-name` Optimization

Before optimization, `joinStudentNames()` loaded all students and repeatedly appended to an immutable `String` inside a loop.

After optimization, it uses `StringBuilder` so the string buffer is reused while building the response.

### `/highest-gpa` Optimization

Before optimization, `findStudentWithHighestGpa()` loaded all students and compared GPA values in Java.

After optimization, `StudentRepository.findFirstByOrderByGpaDesc()` asks the database for the highest-GPA student directly.

### Conclusion

The JMeter measurements show clear improvement after profiling and optimization. The largest improvement was on `/all-student`, where removing the N+1 query pattern reduced the average response time from `119620 ms` to `364 ms`. The `/all-student-name` endpoint also improved from `4694 ms` to `83 ms` after replacing repeated string concatenation with `StringBuilder`. The `/highest-gpa` endpoint improved from `337 ms` to `13 ms` after moving the max-GPA lookup into the database query. Based on these measurements, the optimizations were effective because every tested endpoint had lower average response time and still returned `0.000%` errors in JMeter.

## Reflection

### 1. Difference between JMeter performance testing and IntelliJ Profiler profiling

JMeter measures application performance from the outside by sending HTTP requests and recording response time, throughput, and error rate. IntelliJ Profiler measures performance from inside the application by showing which methods, queries, and code paths consume the most time. In this project, JMeter showed that the endpoints were slow, while IntelliJ Profiler helped identify why they were slow.

### 2. How profiling helps identify weak points

Profiling helps identify weak points by showing where execution time is spent. For example, profiling helped reveal expensive service methods such as `getAllStudentsWithCourses()`, `joinStudentNames()`, and `findStudentWithHighestGpa()`. This made the optimization process more focused because I could target the methods that actually caused the bottlenecks.

### 3. Effectiveness of IntelliJ Profiler

IntelliJ Profiler was effective because it connected runtime performance data directly to the application code. It made it easier to see which methods were expensive and compare behavior before and after optimization. This was useful for confirming that the changes reduced work inside the application.

### 4. Main challenges in performance testing and profiling

The main challenges were keeping the database seeded correctly, making sure the application was running on the expected port, and comparing results under similar conditions. I handled these by verifying the seeded data, running JMeter with the same test plans, saving before-and-after results, and checking that the error rate stayed at `0.000%`.

### 5. Main benefits of IntelliJ Profiler

The main benefit of IntelliJ Profiler is that it shows the internal cause of slow performance instead of only showing that an endpoint is slow. It helped separate database query problems from Java code problems, such as the N+1 query pattern in `getAllStudentsWithCourses()` and repeated string concatenation in `joinStudentNames()`.

### 6. Handling differences between profiler and JMeter results

If IntelliJ Profiler and JMeter results are not fully consistent, I compare what each tool measures. JMeter measures the full request from the client perspective, including HTTP handling, database access, serialization, and response transfer. IntelliJ Profiler focuses on execution inside the application. I use JMeter to confirm user-visible performance improvement and IntelliJ Profiler to understand the code-level reason behind the result.

### 7. Optimization strategies after analysis

After analyzing the results, I optimized the highest-cost code paths first. For `/all-student`, I replaced repeated repository calls with a fetch join. For `/all-student-name`, I replaced repeated `String` concatenation with `StringBuilder`. For `/highest-gpa`, I moved the max-GPA lookup into the database query. To ensure functionality was not affected, I kept the endpoint behavior the same, ran automated tests, reran JMeter, and confirmed all optimized tests completed with `0.000%` errors.
