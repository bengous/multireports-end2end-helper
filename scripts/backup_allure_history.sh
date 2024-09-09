#!/usr/bin/env bash
mkdir -p temp
mkdir -p "$BACKUP_HISTORY_DIR"

if curl --fail --silent --location --output temp/report.zip \
    --request GET "${URL_GITLAB}/api/v4/projects/${CI_PROJECT_ID}/jobs/artifacts/${CI_COMMIT_REF_NAME}/download?job=pages" \
    --header "Authorization: Bearer ${CI_DEPLOY_TOKEN}"; then
    echo "Artifact trouvé téléchargé"
    unzip -o temp/report.zip -d temp
    cp -rv temp/public/history/* "$BACKUP_HISTORY_DIR" || echo "Aucun historique trouvé dans l'artifact"
else
  echo "Aucun artifact trouvé lors de la requête"
fi

ls -1 "$BACKUP_HISTORY_DIR" || echo "Historique vide"