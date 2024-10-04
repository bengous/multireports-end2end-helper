#!/usr/bin/env bash

echo "
<html>
  <body>
    <h1>Allure reports</h1>
    <ul>
" > public/index.html

for dir in */; do
  if [ -d "public/$dir" ] && [ "$dir" != "index.html" ]; then
    echo "
    <li><a href='/$dir/index.html'>Report $dir</a></li>
    " >> public/index.html
  fi
done

echo "
    </ul>
  </body>
</html>
" >> public/index.html