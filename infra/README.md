## Setup
1. Install docker
    1. `sudo apt-get update`
    2. `sudo apt install docker.io`
    3. `sudo systemctl start docker`
    4. `sudo systemctl enable docker`
2. `docker build . -t mcc:infra`
3. `docker run -it --name gcloud-config mcc:infra gcloud init`
4. Follow the wizard: go to the link, authenticate, enter the provided verification code, pick `mcc-fall-2019-g09` as the cloud project to use, and select `n` for default compute region.
5. `docker run -it mcc:infra firebase login:ci --no-localhost`
6. Follow the wizard and log in.
7. Save the output token in ./firebase/token.txt
8. `docker run --rm -it --volumes-from gcloud-config -v ${PWD}/..:/mcc mcc:infra bash` starts an interactive shell inside an mcc:infra container with the repository and gcloud config mounted. Deployments can thereafter be done, e.g. `cd /mcc && ./deploy.sh`


## Firebase deployment
Firebase can be deployed by running ``firebase deploy --only database --token `head -n 1 token.txt` `` inside the ./firebase directory.
