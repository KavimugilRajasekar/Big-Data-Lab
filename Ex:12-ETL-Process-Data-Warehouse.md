# Exercise 12 --- ETL Process for Data Warehouse

## Aim

Develop an **ETL (Extract, Transform, Load)** process using SQL scripts to move data from a transactional source system into a data warehouse. The exercise focuses on data quality assurance—identifying and handling missing values, duplicates, and inconsistencies—and performing analytical queries on the final warehouse.

## Conceptual Workflow

The ETL process consists of three primary phases:
1.  **Extract**: Retrieving raw data from the source transactional database.
2.  **Transform**: Cleaning the data (cleansing), resolving inconsistencies, and formatting it to fit the warehouse schema.
3.  **Load**: Inserting the cleaned, transformed data into the Star Schema (Dimension and Fact tables).

------------------------------------------------------------------------

## 1. Environment

``` text
Apache Hive / HiveQL
Hadoop HDFS
Standard SQL Database
```

------------------------------------------------------------------------

## 2. Source System Setup (Transactional DB)

To simulate a real-world scenario, we first create "Source" tables that contain intentionally "dirty" data (duplicates and NULLs).

### Creating Raw Source Tables
``` sql
-- Source Transactional Table
CREATE TABLE src_sales_transactions (
    txn_id INT,
    prod_id INT,
    cust_id INT,
    txn_date STRING,
    qty INT,
    amount DOUBLE,
    city STRING
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

-- Source Customer Table
CREATE TABLE src_customers (
    cust_id INT,
    cust_name STRING,
    email STRING,
    gender STRING
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
```

### Loading Messy Data
``` sql
-- Inserting data with duplicates and NULLs
INSERT INTO src_sales_transactions VALUES 
(1, 101, 501, '2023-10-01', 1, 800.0, 'New York'),
(1, 101, 501, '2023-10-01', 1, 800.0, 'New York'), -- Duplicate
(2, 102, 502, '2023-10-02', 2, 1200.0, 'Chicago'),
(3, 103, NULL, '2023-10-03', 1, 500.0, 'New York'), -- Missing CustomerID
(4, 101, 501, '2023-10-04', NULL, 800.0, 'New York'); -- Missing Quantity

INSERT INTO src_customers VALUES 
(501, 'Alice Smith', 'alice@example.com', 'Female'),
(502, 'Bob Johnson', 'bob@example.com', 'Male'),
(502, 'Bob Johnson', 'bob@example.com', 'Male'); -- Duplicate
```

------------------------------------------------------------------------

## 3. Data Quality Checks (The "Extract" Phase)

Before loading data, we must identify issues to determine the necessary transformations.

### Check for Duplicates
``` sql
SELECT txn_id, COUNT(*) 
FROM src_sales_transactions 
GROUP BY txn_id 
HAVING COUNT(*) > 1;
```

### Check for Missing Values (NULLs)
``` sql
SELECT COUNT(*) as MissingCustID 
FROM src_sales_transactions 
WHERE cust_id IS NULL;

SELECT COUNT(*) as MissingQty 
FROM src_sales_transactions 
WHERE qty IS NULL;
```

------------------------------------------------------------------------

## 4. Transformation and Loading (The "Transform & Load" Phase)

We now use SQL to cleanse the data and load it into the Star Schema created in Exercise 11.

### A. Loading Dimension Tables (Cleansing)
We use `DISTINCT` to remove duplicates and `COALESCE` to handle missing values.

``` sql
-- Load Dim_Customer from src_customers (Removing duplicates)
INSERT INTO Dim_Customer (CustomerID, CustomerName, Email, Gender)
SELECT DISTINCT cust_id, cust_name, email, gender 
FROM src_customers;

-- Load Dim_Geography (Extracting unique cities from transactions)
INSERT INTO Dim_Geography (GeoID, City)
SELECT DISTINCT CAST(row_number() OVER() AS INT), city 
FROM src_sales_transactions;
```

### B. Loading the Fact Table (Transformation)
We handle the missing quantity by assigning a default value (e.g., 1) and filtering out records with missing critical keys.

``` sql
INSERT INTO Fact_Sales (SalesID, ProductID, CustomerID, TimeKey, GeoID, Quantity, TotalAmount)
SELECT 
    s.txn_id, 
    s.prod_id, 
    s.cust_id, 
    CAST(regexp_replace(s.txn_date, '-', '') AS INT), -- Transform Date to TimeKey
    g.GeoID, 
    COALESCE(s.qty, 1), -- Handle missing Quantity
    s.amount
FROM src_sales_transactions s
JOIN Dim_Geography g ON s.city = g.City
WHERE s.cust_id IS NOT NULL; -- Remove inconsistent records
```

------------------------------------------------------------------------

## 5. Final Reporting Queries

Now that the data is loaded into the Warehouse, we can retrieve business insights.

### Query 1: Total Sales Revenue by Product Category
``` sql
SELECT p.Category, SUM(f.TotalAmount) as Revenue
FROM Fact_Sales f
JOIN Dim_Product p ON f.ProductID = p.ProductID
GROUP BY p.Category;
```

### Query 2: Customer Demographics (Sales by Gender)
``` sql
SELECT c.Gender, SUM(f.TotalAmount) as TotalSpent
FROM Fact_Sales f
JOIN Dim_Customer c ON f.CustomerID = c.CustomerID
GROUP BY c.Gender;
```

### Query 3: Sales Trends Over Time
``` sql
SELECT t.Year, t.Month, SUM(f.TotalAmount) as MonthlySales
FROM Fact_Sales f
JOIN Dim_Time t ON f.TimeKey = t.TimeKey
GROUP BY t.Year, t.Month
ORDER BY t.Year, t.Month;
```

------------------------------------------------------------------------

## Result

The ETL process was successfully developed. By implementing strict **Data Quality Checks**, we identified duplicates and NULL values in the source system. Through the **Transformation** phase, we cleansed the data using `DISTINCT` and `COALESCE` and mapped transactional data into a structured **Star Schema**. The final reporting queries demonstrate that the data warehouse now provides a "single version of truth" for retail business analysis.
