ROOT=http://localhost:8080

echo '--- GET /users'
curl \
    -X GET \
    ${ROOT}/users?param=value
echo ''

echo '--- PUT /users'
curl \
    -X PUT \
    -d '{"key1":"value1","key2":"value2"}' \
    -H "Content-Type: application/json" \
    ${ROOT}/users
echo ''
