#!/usr/bin/env bash

set -e

# Find all report directories
REPORTS=$(find public -type d -mindepth 2 -maxdepth 2)

# Start the HTML file
cat <<EOF > ${INDEX_FILE}
<!DOCTYPE html>
<html>
<head>
  <title>Allure Reports Index</title>
</head>
<body>
  <h1>Allure Reports Index</h1>
  <ul>
EOF

# Add links to the reports
for REPORT in ${REPORTS}; do
  BRANCH=$(basename $(dirname ${REPORT}))
  PIPELINE_ID=$(basename ${REPORT})
  echo "    <li><a href=\"./${BRANCH}/${PIPELINE_ID}/\">Branch: ${BRANCH}, Pipeline: ${PIPELINE_ID}</a></li>" >> ${INDEX_FILE}
done

# End the HTML file
cat <<EOF >> ${INDEX_FILE}
  </ul>
</body>
</html>
EOF
