#!/bin/bash

# Script that deploys all infra/backend resources. To run, setup docker as per instructions in ./infra/README.md,
# then run the script inside the container.

set -o pipefail
set -e

REPOSITORY_ROOT=`realpath $(dirname "$0")`

echo '
--- Creating app engine ---
' # app create fails if an app already exists. Ignore error
gcloud app create --project=mcc-fall-2019-g09 --region=europe-west 2>/dev/null || true

echo '
--- Enabling required services ---
'
gcloud services enable servicemanagement.googleapis.com
gcloud services enable servicecontrol.googleapis.com
gcloud services enable endpoints.googleapis.com

echo '
--- Deploying firebase ---
'
cd $REPOSITORY_ROOT/infra/firebase
firebase deploy --only database --token `head -n 1 token.txt`


echo '
--- Deploying endpoints ---
'
cd $REPOSITORY_ROOT/backend
gcloud endpoints services deploy api.yaml

echo '
--- Deploying app engine ---
'
cd $REPOSITORY_ROOT/backend
yes | gcloud app deploy app.yaml
