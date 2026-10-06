# Exercise 11 --- Star Schema for Retail Sales Data Warehouse

## Aim

Design and implement a **Star Schema** for a Retail Sales Data Warehouse. This involves creating a central **Fact Table** for sales transactions and multiple **Dimension Tables** for Products, Customers, Time, and Geography to support Online Analytical Processing (OLAP).

## Conceptual Design

A **Star Schema** is the simplest style of data warehouse schema. It consists of one or more fact tables referencing any number of dimension tables. It is called a star schema because the fact table sits at the center of a star-like structure, surrounded by dimensions.

### Schema Components:
- **Fact Table (`Fact_Sales`)**: Stores quantitative data (measures) and foreign keys to dimensions.
- **Dimension Tables**: Store descriptive attributes (context) for the facts.
    - `Dim_Product`: Details about the items sold.
    - `Dim_Customer`: Details about the buyers.
    - `Dim_Time`: Temporal attributes (Year, Quarter, Month, Day).
    - `Dim_Geography`: Location attributes of the sale.

------------------------------------------------------------------------

## 1. Environment

``` text
Apache Hive / HiveQL
Hadoop HDFS
Standard SQL Database
```

------------------------------------------------------------------------

## 2. Implementation: Creating Dimension Tables

Run the following HiveQL/SQL commands to create the dimension tables.

### A. Product Dimension
``` sql
CREATE TABLE Dim_Product (
    ProductID INT,
    ProductName STRING,
    Category STRING,
    Brand STRING,
    UnitPrice DOUBLE
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
```

### B. Customer Dimension
``` sql
CREATE TABLE Dim_Customer (
    CustomerID INT,
    CustomerName STRING,
    Email STRING,
    Gender STRING,
    Segment STRING
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
```

### C. Time Dimension
``` sql
CREATE TABLE Dim_Time (
    TimeKey INT,
    FullDate DATE,
    Day INT,
    Month INT,
    Quarter INT,
    Year INT
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
```

### D. Geography Dimension
``` sql
CREATE TABLE Dim_Geography (
    GeoID INT,
    City STRING,
    State STRING,
    Country STRING,
    Region STRING
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
```

------------------------------------------------------------------------

## 3. Implementation: Creating the Fact Table

The fact table contains the foreign keys to all the dimensions and the numeric measures.

``` sql
CREATE TABLE Fact_Sales (
    SalesID INT,
    ProductID INT,
    CustomerID INT,
    TimeKey INT,
    GeoID INT,
    Quantity INT,
    TotalAmount DOUBLE,
    Discount DOUBLE
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
```

------------------------------------------------------------------------

## 4. Data Population (Example Inserts)

### Loading Dimensions
``` sql
INSERT INTO Dim_Product VALUES (101, 'Laptop', 'Electronics', 'Dell', 800.00), (102, 'Smartphone', 'Electronics', 'Samsung', 600.00);
INSERT INTO Dim_Customer VALUES (501, 'Alice Smith', 'alice@example.com', 'Female', 'Corporate'), (502, 'Bob Johnson', 'bob@example.com', 'Male', 'Consumer');
INSERT INTO Dim_Time VALUES (20231006, '2023-10-06', 6, 10, 4, 2023);
INSERT INTO Dim_Geography VALUES (901, 'New York', 'NY', 'USA', 'North America');
```

### Loading Fact Table
``` sql
INSERT INTO Fact_Sales VALUES (1, 101, 501, 20231006, 901, 1, 800.00, 50.00);
```

------------------------------------------------------------------------

## 5. Analysis Queries (OLAP Operations)

### Query 1: Total Sales Amount by Product Category
``` sql
SELECT p.Category, SUM(f.TotalAmount) as TotalRevenue
FROM Fact_Sales f
JOIN Dim_Product p ON f.ProductID = p.ProductID
GROUP BY p.Category;
```

### Query 2: Sales Trends by Year and Quarter
``` sql
SELECT t.Year, t.Quarter, SUM(f.TotalAmount) as QuarterlySales
FROM Fact_Sales f
JOIN Dim_Time t ON f.TimeKey = t.TimeKey
GROUP BY t.Year, t.Quarter;
```

### Query 3: Top Performing City by Sales
``` sql
SELECT g.City, SUM(f.TotalAmount) as CitySales
FROM Fact_Sales f
JOIN Dim_Geography g ON f.GeoID = g.GeoID
GROUP BY g.City
ORDER BY CitySales DESC
LIMIT 1;
```

------------------------------------------------------------------------

## Result

The Star Schema for the Retail Sales Data Warehouse was successfully designed and implemented. By separating the descriptive attributes into **Dimension Tables** and the quantitative measures into a **Fact Table**, we have created a structure that optimizes query performance for large-scale aggregations and reporting, typical of a Data Warehousing environment.
