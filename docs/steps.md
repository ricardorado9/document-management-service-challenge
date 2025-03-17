## 🚀 Steps to deploy the application

From the **root folder** :

### 📌 1. Execute mvn clean package

```bash
mvn clean package
```

### 📌 2. Start docker services (Postgres, MinIO, Document Management Service)

```bash
docker compose  -f ./docker/docker-compose.yml  up --build  -d
```

### 📌 3. Test with the  Postman collections (docs/collection)

📄 Collection [Document API Local.postman_environment.json](collections/Document_Management_API.postman_collection.json)

📄 Environment [Document Management API.postman_collection.json](collections/Document_API_Local.postman_environment.json)
### 📌 4. Verify Database

Connect

```bash
docker exec -it postgresql_container psql -U admin -d challenge 
```

Query

```bash
select * from document_schema.documents d inner join document_schema.document_tags t on   d.id =t.document_id;
```

### 📌 5. Stop Services

```bash
docker compose  -f ./docker/docker-compose.yml  down
```

