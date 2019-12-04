## Setup
1. `docker build . -t mcc:infra`
2. `docker run -ti --name gcloud-config mcc:infra gcloud init`
3. Follow the wizard: go to the link, authenticate, enter the provided verification code, pick `mcc-fall-2019-g09` as the cloud project to use, and select `n` for default compute region.
4. `docker run --rm -it --volumes-from gcloud-config -v ${PWD}/..:/mcc mcc:infra bash` starts an interactive shell inside an mcc:infra container with the repository and gcloud config mounted. Deployments can thereafter be done, e.g. `cd /mcc/backend/ && gcloud endpoints services deploy api.yaml`
