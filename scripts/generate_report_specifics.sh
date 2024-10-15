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
## GitLab specific variables
CI_COMMIT_BRANCH=${CI_COMMIT_BRANCH:-$UNKNOWN}
CI_COMMIT_SHORT_SHA=${CI_COMMIT_SHORT_SHA:-$UNKNOWN}
CI_COMMIT_TITLE=${CI_COMMIT_TITLE:-$UNKNOWN}
CI_PIPELINE_ID=${CI_PIPELINE_ID:-$UNKNOWN}
GITLAB_USER_NAME=${GITLAB_USER_NAME:-$UNKNOWN}

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
    --arg pipelineId "$4" \
    --arg username "$5" \
    '.reports += [{
      branch: $branch,
      commitSha: $commitSha,
      commitTitle: $commitTitle,
      pipelineId: $pipelineId,
      username: $username
    }]' "$ALL_REPORTS_INFO" > "$tmp_file"
  mv "$tmp_file" "$ALL_REPORTS_INFO"
}

cd public || exit 1

initialize_reports_file_if_not_found

echo "Before update"
cat "$ALL_REPORTS_INFO"
if ! update_report "$CI_COMMIT_BRANCH" "$CI_COMMIT_SHORT_SHA" "$CI_COMMIT_TITLE" "$CI_PIPELINE_ID" "$GITLAB_USER_NAME"; then
  echo "Error updating JSON" >&2
  exit 1
fi
echo "------------------------------------------------------------------------------------------------"
echo "After update"
cat "$ALL_REPORTS_INFO"
