#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail

BACKUP_HISTORY_DIR=${BACKUP_HISTORY_DIR:-backup/history}
PUBLIC_DIR=${PUBLIC_DIR:-public}
REPORTS_DIR="${PUBLIC_DIR}/reports"

mkdir -p "$BACKUP_HISTORY_DIR"

if [ ! -d "$REPORTS_DIR" ]; then
  echo "No previously published reports directory found"
  exit 0
fi

latest_report_dir=$(
  find "$REPORTS_DIR" -mindepth 1 -maxdepth 1 -type d -printf '%f\n' \
    | sort -rn \
    | head -n 1
)

if [ -z "$latest_report_dir" ]; then
  echo "No previous report found"
  exit 0
fi

latest_history_dir="${REPORTS_DIR}/${latest_report_dir}/history"

if [ ! -d "$latest_history_dir" ]; then
  echo "No history directory found in latest report: ${latest_report_dir}"
  exit 0
fi

cp -rv "${latest_history_dir}/." "$BACKUP_HISTORY_DIR/"
ls -1 "$BACKUP_HISTORY_DIR"
