#!/bin/sh

# Requires gcloud to be set up correctly for the project (e.g. gcloud init)

cd $PWD/backend

#gcloud app create --project=mcc-fall-2019-g09 --region=europe-west

# Project needs to have below services enabled to do the endpoints deployment.
#gcloud services enable servicemanagement.googleapis.com
#gcloud services enable servicecontrol.googleapis.com
#gcloud services enable endpoints.googleapis.com

gcloud endpoints services deploy api.yaml

gcloud app deploy app.yaml
