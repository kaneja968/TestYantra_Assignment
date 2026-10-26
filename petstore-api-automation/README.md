# Petstore API Automation

Java 17 API test suite using Rest Assured and JUnit 5 for the Swagger Petstore v2 API.

## Coverage

- End-to-end create, read, update, and delete flow with a unique pet ID and cleanup on failure.
- Negative checks for an unknown pet and an unsupported HTTP method.
- Parallel execution for independent test methods; CRUD operations stay together as one isolated scenario.
- Per-request HTTP connection and response timeouts.
- Fail-fast validation for the configured HTTP(S) base URL.
- Surefire XML/text report, Maven HTML report, and Extent Spark HTML report.

## Requirements

- JDK 17 or later
- Maven 3.8 or later

## Run

Run tests and generate the HTML report:

```bash
mvn clean verify
```

The Maven HTML report is generated at `target/reports/test-report.html`, and the Extent Spark report at `target/spark-report/Spark.html`. Surefire also writes XML and text results to `target/surefire-reports/`.

## Environments

Set the target URL with either a Maven property or an environment variable. The Maven property takes precedence; the default is the public Petstore v2 endpoint.

```bash
mvn clean verify -Dapi.baseUrl=https://petstore.swagger.io/v2
```

```bash
API_BASE_URL=https://your-test-environment.example/api/v2 mvn clean verify
```

## CI

The GitHub Actions workflow runs the suite on pushes and pull requests, then uploads the Surefire results and both HTML reports as a build artifact, including when tests fail.

The public Petstore is a shared demo service and is not a production-stable dependency. For reliable team CI, point `API_BASE_URL` at an isolated test instance.
