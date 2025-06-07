# cloud-ai-read-model

# INFO

### Purpose
A [Read Model](https://cqrs.nu/tutorial/cs/03-read-models) service for ai-data-services.
The main purpose is to enable __efficient read operations__. In a nutshell, it does the following:
* Listens for changes in the primary FSM data stores
* Saves all changes in the own database 
* Provides APIs  for read queries for aggregates such as Jobs and Resources

For further documentation see [metrics](docs/METRICS.md) and [all the things kafka & events](docs/EVENTS_AND_SNAPSHOTS.md).

In some situations were events 
### Maintainers
- https://github.tools.sap/orgs/coresystemsFSM/teams/sagittarius

### Pipeline
- https://bamboo.coresuite.com/browse/PIP-CLOUDAIREAD

### Monitoring
- [Grafana](http://grafana.et-1.coreinfra.io/d/backend_cloud_microservices/backend-microservices?orgId=1&var-app=cloud-poc&var-pod=All) [docs](https://github.tools.sap/coresystemsFSM/documentation/wiki/Monitoring)
- [Kibana logs](https://elks-es.dev.coresuite.com/goto/32d87d5e45c257fc6dd587df86cbb01a)
- [Tracing](http://jaeger.et-1.coreinfra.io/search?end=1564063719071000&limit=20&lookback=1h&maxDuration&minDuration&service=cloud-ai-read-model&start=1564060119071000) [docs](https://github.tools.sap/coresystemsFSM/documentation/wiki/Logging-and-tracing)

### Eventing
- topics
- events
- [docs] https://github.tools.sap/coresystemsFSM/documentation/wiki/Events-and-messaging

# CONTRIBUTE

### Dependencies
- database (see vault for credentials)
- vault [docs] https://github.tools.sap/coresystemsFSM/documentation/wiki/Vault

### How to run `cloud-ai-read-model` locally

(warning, it is quite CPU intensive to run all the containers)
1. Start db and kafka, from cloud-ai-read-model dir `../local-dockers/docker/start_all_dockers_locally.sh ms kafka`
2. Create intellij configuration (e.g. by copy-paste existing) and set profile `local`
3. Export required [environment variables](#Environment-variables)
4. To enable indexing locally for all tenants hardcode `return true;` in `TenantBasedEventFilter.shouldProcess`
5. _Optional_: Run script in `scripts/start_jaeger.sh` to start jaeger tracing locally in a docker container
6. Run app using intellij (command line is  currently cumbersome)

_for MAC M1 chip_:<br>
Installation on M1 was not tested yet by the team, but there are documentation regarding this task:
 * [GH thread with alternatives in some comments](https://github.com/confluentinc/confluent-kafka-go/issues/591)
 * [Video with step-by-step installation](https://www.youtube.com/watch?v=_u_aWbm-ZJ0)```
#### Connect to the DB

Its is useful, for example if you want to answer question such as *how can I find a person ID?*
the steps are the following:
1. connect to your local dB with `docker exec -it  postgres-ms-test-arm64  /bin/sh` (can check the name of your db container with `docker ps`)
2. inside the container, go to pg user: `su - postgres`
3. check if postgres is running and has the dbs: `$ psql --list`  it should show a list ob databases
4. go do the database: `$ psql cloud-ai-read-model`
5. go to your schema: `> set search_path to a111_222`;  (replace 111 and 222 with account and company names)
6. check the person table: `> select * from person`;

you can also connect with GUI interface, just follow the steps in this short clip:
![](docs/connect-local-db-intelij.gif)

### Environment variables 

* `OAUTH2_CLOUD_AI_READ_MODEL_CLIENT_SECRET` **(required)**: get from vault under `vault/secrets/oauth2/show/cloud-ai-read-model`. It's required to make calls to upstream services.
* `KAFKA_GROUP_ID` **(required)**: when locally running set to `cloud-ai-read-model-<ldap-user>` or otherwise you will steal messages from read model on QT!
* `KAFKA_BOOTSTRAP_SERVERS`:  to use kafka from local docker set to `localhost:9092` or by default qt kafka broker is used
* `KAFKA_SCHEMA_REGISTRY`:  to use kafka from local docker set to `http://localhost:8081` or by default qt schema registry is used
* `CLOUD-HOLIDAY-SERVICE-HOST`: by default holiday service on qt is used
* `CLOUD_TECHNICAL_EVENTS_SERVICE_HOST`: by default tech events service on qt is used
* `API_INTERNAL_TECHEVENTS_USERNAME` **(required)**: username for tech events service (can be found in pod secrets via k8s console)
* `API_INTERNAL_TECHEVENTS_PASSWORD` **(required)**: password for tech events service (can be found in pod secrets via k8s console)


**How to publish test record to Kafka**

1. Make sure you run the service[ai-read-model], Kafka, PostgreSQL, and all the supporting stuff
2. `cd scripts/kafka_producer_tool`
3. `bash produce_event.sh`

### Building
Use the following command: `mvn clean enforcer:enforce test -Pintegration package install -U`

It will clean, check for dependencies conflict and finally run integration tests.

To run the tests only, the command is:
`mvn clean verify -P integration -D noDocker`

#### Build docker image
Use the following command to build docker image:
```
mvn clean compile jib:dockerBuild
```

The base Jib plugin configuration provided in `cloud-microservices-parent` module as plugin management.

Required for docker image building env variables are following:
- `APP_CLASSPATH` - app classpath (__default value__ specified in `cloud-microservices-parent`)
- `APP_MAIN_CLASS` - app main class

It's possible to extend default image JVM flags with following env variables:
- `JVM_EXTRA_ARGS`
- `JVM_EXTRA_PROPERTIES`

You can find more configuration details in:
- [Base docker image docs](https://github.tools.sap/coresystemsFSM/dockerfiles/tree/master/docker-base-java)
- [Jib maven plugin docs](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin)


### Prometheus metrics from starter-events

[goto METRICS.md](docs/METRICS.md)