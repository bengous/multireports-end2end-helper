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
      <table>
        <thead>
          <tr>
            <th>Run</th>
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
  | reverse[]
  | "<tr><td><a href=\"reports/\(.runId)/index.html\">\(.runId)</a></td><td>\(.branch)</td><td><code>\(.commitSha)</code></td><td>\(.commitTitle)</td><td>\(.actor // .username // "unknown")</td></tr>"
' "$ALL_REPORTS_INFO" >> "$INDEX_FILE"

cat >> "$INDEX_FILE" <<'EOF'
        </tbody>
      </table>
    </main>
  </body>
</html>
EOF
