#!/bin/bash
set -eu

ROOT=http://localhost:8080
CURL="/usr/bin/curl -sS" # -s silent -S show-errors
JQ="/usr/bin/jq -r" # -r raw-output

# assumed token for temp@example.local user
auth="eyJpc3N1ZXIiOiJUT0tFTl9JU1NVRVIiLCJpZCI6Ikl3M0JtS2V6cjVoRWw5QUFIM2RkVm1IQWxzOTIiLCJlbWFpbCI6InRlbXBAZXhhbXBsZS5sb2NhbCJ9"
ret_val="" # saves the output of curl

get() {
    PATH=$1

    echo "--- GET ${PATH}"
    ret_val=$(\
        ${CURL} \
            -X GET \
            -H "X-Endpoint-API-UserInfo: ${auth}" \
            ${ROOT}${PATH} \
    )
    echo $ret_val
    echo ''
}

delete() {
    PATH=$1

    echo "--- DELETE ${PATH}"
    ret_val=$(
        ${CURL} \
            -X DELETE \
            -H "X-Endpoint-API-UserInfo: ${auth}" \
            ${ROOT}${PATH} \
    )
    echo $ret_val
    echo ''
}

put() {
    PATH=$1
    DATA=$2

    echo "--- PUT ${PATH}"
    ret_val=$(\
        ${CURL} \
            -X PUT \
            -d "${DATA}" \
            -H "Content-Type: application/json" \
            -H "X-Endpoint-API-UserInfo: ${auth}" \
            ${ROOT}${PATH}\
    )
    echo $ret_val
    echo ''
}

post() {
    PATH=$1
    DATA=$2

    echo "--- POST ${PATH}"
    ret_val=$(\
        ${CURL} \
            -X POST \
            -d "${DATA}" \
            -H "Content-Type: application/json" \
            -H "X-Endpoint-API-UserInfo: ${auth}" \
            ${ROOT}${PATH}\
    )
    echo $ret_val
    echo ''
}


## ACTUAL TESTING

post "/project" '{"name":"project_name1","type":"group", "description":"desc","deadline":"Wed, 14 Jun 2020 07:00:00 GMT"}'
PROJECT_ID=$(echo "${ret_val}" | ${JQ} '.project_id')

get "/project/${PROJECT_ID}"

get "/projects"

get "/project/${PROJECT_ID}/attachments"

post "/project/${PROJECT_ID}/members/" '{"members":["ICXa2Dr4LeZTNyN9tv60pNXMjqC3","QM9HKPoy9KffdUmP4JIWOem6zC93"]}'

get "/project/${PROJECT_ID}"

delete "/project/${PROJECT_ID}"
