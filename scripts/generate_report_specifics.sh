#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
SCRIPTS_DEBUG_MODE=${SCRIPTS_DEBUG_MODE:-"disabled"}
if [ "$SCRIPTS_DEBUG_MODE" = "enabled" ]; then
  set -x
fi

cd public || exit 1

ALL_REPORTS_INFO="all_reports_info.json"
if [ ! -f "$ALL_REPORTS_INFO" ]; then
  echo "Creating empty reports file"
  echo '{
    "reports": []
  }' > "$ALL_REPORTS_INFO"
fi

UNKNOWN="unknown"
CI_COMMIT_BRANCH=${CI_COMMIT_BRANCH:-$UNKNOWN}
CI_COMMIT_SHORT_SHA=${CI_COMMIT_SHORT_SHA:-$UNKNOWN}
CI_COMMIT_TITLE=${CI_COMMIT_TITLE:-$UNKNOWN}
CI_PIPELINE_ID=${CI_PIPELINE_ID:-$UNKNOWN}
GITLAB_USER_NAME=${GITLAB_USER_NAME:-$UNKNOWN}

function update_report() {
  local tmp_file
  tmp_file=$(mktemp)
  jq ".reports += [{
    \"branch\": \"$1\",
    \"commitSha\": \"$2\",
    \"commitTitle\": \"$3\",
    \"pipelineId\": \"$4\",
    \"username\": \"$5\",
  }]" "$ALL_REPORTS_INFO" > "$tmp_file"
  mv "$tmp_file" "$ALL_REPORTS_INFO"
}

echo "Before update"
cat "$ALL_REPORTS_INFO"
if ! update_report "$CI_COMMIT_BRANCH" "$CI_COMMIT_SHORT_SHA" "$CI_COMMIT_TITLE" "$CI_PIPELINE_ID" "$GITLAB_USER_NAME"; then
  echo "Error updating JSON" >&2
  exit 1
fi
echo "------------------------------------------------------------------------------------------------"
echo "After update"
cat "$ALL_REPORTS_INFO"
