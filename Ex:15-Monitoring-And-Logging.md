# Exercise 15 --- Monitoring and Logging for Deployed Applications

## Aim

Implement a comprehensive **Observability** strategy for the deployed application. This involves configuring monitoring tools to collect and visualize performance metrics, setting up centralized logging to aggregate logs from multiple containers, and integrating these systems into the CI/CD pipeline for proactive issue detection.

## Conceptual Workflow

Observability is achieved through three main pillars: **Metrics, Logging, and Tracing**. For this exercise, we focus on the first two.

**The Observability Pipeline:**
- **Monitoring**: `App Metrics` $\rightarrow$ `Prometheus (Collection)` $\rightarrow$ `Grafana (Visualization)` $\rightarrow$ `Alertmanager (Notification)`
- **Logging**: `App Logs` $\rightarrow$ `Fluentd/Logstash (Aggregation)` $\rightarrow$ `Elasticsearch (Indexing)` $\rightarrow$ `Kibana (Visualization)`

------------------------------------------------------------------------

## 1. Environment

``` text
Monitoring: Prometheus, Grafana
Logging: ELK Stack (Elasticsearch, Logstash, Kibana) or EFK (Fluentd)
Instrumentation: prometheus_client (Python)
Orchestration: Kubernetes
```

------------------------------------------------------------------------

## 2. Application Instrumentation (Metrics)

To allow Prometheus to monitor the application, the app must expose a `/metrics` endpoint.

### A. Update the Application (`app.py`)
``` python
from flask import Flask
from prometheus_client import start_http_server, Counter, Histogram

app = Flask(__name__)

# Define custom metrics
REQUEST_COUNT = Counter('app_requests_total', 'Total number of requests')
REQUEST_LATENCY = Histogram('app_request_latency_seconds', 'Request latency')

@app.route('/')
def hello():
    REQUEST_COUNT.inc() # Increment request counter
    return "Hello from Dockerized App!"

if __name__ == "__main__":
    # Start Prometheus metrics server on port 8000
    start_http_server(8000)
    app.run(host='0.0.0.0', port=80)
```

------------------------------------------------------------------------

## 3. Monitoring with Prometheus and Grafana

### A. Prometheus Configuration (`prometheus.yml`)
Prometheus "scrapes" (pulls) metrics from the application at regular intervals.

``` yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'python-app'
    static_configs:
      - targets: ['sample-app-service:8000'] # Target the K8s service
```

### B. Visualizing with Grafana
1. **Connect Data Source**: In Grafana, add **Prometheus** as the data source using the Prometheus server URL.
2. **Create Dashboard**:
    - Add a "Stat" panel for `sum(app_requests_total)`.
    - Add a "Graph" panel for `rate(app_request_latency_seconds_sum[5m])`.
    - Add a "Gauge" for CPU/Memory usage from the K8s node exporter.

------------------------------------------------------------------------

## 4. Centralized Logging (EFK Stack)

In a containerized environment, logs are ephemeral. We use the EFK stack to persist and search them.

### Logging Workflow:
1. **Fluentd (Collector)**: Runs as a `DaemonSet` on every Kubernetes node. It collects logs from `/var/log/containers/*.log`.
2. **Elasticsearch (Store)**: Indexes the logs for fast full-text searching.
3. **Kibana (UI)**: Provides a search bar and dashboards to filter logs by `pod_name`, `severity` (INFO, ERROR), or `timestamp`.

### Example Log Search in Kibana:
`level: "ERROR" AND pod_name: "sample-app-v1-xyz"`

------------------------------------------------------------------------

## 5. Integration with CI/CD and Proactive Detection

### A. Alerting with Alertmanager
Configure alerts to notify the team before users notice a failure.

**Alert Rule Example:**
``` yaml
- alert: HighErrorRate
  expr: rate(app_requests_total{status="500"}[5m]) > 0.1
  for: 2m
  labels:
    severity: critical
  annotations:
    summary: "High error rate detected on sample-app"
```

### B. CI/CD Pipeline Integration
Modify the GitHub Actions workflow to include **Post-Deployment Health Checks**:

``` yaml
    - name: Post-Deployment Health Check
      run: |
        # Query Prometheus API to check error rates after deploy
        STATUS=$(curl -s "http://prometheus:9090/api/v1/query?query=up")
        if [[ $STATUS != *"1"* ]]; then
          echo "Deployment Health Check Failed! Triggering Rollback..."
          kubectl rollout undo deployment/sample-app-deployment
          exit 1
        fi
```

------------------------------------------------------------------------

## Result

A complete observability stack was implemented for the deployed application. By integrating **Prometheus and Grafana**, we gained real-time visibility into application performance. The **EFK stack** provided a centralized way to troubleshoot errors across multiple containers. Finally, by integrating **Alertmanager** and **Health Checks** into the CI/CD pipeline, the system transitioned from reactive troubleshooting to proactive issue detection and automatic recovery.
