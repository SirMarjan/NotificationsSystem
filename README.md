# Notification app

## Backend

Start class: `src/main/java/pl/marcinsobanski/notificationssystem/infrastructure/Application.java`

Run with `docker` profile enable persistent database located in `/data/h2db`, by default in memory h2, can be changed in
`application-docker.yaml`

Url:

* swagger-ui http://localhost:8080/swagger-ui/index.html
* h2-console http://localhost:8080/h2-console
    * jdbc url and username in `application*.yaml` files

## Frontend

Required node 24+

```shell
cd .\NotificationsSystemFront
npm install
npm run dev
```

Url:

* http://localhost:5173/

## Run by (docker) compose

`\NotificationsSystemDocker\compose.yaml`

Url:

* backend http://localhost:8080
* frontend http://localhost:80
* mail http://localhost:8025
* swagger-ui http://localhost:8080/swagger-ui/index.html
* h2-console http://localhost:8080/h2-console
    * JDBC URL `jdbc:h2:file:/data/h2db`
    * user `admin`

Important:

* Path for backend api is hardcoded in `TemplateService.js`
