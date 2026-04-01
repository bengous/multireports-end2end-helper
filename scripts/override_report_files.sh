#!/usr/bin/env bash

if [ ! -d "$ALLURE_REPORT_DIR" ]; then
  echo "ALLURE_REPORT_DIR is not a valid directory"
  exit 1
fi

# shellcheck disable=SC2164
cd "$ALLURE_REPORT_DIR"

INDEX_FILE="index.html"
if [ ! -f "$INDEX_FILE" ]; then
  echo "INDEX_FILE does not exist"
  exit 1
fi

OVERRIDE_WITH=$(cat <<EOF
    <script defer>
      console.log(0);
      let retries = 0;
      const maxRetries = 20;
      const intervalId = setInterval(function() {
        const side = document.querySelector('div.side-nav__head');

        if (side) {
            console.log(1);
            side.innerHTML = \`
                <div class="side-nav__head" style="display: flex; flex-direction: column">
                    <div style="display: flex;align-items: center;justify-content: center;margin-bottom: 8px;cursor: pointer;">
                        <a href="../../." style="all: unset;color: white;font-weight: bolder;border: 1px solid white; padding: 4px">BACK TO INDEX</a>
                    </div>
                    <a href="." class="side-nav__brand" data-ga4-event="home_click">
                        <span class="side-nav__brand-text">Allure</span>
                    </a>
                </div>
               \`;

           // si jamais on change de page alors le lien disparait \- petit hack pour refaire l\'injection
           window.navigation.addEventListener("navigate", (event) => {
            console.log(2);
               side.innerHTML = \`
                   <div class="side-nav__head" style="display: flex; flex-direction: column">
                       <div style="display: flex;align-items: center;justify-content: center;margin-bottom: 8px;cursor: pointer;">
                           <a href="../../." style="all: unset;color: white;font-weight: bolder;border: 1px solid white; padding: 4px">BACK TO INDEX</a>
                       </div>
                       <a href="." class="side-nav__brand" data-ga4-event="home_click">
                           <span class="side-nav__brand-text">Allure</span>
                       </a>
                   </div>
                  \`;
           });

          clearInterval(intervalId);
        } else if (retries >= maxRetries) {
          clearInterval(intervalId);
        }

        retries++;
      }, 500);
    </script>
</body>
EOF
)

sed "s|</body>|PLACEHOLDER|g" "$INDEX_FILE" > index.html.tmp

awk -v replacement="$OVERRIDE_WITH" '{gsub(/PLACEHOLDER/, replacement)}1' index.html.tmp > index.html
