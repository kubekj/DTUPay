# 02267 Software Development of Web Services: DTU Pay project

The project consists of 7 projects, where the `messaging-utilities-3.4` was provided by Hubert Baumeister. The remaining projects have been developed throughout the course with comments indicating the responsible people.

- A facade microservice in `facade` which offers an external REST interface and is used for end-to-end testing. This service communicates via message queues internally.
- An Account Management microservice in `account-mgmt-service` responsible for managing accounts in DTU Pay.
- A Payment Management microservice in `payment-mgmt-service` responsible for communication with the bank.
- A Report Management microservice in `report-mgmt-service` responsible for reporting services available to the manager, merchant, and customer.
- A Token Management microservice in `token-mgmt-service` responsible for issuing, consuming, and managing tokens.
- The end-to-end tests in `end-to-end-tests`.

The main `docker-compose.yml` file is in the `end-to-end-tests`.

The `build_and_run.sh` script is used to build, deploy and test the project.

In case of the end-to-end tests being stuck, run `docker-compose up -d` inside `end-to-end-tests` repeatedly until all services are running.