# multireports-end2end-helper

Java 21 E2E API testing framework for repeatable endpoint validation, fast CI feedback, and published Allure history.

| CI | Published Reports |
| --- | --- |
| [![CI](https://github.com/bengous/multireports-end2end-helper/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/bengous/multireports-end2end-helper/actions/workflows/ci.yml) | [Open the Allure reports index](https://bengous.github.io/multireports-end2end-helper/) |

Use the CI workflow for fast validation and the published Pages site to inspect historical report runs over time.

## Quick Start

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

- `CI` runs on `main` pushes and pull requests and is optimized for fast validation.
- `Publish Reports` runs manually and once a week on Monday at 07:00 UTC to rebuild and deploy the full GitHub Pages report site.
- Published reports are stored under `public/reports/<run-id>` and exposed through the Pages index at the site root.
- Previous published reports are restored from the Actions cache so history survives across deployments without a dedicated `pages` branch.
- GitHub Pages is refreshed by the scheduled or manual publish workflow, not by every push to `main`.

## Operator flow

1. Push to `main` to run fast validation in `CI`.
2. Use `Publish Reports` when you want GitHub Pages refreshed immediately.
3. Rely on the weekly scheduled `Publish Reports` run for routine report refresh.

<details>
<summary>Publish workflow inputs</summary>

The `Publish Reports` workflow supports:

- `api_url`: override the target API URL
- `suite_file`: override the TestNG suite XML file
- `debug_mode`: enable shell debug traces for the repo scripts
- `upload_test_artifacts`: control whether the publish run uploads Allure artifacts

</details>

<details>
<summary>Repository notes</summary>

- The fast `CI` workflow disables artifact upload by default via the top-level `UPLOAD_TEST_ARTIFACTS` variable in [.github/workflows/ci.yml](/home/b3ngous/projects/multireports-end2end-helper/.github/workflows/ci.yml). Set it back to `"true"` if you need artifacts on every push.
- `scripts/override_report_files.sh` injects the "BACK TO INDEX" link into the generated Allure HTML.
- `scripts/generate_report_specifics.sh` writes deployment metadata into `public/all_reports_info.json`.
- `scripts/backup_allure_history.sh` restores the latest published Allure `history/` directory before generating a new report.
- `scripts/bootstrap_legacy_reports.sh` is a temporary recovery mechanism used by the publish workflow to restore known legacy `dev` reports into the `main`-based Pages site.

</details>
