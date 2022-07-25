#!/usr/bin/env bash

echo "Create index patterns"
curl -XPOST 'http://localhost:5601/api/saved_objects/index-pattern/plc_data_idx' \
    -H 'kbn-xsrf: nevergonnagiveyouup' \
    -H 'Content-Type: application/json' \
    -d '{"attributes":{"title":"plc_data*","timeFieldName":"EVENT_TS"}}'

curl -XPOST 'http://localhost:5601/api/saved_objects/index-pattern/plc_stream_idx' \
    -H 'kbn-xsrf: nevergonnagiveyouup' \
    -H 'Content-Type: application/json' \
    -d '{"attributes":{"title":"plc_stream*","timeFieldName":"EVENT_TS"}}'

echo "Setting the index pattern as default"
curl -XPOST 'http://localhost:5601/api/kibana/settings' \
    -H 'kbn-xsrf: nevergonnagiveyouup' \
    -H 'content-type: application/json' \
    -d '{"changes":{"defaultIndex":"plc_stream_idx"}}'

