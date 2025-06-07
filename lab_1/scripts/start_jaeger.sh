#!/bin/sh
# Running jaeger locally in docker could be included as part of the local-dockers setup

jaeger_running=$(docker ps -f name=jaeger -q)
jaeger_stopped=$(docker ps -f name=jaeger -q -a)

if [ -z "$jaeger_running" ]
then
  if [ -n "$jaeger_stopped" ]
  then
    echo "Restarting jaeger"
    docker restart jaeger
  else
    echo "Running new jaeger container"
    docker run -d --name jaeger \
      -e COLLECTOR_ZIPKIN_HOST_PORT=:9411 \
      -p 5775:5775/udp \
      -p 6831:6831/udp \
      -p 6832:6832/udp \
      -p 5778:5778 \
      -p 16686:16686 \
      -p 14250:14250 \
      -p 14268:14268 \
      -p 14269:14269 \
      -p 9411:9411 \
      jaegertracing/all-in-one:latest
  fi
fi
echo "jaeger is running, visit http://localhost:16686/search"