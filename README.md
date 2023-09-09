# MealPlanner

An app to help people choose their meal plans.

Users may select the number of different recipes that they want and generate a random menu - e.g. for weekly dinners. They can select different recipes that they want as part of the menu, and may search through all available recipes based on either the diet type (e.g. vegetarian) or by recipe/ingredient name.

Admins may further be able to create/modify existing recipes, and download all recipes/ingredients as a csv file.

The deployment of the app to AWS is currently WIP.

## Tech stack & what I learned in the process

### Api - Java 17 / Spring Boot 3

- Using App Listeners to perform operations on app start
- Manually implementing access token / refresh token authentication with a custom authentication filter
- Implementing Role-based access control
- Creating dynamic sql queries using criteria builder
- Custom annotations for validation
- Test containers for integration tests
- Usage of feature flags & conditional beans

### CLOUD

- Circle-CI
- Docker
- AWS

### UI - Typescript / React 18

- RTKQuery
- Material UI 5
- RTKQuery - baseQueryWithReauth
- Using Higher-Order-Components
- Using an nginx webserver for Docker container to reduce image size to ~45 mb
