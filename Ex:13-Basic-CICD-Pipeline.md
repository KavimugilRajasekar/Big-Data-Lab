# Exercise 13 --- Basic CI/CD Pipeline for Sample Application

## Aim

Create a basic **CI/CD (Continuous Integration / Continuous Deployment)** pipeline for a sample application. The goal is to automate the process of building, testing, and deploying code using a version control system (GitHub) and a CI tool (GitHub Actions).

## Conceptual Workflow

CI/CD is a method to frequently deliver apps to customers by introducing automation into the stages of app development:
- **Continuous Integration (CI)**: Automatically building and testing the code every time a developer commits changes to the version control system.
- **Continuous Deployment (CD)**: Automatically deploying the tested code to a production environment.

**Pipeline Stages:**
`Code Commit` $\rightarrow$ `Build` (Install Deps) $\rightarrow$ `Test` (Run Unit Tests) $\rightarrow$ `Deploy` (Release to Server)

------------------------------------------------------------------------

## 1. Environment

``` text
Version Control: GitHub
CI/CD Tool: GitHub Actions
Language: Python 3.x
OS: Ubuntu (GitHub-hosted Runner)
```

------------------------------------------------------------------------

## 2. Sample Application Setup

Before setting up the pipeline, we need a simple application to test.

### A. Create the Application (`app.py`)
``` python
def add(a, b):
    return a + b

if __name__ == "__main__":
    print(f"Result of 2 + 3 is: {add(2, 3)}")
```

### B. Create a Test File (`test_app.py`)
``` python
import unittest
from app import add

class TestApp(unittest.TestCase):
    def test_add(self):
        self.assertEqual(add(2, 3), 5)
        self.assertEqual(add(-1, 1), 0)

if __name__ == "__main__":
    unittest.main()
```

### C. Create Dependencies File (`requirements.txt`)
``` text
# No external dependencies for this basic app, 
# but this file is required for the build stage.
```

------------------------------------------------------------------------

## 3. Version Control Setup (GitHub)

1. Create a new repository on GitHub named `basic-cicd-demo`.
2. Initialize the local directory and push the code:

``` bash
git init
git add .
git commit -m "Initial commit: sample app and tests"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/basic-cicd-demo.git
git push -u origin main
```

------------------------------------------------------------------------

## 4. CI/CD Configuration (GitHub Actions)

GitHub Actions uses **YAML** files stored in a specific directory to define workflows.

### Create the Workflow File
Create a directory `.github/workflows/` and a file named `main.yml` inside it.

``` bash
mkdir -p .github/workflows
nano .github/workflows/main.yml
```

### Workflow Definition (`main.yml`)
``` yaml
name: Python CI/CD Pipeline

# Trigger the pipeline on every push to the main branch
on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout Code
      uses: actions/checkout@v3

    - name: Set up Python
      uses: actions/setup-python@v4
      with:
        python-version: '3.9'

    - name: Install Dependencies
      run: |
        python -m pip install --upgrade pip
        if [ -f requirements.txt ]; then pip install -r requirements.txt; fi

    - name: Run Unit Tests
      run: |
        python test_app.py

  deploy:
    needs: build-and-test # Only deploy if the build-and-test job succeeds
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main' # Only deploy from the main branch

    steps:
    - name: Simulate Deployment
      run: |
        echo "Connecting to production server..."
        echo "Deploying application files..."
        echo "Deployment Successful! App is now live."
```

------------------------------------------------------------------------

## 5. Triggering and Verifying the Pipeline

### A. Commit and Push the Workflow
``` bash
git add .github/workflows/main.yml
git commit -m "Add GitHub Actions CI/CD pipeline"
git push origin main
```

### B. Verification
1. Open your repository on **GitHub**.
2. Click on the **"Actions"** tab.
3. You will see a workflow run titled "Add GitHub Actions CI/CD pipeline".
4. Click on the run to see the progress of the **Build**, **Test**, and **Deploy** stages.
5. A **Green Checkmark** indicates that the code was successfully built, tested, and deployed.

------------------------------------------------------------------------

## Result

A basic CI/CD pipeline was successfully implemented using GitHub Actions. By automating the build and test process, we ensure that no breaking changes are merged into the main branch. The deployment stage demonstrates how software can be delivered to production automatically upon a successful test pass, significantly reducing manual intervention and deployment errors.
