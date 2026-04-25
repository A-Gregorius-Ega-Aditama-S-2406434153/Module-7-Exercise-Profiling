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

Profiler snapshot for optimized `/all-student`: [all-student-after-optimization.jfr](Jmeter/Assets/Optimized%20results/profiling/all-student-after-optimization.jfr)

Profiler snapshot for optimized `/all-student-name`: [all-student-name-after-optimization.jfr](Jmeter/Assets/Optimized%20results/profiling/all-student-name-after-optimization.jfr)

| Endpoint | Method | Before | After | Decrease | Modified |
| --- | --- | ---: | ---: | ---: | --- |
| `/all-student` | `getAllStudentsWithCourses()` | 119620 ms | 372 ms | 99.69% | Replaced per-student repository calls with one fetch-join query that loads `StudentCourse`, `Student`, and `Course` together. |
| `/all-student-name` | `joinStudentNames()` | 4694 ms | 107 ms | 97.72% | Replaced repeated `String` concatenation with `StringBuilder`. |

Optimized summary CSVs:
[all-student-summary.csv](Jmeter/Assets/Optimized%20results/csv/all-student-summary.csv),
[all-student-name-summary.csv](Jmeter/Assets/Optimized%20results/csv/all-student-name-summary.csv)

### `/all-student` Optimization

Before optimization, `getAllStudentsWithCourses()` loaded all students, then queried student-course rows once per student. With 20,000 students, this produced an N+1 query pattern and made the endpoint very slow.

After optimization, `StudentCourseRepository.findAllWithStudentAndCourse()` uses a fetch join to retrieve the student-course rows with their related student and course data in one query.

### `/all-student-name` Optimization

Before optimization, `joinStudentNames()` loaded all students and repeatedly appended to an immutable `String` inside a loop.

After optimization, it uses `StringBuilder` so the string buffer is reused while building the response.
