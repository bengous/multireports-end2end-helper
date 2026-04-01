# multireports-end2end-helper

Java 21 E2E API testing framework using TestNG, RestAssured, and Allure.

## Reports

Published reports index:

[Allure Reports Table](https://bengous.github.io/multireports-end2end-helper/)

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

- `CI` runs on `main` pushes and pull requests and is optimized for fast validation.
- `Publish Reports` is a manual workflow that rebuilds and deploys the full GitHub Pages report site.
- The deployed site keeps published reports in `public/reports/<run-id>` and exposes an index at the site root.
- Previous published reports are restored from an Actions cache so the index and report history survive across deployments without a dedicated `pages` branch.
- The repository temporarily keeps a legacy report bootstrap step, but it now runs only in the manual publish workflow to republish historical pre-`main` reports from preserved GitHub Actions artifacts.
- GitHub Pages is refreshed by the manual publish workflow, not by every push to `main`.

### Publish workflow inputs

The `Publish Reports` workflow supports:

- `api_url`: override the target API URL
- `suite_file`: override the TestNG suite XML file
- `debug_mode`: enable shell debug traces for the repo scripts
- `upload_test_artifacts`: control whether the publish run uploads Allure artifacts

## Repository notes

- The fast `CI` workflow keeps artifact upload enabled by default via the top-level `UPLOAD_TEST_ARTIFACTS` variable in [.github/workflows/ci.yml](/home/b3ngous/projects/multireports-end2end-helper/.github/workflows/ci.yml).
- `scripts/override_report_files.sh` injects the "BACK TO INDEX" link into the generated Allure HTML.
- `scripts/generate_report_specifics.sh` writes deployment metadata into `public/all_reports_info.json`.
- `scripts/backup_allure_history.sh` restores the latest published Allure `history/` directory before generating a new report.
- `scripts/bootstrap_legacy_reports.sh` is a temporary recovery mechanism that restores known legacy `dev` reports into the `main`-based Pages site.
