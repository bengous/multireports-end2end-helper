#!/usr/bin/env bash

cd public || exit 1 # 404

echo "
<html>
  <body>
    <h1>Allure reports</h1>
    <ul>
" > index.html

for dir in */; do
    # https://unix.stackexchange.com/questions/86722/how-do-i-loop-through-only-directories-in-bash#answer-86724
    [ -L "${dir%/}" ] && continue
    echo "
    <li><a href='/$dir/index.html'>Report $dir</a></li>
    " >> index.html
done

echo "
    </ul>
  </body>
</html>
" >> index.html