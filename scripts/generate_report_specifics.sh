#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
SCRIPTS_DEBUG_MODE=${SCRIPTS_DEBUG_MODE:-"disabled"}
if [ "$SCRIPTS_DEBUG_MODE" = "enabled" ]; then
  set -x
fi

# Variables
UNKNOWN="unknown"
ALL_REPORTS_INFO="all_reports_info.json"
REPORT_BRANCH=${REPORT_BRANCH:-${GITHUB_REF_NAME:-$UNKNOWN}}
REPORT_COMMIT_SHA=${REPORT_COMMIT_SHA:-${GITHUB_SHA:-$UNKNOWN}}
REPORT_COMMIT_TITLE=${REPORT_COMMIT_TITLE:-$UNKNOWN}
REPORT_RUN_ID=${REPORT_RUN_ID:-${GITHUB_RUN_ID:-$UNKNOWN}}
REPORT_ACTOR=${REPORT_ACTOR:-${GITHUB_ACTOR:-$UNKNOWN}}

function initialize_reports_file_if_not_found() {
  if [ ! -f "$ALL_REPORTS_INFO" ]; then
    echo "Creating empty reports file"
    echo '{
      "reports": []
    }' > "$ALL_REPORTS_INFO"
  fi
}

function update_report() {
  local tmp_file
  tmp_file=$(mktemp)
  jq --arg branch "$1" \
    --arg commitSha "$2" \
    --arg commitTitle "$3" \
    --arg runId "$4" \
    --arg actor "$5" \
    '
    .reports |= map(select(.runId != $runId and .pipelineId != $runId))
    | .reports += [{
      branch: $branch,
      commitSha: $commitSha,
      commitTitle: $commitTitle,
      runId: $runId,
      pipelineId: $runId,
      actor: $actor,
      username: $actor
    }]
    ' "$ALL_REPORTS_INFO" > "$tmp_file"
  mv "$tmp_file" "$ALL_REPORTS_INFO"
}

cd public || exit 1

initialize_reports_file_if_not_found

echo "Before update"
cat "$ALL_REPORTS_INFO"
if ! update_report "$REPORT_BRANCH" "$REPORT_COMMIT_SHA" "$REPORT_COMMIT_TITLE" "$REPORT_RUN_ID" "$REPORT_ACTOR"; then
  echo "Error updating JSON" >&2
  exit 1
fi
echo "------------------------------------------------------------------------------------------------"
echo "After update"
cat "$ALL_REPORTS_INFO"
