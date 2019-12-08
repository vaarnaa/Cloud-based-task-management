## Setup
1. Install docker
    1. `sudo apt-get update`
    2. `sudo apt install docker.io`
    3. `sudo systemctl start docker`
    4. `sudo systemctl enable docker`
2. `docker build . -t mcc:infra`
3. `docker run -it --name gcloud-config mcc:infra gcloud init`
    follow the wizard:
    1. go to the link
    2. authenticate
    3. enter the provided verification code
    4. pick `mcc-fall-2019-g09` as the cloud project to use
    5. select `n` for default compute region.
4. `docker run -it mcc:infra firebase login:ci --no-localhost`
    1. log in according to the wizard
    2. save the output token in ./firebase/token.txt
5. `docker run --rm -it --volumes-from gcloud-config -v ${PWD}/..:/mcc mcc:infra bash` starts an interactive shell inside an mcc:infra container with the repository and gcloud config mounted. Deployments can thereafter be done, e.g. `cd /mcc && ./deploy.sh`
