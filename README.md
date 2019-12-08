# mcc-fall-2019-g09

## Backend
Backend (located under `./backend`) contains the app engine and endpoints configurations, app.yaml and api.yaml, respectively. Start with `npm install` and run server with `npm start`.

Testing and linting don't require a server running:
```
npm test
npm run lint -- --fix
```

VSCode will automatically use its integrated eslint plugin. Be sure to fix
issues manually or run with `--fix` before commit.

* [Coverage report](https://mcc.zi.fi/lcov-report/)

### Deployment
Set up docker as described in infra/README.md and run ./deploy.sh.

## Frontend

Automatically built app APKs are available from
[CI Pipelines](https://version.aalto.fi/gitlab/CS-E4100/mcc-fall-2019-g09/pipelines)
as Artifacts download about 15 minutes after each commit.
