# Mission Service
This service is responsible for managing military missions. It provides functionalities to create, read, update, and delete missions, as well as assign assets and personnel to missions.

## Features
- Mission Management: Create, read, update, and delete missions.
- Asset Assignment: Assign and manage assets for missions.
- Personnel Assignment: Assign and manage personnel for missions.
- Search and Filter: Search and filter missions based on various criteria.
- Integration with Asset Service: Seamless integration with the Asset Service for asset information.
- Integration with User Service: Seamless integration with the User Service for personnel information.
- Dockerized: Easily deployable using Docker.
- Kubernetes Ready: Can be deployed in a Kubernetes cluster.

## API Documentation
The OpenAPI Specification for the Mission Service can be found at `/api-docs`. For swagger ui, visit `/swagger-ui.html`.

## Technologies Used
- Java 21
- Spring Boot
- Hibernate
- PostgreSQL
- Docker
- Kubernetes
- OpenAPI/Swagger

## Environment Variables
```
DB_USER=
DB_PASSWORD=
DB_NAME=
DB_URL=
API_GATEWAY_URL=
USER_SERVICE_URL=
ASSET_SERVICE_URL=
ENCRYPTION_KEY=
```