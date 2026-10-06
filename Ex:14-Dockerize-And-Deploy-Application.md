# Exercise 14 --- Dockerization and Container Deployment

## Aim

Dockerize a sample application and deploy it using container orchestration. The goal is to package an application into a portable container image, store it in a central registry, and manage its deployment using Kubernetes to ensure scalability and high availability.

## Conceptual Workflow

Containerization allows developers to package an application with all its dependencies into a single image that runs consistently across different environments.

**Deployment Pipeline:**
`Application Code` $\rightarrow$ `Dockerfile` $\rightarrow$ `Docker Image` $\rightarrow$ `Container Registry` $\rightarrow$ `Orchestrator (Kubernetes)` $\rightarrow$ `Running Pods`

------------------------------------------------------------------------

## 1. Environment

``` text
Containerization: Docker
Registry: Docker Hub / Amazon ECR
Orchestration: Kubernetes (K8s) / Minikube
CI/CD: GitHub Actions
OS: Linux / Ubuntu
```

------------------------------------------------------------------------

## 2. Dockerization (Packaging the App)

Using the sample Python application from Exercise 13, we will create a `Dockerfile` to define how the image should be built.

### A. Create the `Dockerfile`
``` dockerfile
# Use a lightweight Python base image
FROM python:3.9-slim

# Set the working directory inside the container
WORKDIR /app

# Copy requirements first to leverage Docker cache
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application code
COPY app.py .

# Define the command to run the application
CMD ["python", "app.py"]
```

### B. Build the Docker Image locally
``` bash
# Build the image with a tag
docker build -t sample-app:v1 .

# Verify the image exists locally
docker images
```

### C. Run the Container locally
``` bash
docker run -d --name my-running-app sample-app:v1
docker logs my-running-app
```

------------------------------------------------------------------------

## 3. Container Registry (Distribution)

To deploy to a remote server or orchestrator, the image must be uploaded to a Registry.

### Steps to push to Docker Hub:
``` bash
# 1. Log in to your Docker Hub account
docker login

# 2. Tag the image with your Docker Hub username
# Format: docker tag <local-image> <username>/<repo-name>:<tag>
docker tag sample-app:v1 yourusername/sample-app:v1

# 3. Push the image to the cloud
docker push yourusername/sample-app:v1
```

------------------------------------------------------------------------

## 4. Orchestration with Kubernetes (Deployment)

Kubernetes manages the lifecycle of containers, ensuring the desired number of replicas are always running.

### A. Create the Kubernetes Manifest (`deployment.yaml`)
``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sample-app-deployment
spec:
  replicas: 3 # Run 3 instances for high availability
  selector:
    matchLabels:
      app: sample-app
  template:
    metadata:
      labels:
        app: sample-app
    spec:
      containers:
      - name: sample-app
        image: yourusername/sample-app:v1 # Image from Docker Hub
        ports:
        - containerPort: 80

---
apiVersion: v1
kind: Service
metadata:
  name: sample-app-service
spec:
  selector:
    app: sample-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 80
  type: LoadBalancer
```

### B. Deploy to Kubernetes
``` bash
# Apply the manifest to the cluster
kubectl apply -f deployment.yaml

# Check the status of pods
kubectl get pods

# Check the service status to get the external IP
kubectl get service sample-app-service
```

------------------------------------------------------------------------

## 5. Integrating with CI/CD Pipeline

To fully automate this, update the GitHub Actions workflow (`.github/workflows/main.yml`) from Exercise 13 to include the following steps:

``` yaml
    - name: Log in to Docker Hub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKERHUB_USERNAME }}
        password: ${{ secrets.DOCKERHUB_TOKEN }}

    - name: Build and Push Docker Image
      uses: docker/build-push-action@v3
      with:
        push: true
        tags: yourusername/sample-app:latest

    - name: Deploy to Kubernetes
      run: |
        kubectl apply -f k8s/deployment.yaml
```

------------------------------------------------------------------------

## Result

The application was successfully dockerized and deployed using a container orchestration platform. By using **Docker**, we eliminated the "it works on my machine" problem, and by using **Kubernetes**, we enabled the application to scale automatically and recover from failures. The integration with the **CI/CD pipeline** ensures that every code commit is automatically packaged and deployed to production without manual intervention.
