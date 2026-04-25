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
