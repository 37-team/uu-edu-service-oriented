# Kafka Events Publisher Tool

# INFO

#### Purpose
Tool for publishing events to Kafka 

#### How to run
Prerequisite:
`confluentinc/cp-schema-registry` and `confluentinc/cp-server` (Kafka) should be run  

How to start up Kafka:

- `cd cloud-microservices/local-dockers/docker` 
- run `start_all_dockers_locally.sh kafka` or `start_all_dockers_locally.sh ms kafka` (if you need DB)

How to start up Kafka Events Publisher Tool:  
1. cd `kafka_producer_tool` 
2. `. ./script_lifecycle_install.sh`
3. (if something went wrong on previous step) `source ./env/bin/activate` and `pip3 install -r requirements.txt`
4. `bash produce_event.sh` or `python3 runner.py`


when you test Events consumption locally:
- ensure, that EventFilter does not skip Events, that you publish


#### Parameters

If you want to override existing parameters:

- `--topic`, default is `technical-events-data-high` 
- `--bootstrap-servers`, Kafka broker address, default is `localhost:9092`
- `--schema-registry`, default is `http://localhost:8081`
- `--schema-file`, default is `schemas/technical_activity_event.avsc`
- `--record-key` message key's schema. Kafka messages are key/value pairs. What you set the key is up to you and the requirements of what you are implementing.
Default is: `{"type": "string"}`
- `--record-template-file` - path to the json file, which[the path] will be passed to the `./data/DataCreator.py#create_single(template_path)`