![NiMFID](https://github.com/yusuf-Ao/nimfid-api/assets/59228438/6bcd7c50-812e-4b47-8dc6-814727b3ce80)
# Nigerian Microfinance Institution Directory - An abstract project
This is an abstract project built just for fun. The idea is to have a one directory that holds detailed information of various microfinance organizations. 
Features and functionalities includes:
- JWT Token Stateless Authentication and authorization
- User Managemnet (CRUD Operations)
- Organization Management (CRUD Operations)
- Maps Geolocation and visualizer
- Dashboard analytics and reporting
- Event based email notifications and message queue
---

# NIMFID-API

This repo contains the backend (API) of the project, designed using the microservice architectural pattern.

![NIMFID-Architecture](https://github.com/yusuf-Ao/nimfid-api/assets/59228438/18d39059-8683-4fdb-9526-f212ea02324b)

The API consist of three service;
- Persistense Service: Handles Authentication, Authorization, Fraud check and User Integrity.
- Model Service: Handles the core logic of performing different CRUD operations of managing different organizations in the system
- Notification Service: Uses event based pattern for handling and delivering notifications to different channels.

Tools and Technologies used are:
- JAVA language and springboot framework
- Maven as build tool
- JWT Token
- Docker for containerization
- RabbitMq as a message queue
- Zipkin for distributed tracing
- Springcloud Gateway serving as API Gateway and loadbalancer
- Spring Open feign for microservice internal communication
- Eureka as a service registry and service discovery
- MySQL for database
- JAVA SMTP for mailing
- EMAIL and Discord server as message consumer
- Swagger springdoc using the openapi specification for API Documentation
- Google Maps Geolocation API

![NIMFID-RabbitMQ drawio](https://github.com/yusuf-Ao/nimfid-api/assets/59228438/c6d2e37d-411f-449f-864a-cd590594bf02)


