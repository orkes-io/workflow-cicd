# Conductor Workflow Testing CI/CD

Workflow and Task metadata are deployed to the Conductor server via `api/metadata` endpoints. 

## Recommended approach for deploying your workflow metadata in production systems
1. Check in the JSON files for the workflow and task metadata into the source code repository.  e.g. src/main/resources/metadata
2. Use your normal development practice to review and merge the changes using Pull Requets in the production branch
3. use a deployment script to deploy workflows as part of your code deployment process.

## Deployment scripts
See [deploy_workflows.sh](src/deploy_workflows.sh) and [deploy_tasks.sh](src/deploy_tasks.sh) scripts for working scripts that deploys the workflows and tasks from the src/main/resources/metadata folder.