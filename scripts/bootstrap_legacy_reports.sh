#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

PUBLIC_DIR=${PUBLIC_DIR:-public}
REPO_SLUG=${REPO_SLUG:-}

if [ -z "$REPO_SLUG" ]; then
  echo "REPO_SLUG is required" >&2
  exit 1
fi

mkdir -p "$PUBLIC_DIR/reports"

legacy_run_ids=(
  "23857422450"
  "23857547710"
  "23857630512"
)

for run_id in "${legacy_run_ids[@]}"; do
  report_dir="${PUBLIC_DIR}/reports/${run_id}"

  if [ -d "$report_dir" ]; then
    echo "Legacy report ${run_id} already present"
    continue
  fi

  tmp_dir="$(mktemp -d)"
  trap 'rm -rf "$tmp_dir"' EXIT

  echo "Restoring legacy report ${run_id}"
  gh run download "$run_id" --repo "$REPO_SLUG" -D "$tmp_dir"

  artifact_report_dir="$(find "$tmp_dir" -type d -path '*/site/allure-maven-plugin' | head -n 1)"
  if [ -z "$artifact_report_dir" ]; then
    echo "Allure report directory not found for run ${run_id}" >&2
    exit 1
  fi

  mkdir -p "$report_dir"
  cp -R "${artifact_report_dir}/." "$report_dir/"

  run_payload="$(gh api "repos/${REPO_SLUG}/actions/runs/${run_id}")"
  export REPORT_BRANCH
  REPORT_BRANCH="$(printf '%s' "$run_payload" | jq -r '.head_branch')"
  export REPORT_COMMIT_SHA
  REPORT_COMMIT_SHA="$(printf '%s' "$run_payload" | jq -r '.head_sha[0:7]')"
  export REPORT_COMMIT_TITLE
  REPORT_COMMIT_TITLE="$(printf '%s' "$run_payload" | jq -r '.head_commit.message')"
  export REPORT_COMMIT_TIMESTAMP
  REPORT_COMMIT_TIMESTAMP="$(printf '%s' "$run_payload" | jq -r '.head_commit.timestamp // .created_at // "unknown"')"
  export REPORT_RUN_ID="$run_id"
  export REPORT_ACTOR
  REPORT_ACTOR="$(printf '%s' "$run_payload" | jq -r '.actor.login')"

  chmod +x ./scripts/generate_report_specifics.sh
  ./scripts/generate_report_specifics.sh

  rm -rf "$tmp_dir"
  trap - EXIT
done
