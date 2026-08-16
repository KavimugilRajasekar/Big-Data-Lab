# Exercise 4a --- Hive Installation and Configuration

## Aim

Install and configure Apache Hive 3.1.3 on a single-node Hadoop cluster, including resolving library conflicts and initializing the Metastore using Apache Derby.

## Environment

``` text
OS       : Ubuntu 24.04 LTS
Hadoop   : Apache Hadoop 3.3.6
Java     : OpenJDK 8
Hive     : Apache Hive 3.1.3
Metastore: Apache Derby (Embedded)
```

### Configuration Flow

``` text
Hive
                     |
                     |
             hive-site.xml
                     |
                     v
          Hadoop configuration
          /        |        \
         /         |         \
core-site.xml  hdfs-site.xml  yarn-site.xml
      |
      v
 fs.defaultFS
      |
      v
 hdfs://localhost:9000
      |
      v
     HDFS
```

------------------------------------------------------------------------

## 1. Install Hive

Extract the binary distribution:

``` bash
cd ~/Downloads
tar -xzf apache-hive-3.1.3-bin.tar.gz
mv apache-hive-3.1.3-bin ~/hive
```

------------------------------------------------------------------------

## 2. Configure Environment Variables

Add the following to `~/.bashrc`:

``` bash
export HIVE_HOME=$HOME/hive
export PATH=$PATH:$HIVE_HOME/bin
```

Reload the configuration:
``` bash
source ~/.bashrc
```

------------------------------------------------------------------------

## 3. Resolve Guava Version Conflict

Hive 3.1.3 and Hadoop 3.x often have conflicting versions of the Guava library. To fix this, replace Hive's guava jar with the one from Hadoop:

``` bash
rm $HIVE_HOME/lib/guava-19.0.jar
cp $HADOOP_HOME/share/hadoop/common/lib/guava-*.jar $HIVE_HOME/lib/
```

------------------------------------------------------------------------

## 4. Configure hive-site.xml

Create or edit the `hive-site.xml` configuration file to define the metastore connection and warehouse directory.

``` bash
nano $HIVE_HOME/conf/hive-site.xml
```

Use the following configuration based on the system setup:

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:derby:;databaseName=/home/kavimugil-r/hive/metastore_db;create=true</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>org.apache.derby.jdbc.EmbeddedDriver</value>
    </property>

    <property>
        <name>hive.metastore.uris</name>
        <value></value>
    </property>

    <property>
        <name>hive.metastore.warehouse.dir</name>
        <value>/user/hive/warehouse</value>
    </property>

</configuration>
```

------------------------------------------------------------------------

## 5. Initialize Hive Metastore

Hive requires a database to store metadata (table definitions, etc.). For a local setup, we use the embedded Derby database.

Run the schematool:
``` bash
$HIVE_HOME/bin/schematool -dbType derby -initSchema
```

Expected output: `schemaTool completed`.

------------------------------------------------------------------------

## 6. Verify Installation

Start the Hive CLI:
``` bash
hive
```

Inside the Hive shell, perform a comprehensive test:

``` sql
-- 1. Create a test database
CREATE DATABASE IF NOT EXISTS hadoop_lab;
USE hadoop_lab;

-- 2. Create a table
CREATE TABLE students (
    id INT,
    name STRING,
    age INT,
    course STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ',';

-- 3. Load data (from a local file students.txt)
LOAD DATA LOCAL INPATH '/home/kavimugil-r/students.txt' INTO TABLE students;

-- 4. Basic SELECT
SELECT * FROM students;

-- 5. Aggregation
SELECT course, COUNT(*) FROM students GROUP BY course;

-- 6. Filtering
SELECT * FROM students WHERE age > 20;

-- 7. Metadata checks
SHOW TABLES;
DESCRIBE students;

exit;
```

------------------------------------------------------------------------

# Common Errors

## SLF4J Multiple Bindings Warning
If you see `SLF4J: Class path contains multiple SLF4J bindings`, it is caused by a conflict between Hive's and Hadoop's logging libraries.

**Solution:**
Remove the duplicate binding from Hive's library:
``` bash
rm $HIVE_HOME/lib/log4j-slf4j-impl-2.17.1.jar
```
This is safe as Hive will use the binding provided by Hadoop.

------------------------------------------------------------------------

# Practical Exam Short Version

``` bash
# 1. Environment Setup
source ~/.bashrc

# 2. Resolve Guava Conflict
rm $HIVE_HOME/lib/guava-19.0.jar
cp $HADOOP_HOME/share/hadoop/common/lib/guava-*.jar $HIVE_HOME/lib/

# 3. Initialize Metastore
$HIVE_HOME/bin/schematool -dbType derby -initSchema

# 4. Launch Hive
hive
```

------------------------------------------------------------------------

# Result

Apache Hive 3.1.3 was successfully installed and configured on the Hadoop cluster. The `hive-site.xml` was configured for an embedded Derby metastore, the Guava library conflict was resolved, and the Metastore was initialized. The installation was verified by creating a database and table, loading a local dataset, and executing SQL queries to retrieve and aggregate data.

# Final Environment Summary

``` text
Hadoop Home : /home/kavimugil-r/hadoop
Hive Home   : /home/kavimugil-r/hive
Metastore   : Derby (local directory: /home/kavimugil-r/hive/metastore_db)
```
