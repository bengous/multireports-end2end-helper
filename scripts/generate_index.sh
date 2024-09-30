#!/usr/bin/env bash

set -e

INDEX_FILE="public/index.html"

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

# Find all report directories
find public -mindepth 2 -type d -name "[0-9]*" | while read REPORT; do
  RELATIVE_PATH=${REPORT#public/}
  BRANCH=$(echo ${RELATIVE_PATH} | cut -d'/' -f1)
  PIPELINE_ID=$(echo ${RELATIVE_PATH} | cut -d'/' -f2)
  echo "    <li><a href=\"./${RELATIVE_PATH}/index.html\">Branch: ${BRANCH}, Pipeline: ${PIPELINE_ID}</a></li>" >> ${INDEX_FILE}
done

# End the HTML file
cat <<EOF >> ${INDEX_FILE}
  </ul>
</body>
</html>
EOF
