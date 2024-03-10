# opensearch

Using Docker compose

```bash
# kind
helm repo add opensearch https://opensearch-project.github.io/helm-charts/
helm repo update
helm search repo opensearch
helm install my-deployment opensearch/opensearch

# use this one for Dev
https://opensearch.org/docs/latest/install-and-configure/install-opensearch/docker/

# use this for secure https
https://opensearch.org/downloads.html

# Bring up OpenSearch
docker-compose up
docker-compose up -d # bg process


# opensearch dash
http://localhost:5601/

# username password : admin admin

docker-compose down -v # clear data during shutdown

```

Interacting with Cluster

```py
# https://opensearch.org/docs/latest/clients/python-low-level/
'''
pip install opensearch-py
pip install opensearch-py-ml
pip3 install requests
pip3 install deprecated
pip3 install pandas==2.0.3
'''

from opensearchpy import OpenSearch

host = "localhost"
port = 9200
auth = ("admin", "admin")  # For testing only. Don't store credentials in code.
ca_certs_path = "/full/path/to/root-ca.pem"  # Provide a CA bundle if you use intermediate CAs with your root CA.

# Create the client with SSL/TLS enabled, but hostname verification disabled.
client = OpenSearch(
    hosts=[{"host": host, "port": port}],
    http_compress=True,  # enables gzip compression for request bodies
    http_auth=auth,
    use_ssl=True,
    verify_certs=True,
    ssl_assert_hostname=False,
    ssl_show_warn=False,
    ca_certs=ca_certs_path,
)

```

## ML features

https://opensearch.org/docs/latest/ml-commons-plugin/pretrained-models/#supported-pretrained-models

```py
# pip install opensearch-py-ml
PUT _cluster/settings
{
  "persistent": {
    "plugins": {
      "ml_commons": {
        "only_run_on_ml_node": "false",
        "model_access_control_enabled": "true",
        "native_memory_threshold": "99"
      }
    }
  }
}

curl http://localhost:9200 -ku 'admin:admin'

# enable ML

curl -X PUT "http://localhost:9200/_cluster/settings" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "persistent": {
    "plugins": {
      "ml_commons": {
        "only_run_on_ml_node": "false",
        "model_access_control_enabled": "true",
        "native_memory_threshold": "99"
      }
    }
  }
}
'

# register model group

curl -X POST "http://localhost:9200/_plugins/_ml/model_groups/_register" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "name": "local_model_group",
  "description": "A model group for local models"
}
'

# {"model_group_id":"j39Qlo0BqQ0eGGmEB34x","status":"CREATED"}

# Step 2: Register a local OpenSearch-provided model

curl -X POST "http://localhost:9200/_plugins/_ml/models/_register" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "name": "huggingface/sentence-transformers/msmarco-distilbert-base-tas-b",
  "version": "1.0.2",
  "model_group_id": "Z1eQf4oB5Vm0Tdw8EIP2",
  "model_format": "TORCH_SCRIPT"
}
'


# Test if Model Works by invoking API
curl -X POST "http://localhost:9200/_plugins/_ml/_predict/text_embedding/kX9Qlo0BqQ0eGGmEx36w" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "text_docs":[ "today is sunny"],
  "return_number": true,
  "target_response": ["sentence_embedding"]
}
'

# Get Sparse Encoding
curl -X POST "http://localhost:9200/_plugins/_ml/_predict/sparse_encoding/kX9Qlo0BqQ0eGGmEx36w" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "text_docs":[ "today is sunny"]
}
'

curl -X POST "http://localhost:9200/_plugins/_ml/_predict/sparse_encoding/kX9Qlo0BqQ0eGGmEx36w" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "text_docs":[ "today is sunny"]
}
' | jq '.inference_results[0].output[0].dataAsMap.response'

# https://opensearch.org/docs/latest/search-plugins/semantic-search/

# now we can set up a Text Embedding Model , which lets us do Semantic Search
```

#### Setting up a Text Embedding Model

Ingest Pipeline : We can set a default Model to an Index so for new documents - Embeddings are Mapped to it by default

Info about text embedding:
https://opensearch.org/docs/latest/ingest-pipelines/processors/text-embedding/

Text Embedding Processor Syntax

```json
// field_map : K/V pairs that specify the mapping of a text field to a vector field.

// field_map.<input_field> : The name of the field from which to obtain text for generating text embeddings.

// field_map.<vector_field> : The name of the vector field in which to store the generated text embeddings.

{
  "text_embedding": {
    "model_id": "<model_id>",
    "field_map": {
      "<input_field>": "<vector_field>"
    }
  }
}
```

Step 1 : Create an Ingest Pipeline

```json
PUT /_ingest/pipeline/nlp-ingest-pipeline
{
  "description": "A text embedding pipeline",
  "processors": [
    {
      "text_embedding": {
        "model_id": "bQ1J8ooBpBj3wT4HVUsb",
        "field_map": {
          "passage_text": "passage_embedding"
        }
      }
    }
  ]
}


```

Step 2: Test the Pipeline

```json
POST _ingest/pipeline/nlp-ingest-pipeline/_simulate
{
  "docs": [
    {
      "_index": "testindex1",
      "_id": "1",
      "_source":{
         "passage_text": "hello world"
      }
    }
  ]
}

// Confirm if passage_embedding was generated along with the passage_text


```

#### Semantic Search and Ingest Pipeline for Indexes

1. Create an Index for ingestion

```json
// passage_text should be marked as text type
// passage_embedding must be mapped as a k-NN vector with dims matching Model

PUT /my-nlp-index
{
  "settings": {
    "index.knn": true,
    "default_pipeline": "nlp-ingest-pipeline"
  },
  "mappings": {
    "properties": {
      "id": {
        "type": "text"
      },
      "passage_embedding": {
        "type": "knn_vector",
        "dimension": 768,
        "method": {
          "engine": "lucene",
          "space_type": "l2",
          "name": "hnsw",
          "parameters": {}
        }
      },
      "passage_text": {
        "type": "text"
      }
    }
  }
}

```

2. Insert Documents

```json
PUT /my-nlp-index/_doc/1
{
  "passage_text": "Hello world",
  "id": "s1"
}

PUT /my-nlp-index/_doc/2
{
  "passage_text": "Hi planet",
  "id": "s2"
}



```

3. Perform neural Search

```json

GET /my-nlp-index/_search
{
  "_source": {
    "excludes": [
      "passage_embedding"
    ]
  },
  "query": {
    "bool": {
      "filter": {
         "wildcard":  { "id": "*1" }
      },
      "should": [
        {
          "script_score": {
            "query": {
              "neural": {
                "passage_embedding": {
                  "query_text": "Hi world",
                  "model_id": "bQ1J8ooBpBj3wT4HVUsb",
                  "k": 100
                }
              }
            },
            "script": {
              "source": "_score * 1.5"
            }
          }
        },
        {
          "script_score": {
            "query": {
              "match": {
                "passage_text": "Hi world"
              }
            },
            "script": {
              "source": "_score * 1.7"
            }
          }
        }
      ]
    }
  }
}
```

This Query is Verbose so we should set Defaults

#### Setting a default model on an index or field

A neural query requires a model ID for generating vector embeddings. To eliminate passing the model ID with each neural query request, you can set a default model on a k-NN index or a field.

```json
// Create a Search pipeline with a neural_query_enricher request processor.

// We can set a different model per field too - but we wont need it

PUT /_search/pipeline/default_model_pipeline
{
  "request_processors": [
    {
      "neural_query_enricher" : {
        "default_model_id": "bQ1J8ooBpBj3wT4HVUsb",
        "neural_field_default_id": {
           "my_field_1": "uZj0qYoBMtvQlfhaYeud",
           "my_field_2": "upj0qYoBMtvQlfhaZOuM"
        }
      }
    }
  ]
}

// set the default Model for the index

PUT /my-nlp-index/_settings
{
  "index.search.default_pipeline" : "default_model_pipeline"
}

// now Query - and we get results

GET /my-nlp-index/_search
{
  "_source": {
    "excludes": [
      "passage_embedding"
    ]
  },
  "query": {
    "neural": {
      "passage_embedding": {
        "query_text": "Hi world",
        "k": 100
      }
    }
  }
}
```
