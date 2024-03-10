# ml os

```bash
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
# enable Conversation memory
curl -X PUT "http://localhost:9200/_cluster/settings" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "persistent": {
    "plugins.ml_commons.memory_feature_enabled": true
  }
}
'

# create a Conversation and save the Conversation ID

curl -X POST "http://localhost:9200/_plugins/_ml/memory/conversation" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "name": "Example conversation"
}
'

# conversation ID : ljfhxI0BGiW7YW8yp4Eo

# create an interaction using the conversation_id and save the interaction ID

curl -X POST "http://localhost:9200/_plugins/_ml/memory/conversation/ljfhxI0BGiW7YW8yp4Eo" -H 'Content-Type: application/json' -u 'admin:admin' -d '
{
  "input": "How do I make an interaction?",
  "prompt_template": "Hello OpenAI, can you answer this question? Heres some extra info that may help. [INFO] \\n [QUESTION]",
  "response": "Hello, this is OpenAI. Here is the answer to your question.",
  "origin": "MyFirstOpenAIWrapper",
  "additional_info": "Additional text related to the answer. A JSON or other semi-structured response"
}'


# Interaction id: lzfjxI0BGiW7YW8ydIF0

# Enable RAG

curl -X PUT "http://localhost:9200/_cluster/settings" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "persistent": {"plugins.ml_commons.rag_pipeline_feature_enabled": "true"}
}'

# register model group

curl -X POST "http://localhost:9200/_plugins/_ml/model_groups/_register" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "name": "local_model_group",
  "description": "A model group for local models"
}
'

# model_group_id : mDflxI0BGiW7YW8yBIHf

# register local model and save task_id

curl -X POST "http://localhost:9200/_plugins/_ml/models/_register" -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "name": "huggingface/sentence-transformers/msmarco-distilbert-base-tas-b",
  "version": "1.0.2",
  "model_group_id": "mDflxI0BGiW7YW8yBIHf",
  "model_format": "TORCH_SCRIPT"
}
'
# task id -> mTflxI0BGiW7YW8ygIHF

# get model id after register using task id

curl -X GET "http://localhost:9200/_plugins/_ml/tasks/mTflxI0BGiW7YW8ygIHF" -ku 'admin:admin'

# model id -> 4gflxI0B6IWSwCoIgk5x

# deploy the model

curl -X POST "http://localhost:9200/_plugins/_ml/models/4gflxI0B6IWSwCoIgk5x/_deploy" -ku 'admin:admin' -H 'Content-Type: application/json'

# task id after deploying -> mjfmxI0BGiW7YW8y6IGO

# create search pipeline

curl -X PUT "http://localhost:9200/_search/pipeline/conv_pipeline" -ku 'admin:admin' -H 'Content-Type: application/json' -ku 'admin:admin' -d '
{
  "response_processors": [
    {
      "retrieval_augmented_generation": {
        "tag": "local_model_test",
        "description": "Local model",
        "model_id": "4gflxI0B6IWSwCoIgk5x",
        "context_field_list": ["description"],
        "system_prompt": "You are a helpful assistant",
        "user_instructions": "Generate a concise and informative answer in less than 100 words for the given question"
      }
    }
  ]
}
'

# create index

curl -X PUT "http://localhost:9200/sample_index" -u 'admin:admin' -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "title": {
        "type": "keyword"
      },
      "description": {
        "type": "text"
      }
    }
  }
}'


# Insert some data
curl -X PUT "http://localhost:9200/sample_index/_doc/1" -u 'admin:admin' -H 'Content-Type: application/json' -d'
{
  "title": "Abraham Lincoln",
  "description": "Abraham Lincoln was crazy and was not enjoyed by many during the 1800s"
}
'

curl -X PUT "http://localhost:9200/sample_index/_doc/2" -u 'admin:admin' -H 'Content-Type: application/json' -d'
{
  "title": "Jeevan Limen",
  "description": "Jeenvan Limen is a renowned martial artist with 20 gold medals."
}
'



# using the model for RAG queries

curl -X GET "http://localhost:9200/sample_index/_search?search_pipeline=conv_pipeline" -u 'admin:admin' -H 'Content-Type: application/json' -d'
{
    "query": {
        "multi_match": {
            "query": "Abraham Lincoln",
            "fields": ["title", "description"]
        }
    },
	"ext": {
		"generative_qa_parameters": {
			"llm_model": "huggingface/sentence-transformers/msmarco-distilbert-base-tas-b",
			"llm_question": "Was Abraham Lincoln liked as a politician?",
			"conversation_id": "ljfhxI0BGiW7YW8yp4Eo",
                         "context_size": 5,
                         "interaction_size": 5,
                         "timeout": 15
		}
	}
}
'

```
