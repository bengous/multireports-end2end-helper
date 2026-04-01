# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java 21 E2E API testing framework using TestNG, RestAssured, and Allure reporting. Designed for multi-environment testing with GitHub Actions CI/CD and GitHub Pages report publishing.

## Build & Test Commands

```bash
# Run all tests (uses testng-jsonapi.xml suite by default)
mvn clean test -Dtarget.suite.xml.file=src/test/resources/testng/testng-jsonapi.xml

# Run with custom API URL
mvn clean test -Dapi.url=https://example.com/api -Dtarget.suite.xml.file=src/test/resources/testng/testng-jsonapi.xml

# Run specific test groups
mvn clean test -Dgroups=smoke -Dtarget.suite.xml.file=src/test/resources/testng/testng-jsonapi.xml
mvn clean test -Dgroups=simple -Dtarget.suite.xml.file=src/test/resources/testng/testng-jsonapi.xml
mvn clean test -Dgroups=scenario -Dtarget.suite.xml.file=src/test/resources/testng/testng-jsonapi.xml

# Generate Allure report after tests
mvn allure:report

# View Allure report (opens browser)
mvn allure:serve
```

## Architecture

### Test Configuration Flow
1. TestNG suite XML (`src/test/resources/testng/`) defines `apiURL` and `apiURLfallback` parameters
2. `BaseTest` resolves API URL via `PropertyUtils.getPropertyOrFallback()` - checks system property first, then `config.properties`
3. Fallback URL defined in `src/test/resources/config.properties`

### Key Patterns

**StepChain<T>** (`com.bengous.e2e.common.allure.StepChain`): Fluent builder for multi-step scenario tests. Steps execute sequentially with shared immutable context (use `toBuilder()` pattern). Failed steps cause remaining steps to be skipped but still reported in Allure.

```java
new StepChain<>(initialContext)
    .runStep("Step 1", ctx -> ctx.toBuilder().value(newValue).build())
    .runVoidStep("Step 2", ctx -> assertSomething(ctx))
    .execute();
```

**Test Types**: Group tests using `TestType` constants (`SMOKE`, `SIMPLE`, `SCENARIO`) in `@Test(groups = {...})`.

**JsonAssertion**: Compares JSON with automatic Allure attachments. Uses json-unit's `${json-unit.ignore-element}` for fields present in actual but not in expected.

### Package Structure

- `com.bengous.e2e.common` - Framework utilities (BaseTest, PropertyUtils, TestType)
- `com.bengous.e2e.common.allure` - Allure integration (StepChain, AllureHelper)
- `com.bengous.e2e.common.assertions` - Custom assertions (JsonAssertion, XmlAssertion)
- `com.bengous.e2e.common.client` - RestAssured wrapper and filters
- `com.bengous.e2e.tests.<api-name>` - Test classes per API
- `com.bengous.e2e.tests.<api-name>.context` - Immutable context records for scenario tests

### CI/CD

GitHub Actions runs CI on `main`, generates Allure reports, and deploys the default branch site to GitHub Pages.

Published report metadata is tracked in `public/all_reports_info.json`.

## Code Style

- 4 spaces indentation, 120 char line width
- Multiline chained method calls aligned
- Array initializers on separate lines
- French comments in framework code
