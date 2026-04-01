#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

PUBLIC_DIR=${PUBLIC_DIR:-public}
ALL_REPORTS_INFO="${PUBLIC_DIR}/all_reports_info.json"
INDEX_FILE="${PUBLIC_DIR}/index.html"

mkdir -p "$PUBLIC_DIR"

if [ ! -f "$ALL_REPORTS_INFO" ]; then
  cat > "$ALL_REPORTS_INFO" <<'EOF'
{
  "reports": []
}
EOF
fi

cat > "$INDEX_FILE" <<'EOF'
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Allure Reports</title>
    <style>
      :root {
        color-scheme: light;
        --bg: #f5f7fb;
        --panel: #ffffff;
        --border: #d8e0ec;
        --text: #132238;
        --muted: #5f6f86;
        --link: #0b63ce;
      }

      * {
        box-sizing: border-box;
      }

      body {
        margin: 0;
        font-family: "Segoe UI", sans-serif;
        background: linear-gradient(180deg, #eef4fb 0%, var(--bg) 100%);
        color: var(--text);
      }

      main {
        max-width: 960px;
        margin: 0 auto;
        padding: 48px 20px 64px;
      }

      h1 {
        margin: 0 0 12px;
        font-size: 2rem;
      }

      p {
        margin: 0 0 24px;
        color: var(--muted);
      }

      .hint {
        margin: 0 0 16px;
        font-size: 0.95rem;
      }

      table {
        width: 100%;
        border-collapse: collapse;
        background: var(--panel);
        border: 1px solid var(--border);
        border-radius: 16px;
        overflow: hidden;
      }

      th,
      td {
        padding: 14px 16px;
        text-align: left;
        border-bottom: 1px solid var(--border);
      }

      th {
        background: #edf3fb;
        font-size: 0.92rem;
      }

      tr:last-child td {
        border-bottom: none;
      }

      tbody tr[data-href] {
        cursor: pointer;
      }

      tbody tr[data-href]:hover {
        background: #f6f9fe;
      }

      tbody tr[data-href]:focus-visible {
        outline: 2px solid var(--link);
        outline-offset: -2px;
        background: #eef5ff;
      }

      a {
        color: var(--link);
        text-decoration: none;
        font-weight: 600;
      }

      code {
        font-family: "SFMono-Regular", Consolas, monospace;
      }
    </style>
  </head>
  <body>
    <main>
      <h1>Allure reports</h1>
      <p>Published test reports available on GitHub Pages.</p>
      <p class="hint">Select a row to open that report.</p>
      <table>
        <thead>
          <tr>
            <th>Committed</th>
            <th>Branch</th>
            <th>Commit</th>
            <th>Title</th>
            <th>Actor</th>
          </tr>
        </thead>
        <tbody>
EOF

jq -r '
  .reports
  | sort_by([(.commitTimestamp != null and .commitTimestamp != "" and .commitTimestamp != "unknown"), .commitTimestamp])
  | reverse[]
  | . as $report
  | ($report.commitTimestamp // "unknown") as $timestamp
  | ($timestamp
      | if . == "unknown" or . == "" then "unknown"
        else (fromdateiso8601 | gmtime | strftime("%Y-%m-%d %H:%M UTC"))
        end) as $formattedTimestamp
  | "<tr data-href=\"reports/\($report.runId)/index.html\" tabindex=\"0\" role=\"link\" aria-label=\"Open report for \($report.commitTitle)\"><td>\($formattedTimestamp)</td><td>\($report.branch)</td><td><code>\($report.commitSha)</code></td><td>\($report.commitTitle)</td><td>\($report.actor // $report.username // "unknown")</td></tr>"
' "$ALL_REPORTS_INFO" >> "$INDEX_FILE"

cat >> "$INDEX_FILE" <<'EOF'
        </tbody>
      </table>
    </main>
    <script>
      document.querySelectorAll("tbody tr[data-href]").forEach((row) => {
        const navigate = () => {
          window.location.href = row.dataset.href;
        };

        row.addEventListener("click", navigate);
        row.addEventListener("keydown", (event) => {
          if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            navigate();
          }
        });
      });
    </script>
  </body>
</html>
EOF
