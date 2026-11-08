## Running the Database with Docker

The backend uses a PostgreSQL database running in a Docker container.

### 1. Start Docker

Starting up docker desktop:

```bash
& "C:\Program Files\Docker\Docker\Docker Desktop.exe"
```

To verify that Docker is running, execute:

```bash
docker info
```

### 2. Start the PostgreSQL Container

Navigate to the directory containing the `docker-compose.yml` file.

Run the following command to create and start the container:

```bash
docker compose up -d
```

The `-d` option runs the container in the background.

### 3. Check the Container Status

To verify that the container is running, execute:

```bash
docker compose ps
```

### 4. Stop the Container

To stop the container, execute:

```bash
docker compose down
```

This stops and removes the container. Data stored in a persistent Docker volume will be preserved.

### 5. Restart the Container

To restart the database after stopping it, execute:

```bash
docker compose up -d
```

### 6. Check PostgreSQL Database

To verify that the PostgreSQL database is running and contains the imported test results, follow these steps.

**Step 1: Open the PostgreSQL prompt**

Open a terminal and execute:

```bash
docker exec -it testinsight-postgres psql -U postgres
```

This command opens the PostgreSQL command-line interface (`psql`) inside the running Docker container.

**Step 2: Connect to the TestInsight database**

In the PostgreSQL prompt, execute:

```sql
\c testinsight
```

This command connects to the `testinsight` database.

**Step 3: Check the imported test results**

Execute the following SQL query:

```sql
SELECT * FROM test_result;
```

This query displays all records stored in the `test_result` table.

**Step 4: Exit PostgreSQL**

To exit the PostgreSQL prompt, execute:

```sql
\q
```