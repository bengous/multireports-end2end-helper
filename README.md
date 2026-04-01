# multireports-end2end-helper

Java 21 E2E API testing framework using TestNG, RestAssured, and Allure.

## Local usage

Run the default suite:

```bash
mvn clean test -Dtarget.suite.xml.file=src/test/resources/testng/testng-jsonapi.xml
```

Run against a specific API URL:

```bash
mvn clean test \
  -Dapi.url=https://example.com/api \
  -Dtarget.suite.xml.file=src/test/resources/testng/testng-jsonapi.xml
```

Generate an Allure report after the test run:

```bash
mvn allure:report
```

## GitHub Actions

The repository now uses GitHub Actions instead of GitLab CI.

- CI runs on `dev` and `master` pushes and pull requests.
- GitHub Pages deployment is handled by the workflow on the repository default branch.
- The deployed site keeps published reports in `public/reports/<run-id>` and exposes an index at the site root.
- Previous published reports are restored from an Actions cache so the index and report history survive across deployments without a dedicated `pages` branch.

### Workflow inputs

The manual workflow supports:

- `api_url`: override the target API URL
- `suite_file`: override the TestNG suite XML file
- `debug_mode`: enable shell debug traces for the repo scripts

## Repository notes

- `scripts/override_report_files.sh` injects the "BACK TO INDEX" link into the generated Allure HTML.
- `scripts/generate_report_specifics.sh` writes deployment metadata into `public/all_reports_info.json`.
- `scripts/backup_allure_history.sh` restores the latest published Allure `history/` directory before generating a new report.
