# Petstore API Automation (Java + Rest Assured + JUnit 5)

Production-style API test project for the Swagger Petstore **v2** PET endpoints.

## Coverage
- `POST /pet`: create a pet and assert a successful 2xx response.
- `GET /pet/{petId}`: assert HTTP 200 and verify every key field matches the POST request.
- `PUT /pet`: update the pet and assert the changed name/status via GET.
- `DELETE /pet/{petId}`: delete the test pet and assert a successful 2xx response.
- Negative check: GET an unknown ID and assert HTTP 404.
- Unsupported-method check: send `PATCH /pet` and assert HTTP 405 Method Not Allowed.
- Configuration via `api.baseUrl` system property; API calls encapsulated in a client; model records; unique test ID.

Official API documentation: https://petstore.swagger.io/ (base URL `https://petstore.swagger.io/v2`).

## Prerequisites
- JDK 17+
- Maven 3.8+

## Run
```bash
mvn clean test
```
Override base URL if needed:
```bash
mvn clean test -Dapi.baseUrl=https://petstore.swagger.io/v2
```

## Notes
The Petstore is a shared public demo service, not a stable production environment. Tests use a unique ID but external users or service resets can still affect results. The unknown-ID negative assertion assumes the service returns 404 for an absent ID, as documented. Run the suite serially; it intentionally exercises the same pet across ordered test methods.

## Suggested CI
Run `mvn -B clean test` in a GitHub Actions workflow on pull requests and pushes. Add environment-specific reporting, retries only for transient infrastructure failures, and secret-managed credentials if the target API later requires authentication.

## Publish to Git
```bash
git init
git add .
git commit -m "Add Petstore API automation tests"
git branch -M main
git remote add origin https://github.com/<your-username>/petstore-api-automation.git
git push -u origin main
```
