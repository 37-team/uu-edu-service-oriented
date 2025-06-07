from argparse import ArgumentParser

import uuid as uuid
from confluent_kafka import avro
from confluent_kafka.avro import AvroProducer
import logging
import json
from datetime import datetime, timezone


def create_instance_of(class_name: str):
    return getattr(globals()[class_name], class_name)()


class TestRunner:

    def __init__(self):
        arg_parser = ArgumentParser()
        arg_parser.add_argument("--topic", required=False, default="technical-events-data-high", help="Topic name")
        arg_parser.add_argument("--bootstrap-servers", required=False, default="localhost:9092",
                                help="Bootstrap server address")
        arg_parser.add_argument("--schema-registry", required=False, default="http://localhost:8081",
                                help="Schema Registry url")
        arg_parser.add_argument("--schema-file", required=False, default="schemas/technical_activity_event.avsc",
                                help="path to file of Avro schema to use")
        arg_parser.add_argument("--record-key", required=False, type=str,
                                help="Record key. If not provided, will be a random UUID")
        arg_parser.add_argument("--record-template-file", required=False, default="templates/technical_activity_event.json",
                                help="file for record value")
        arg_parser.add_argument("--debug", required=False, default="False")
        self.args = arg_parser.parse_args()
        logging.basicConfig(level=logging.INFO)
        if self.args.debug is not None and self.args.debug == "True":
            logging.basicConfig(level=logging.DEBUG)

    @property
    def args(self):
        return self._args

    @args.setter
    def args(self, value):
        self._args = value

    def load_json(self, template_path):
        with open(template_path, "r") as template:
            return json.loads(template.read())

    def prepare_data(self, data):
        data["id"] = str(uuid.uuid4())
        data["tracing"] = str(uuid.uuid4())
        data["time"] = datetime.now(timezone.utc).isoformat()
        data["topic"] = "technical-events-data-high"
        return data

    def create_single_record(self, template_path):
        data = self.load_json(template_path)
        return self.prepare_data(data)

    def load_avro_schema_from_file(self, schema_file):
        key_schema_string = """
        {"type": "string"}
        """

        key_schema = avro.loads(key_schema_string)
        value_schema = avro.load(schema_file)

        return key_schema, value_schema

    def send_record(self):

        # prepare schemas and config for Kafka producer
        key_schema, value_schema = self.load_avro_schema_from_file(self.args.schema_file)

        producer_config = {
            "bootstrap.servers": self.args.bootstrap_servers,
            "schema.registry.url": self.args.schema_registry
        }

        # create Kafka producer and data for sending
        producer = AvroProducer(producer_config, default_key_schema=key_schema, default_value_schema=value_schema)

        key = self.args.record_key if self.args.record_key else str(uuid.uuid4())
        value = self.create_single_record(self.args.record_template_file)

        # send data to Kafka
        try:
            producer.produce(topic=self.args.topic, key=key, value=value)
        except Exception as e:
            logging.error(f"""Exception while producing record value - {value} to topic - {self.args.topic}: {e}""")
        else:
            logging.info(f"""Successfully producing record value - {value} to topic - {self.args.topic}""")
        producer.flush()
        return value

    def run(self):
        try:
            self.send_record()
        except Exception as e:
            logging.error(e)


if __name__ == "__main__":
    runner = TestRunner()
    runner.run()
