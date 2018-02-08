# 基于Elasticsearch的BOSS业务数据查询DSL

## 初始页面
```
GET /boss-mock/soap-mock/_search
{
 "size": 20, 
"query": {
  "range": {
    "response_time": {
      "gte": "2017-11-06T06:21:31.271Z",
      "lte": "2017-11-06T07:21:31.271Z"
    }
  }
}
, "sort": [
  {
    "response_time": {
      "order": "desc"
    }
  }
]
}
```

## 查询号码

```
GET /boss-mock/soap-mock/_search
{
  "query": {
    "terms": {
      "msisdn.keyword": [
        "8618355610180",
        "8618554987063"
      ]
    }
  }
}

```

## 根据task_id查询
```
GET /boss-mock/soap-mock/_search
{
  "query": {
    "term": {
      "task_id.keyword": {
        "value": "a-a83af24c-6dbd-4d3c-8906-7fdfbde4e396#1509950868493"
      }
    }
    
  }
}
```