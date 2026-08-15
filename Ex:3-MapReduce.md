# Exercise 3 --- Hadoop MapReduce Word Count

## Aim

Implement a Hadoop MapReduce Word Count program using **Python Mapper +
Python Reducer + Hadoop Streaming**.

## Environment

``` text
Ubuntu 24.04 LTS
Apache Hadoop 3.5.0
Java 21
Python 3
HDFS + YARN
```

------------------------------------------------------------------------

## 1. Start Hadoop

``` bash
start-dfs.sh
start-yarn.sh
```

Check:

``` bash
jps
```

Expected:

``` text
NameNode
DataNode
SecondaryNameNode
ResourceManager
NodeManager
Jps
```

------------------------------------------------------------------------

## 2. Create the Project Directory

``` bash
mkdir -p ~/hadoop-wordcount
cd ~/hadoop-wordcount
```

------------------------------------------------------------------------

## 3. Create the Input Corpus

``` bash
nano input.txt
```

Enter:

``` text
hello hadoop
hello world
hadoop is powerful
world of hadoop
hadoop makes big data processing easy
big data is powerful
```

Save:

``` text
Ctrl+O
Enter
Ctrl+X
```

Check:

``` bash
cat input.txt
```

------------------------------------------------------------------------

## 4. Create the Mapper

``` bash
nano mapper.py
```

Use:

``` python
#!/usr/bin/env python3

import sys

for line in sys.stdin:
    words = line.strip().split()

    for word in words:
        print(f"{word.lower()}\t1")
```

Make executable:

``` bash
chmod +x mapper.py
```

### Mapper idea

``` text
hello hadoop
        ↓
hello   1
hadoop  1
```

The Mapper does **not** calculate the final frequency.

------------------------------------------------------------------------

## 5. Test the Mapper

``` bash
cat input.txt | ./mapper.py
```

If this works, Python and the Mapper are correct.

------------------------------------------------------------------------

## 6. Create the Reducer

``` bash
nano reducer.py
```

Use:

``` python
#!/usr/bin/env python3

import sys

current_word = None
current_count = 0

for line in sys.stdin:
    line = line.strip()
    word, count = line.split("\t", 1)
    count = int(count)

    if current_word == word:
        current_count += count
    else:
        if current_word is not None:
            print(f"{current_word}\t{current_count}")

        current_word = word
        current_count = count

if current_word is not None:
    print(f"{current_word}\t{current_count}")
```

Make executable:

``` bash
chmod +x reducer.py
```

------------------------------------------------------------------------

## 7. Test Mapper + Reducer Locally

This simulates Hadoop's Shuffle and Sort with Linux `sort`:

``` bash
cat input.txt | ./mapper.py | sort | ./reducer.py
```

Expected:

``` text
big         2
data        2
easy        1
hadoop      4
hello       2
is          2
makes       1
of          1
powerful    2
processing  1
world       2
```

If this works, the Python MapReduce logic is correct.

------------------------------------------------------------------------

## 8. Create HDFS Input Directory

``` bash
hdfs dfs -mkdir -p /wordcount/input
```

Check:

``` bash
hdfs dfs -ls /wordcount
```

------------------------------------------------------------------------

## 9. Upload Input to HDFS

``` bash
hdfs dfs -put -f input.txt /wordcount/input/
```

Check:

``` bash
hdfs dfs -ls /wordcount/input
```

View:

``` bash
hdfs dfs -cat /wordcount/input/input.txt
```

------------------------------------------------------------------------

## 10. Remove Previous Output

Hadoop will fail if the output directory already exists.

``` bash
hdfs dfs -rm -r -f /wordcount/output
```

------------------------------------------------------------------------

## 11. Run Hadoop Streaming

``` bash
hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
-input /wordcount/input \
-output /wordcount/output \
-mapper ~/hadoop-wordcount/mapper.py \
-reducer ~/hadoop-wordcount/reducer.py
```

Conceptually:

``` text
Input
  ↓
HDFS
  ↓
Mapper
  ↓
Shuffle & Sort
  ↓
Reducer
  ↓
HDFS Output
```

------------------------------------------------------------------------

## 12. View the Result

``` bash
hdfs dfs -ls /wordcount/output
```

Expected:

``` text
_SUCCESS
part-00000
```

Display:

``` bash
hdfs dfs -cat /wordcount/output/part-00000
```

Expected:

``` text
big         2
data        2
easy        1
hadoop      4
hello       2
is          2
makes       1
of           1
powerful    2
processing  1
world       2
```

------------------------------------------------------------------------

## 13. Copy Output to Linux

``` bash
hdfs dfs -get -f /wordcount/output ~/hadoop-wordcount/
```

Then:

``` bash
cat ~/hadoop-wordcount/output/part-00000
```
------------------------------------------------------------------------

## 14. Web-Interface

 We can see the Output at Web-Interface at Ports,

### For: HDFS / NameNode
```bash
http://localhost:9870
```
### For: YARN / ResourceManager
```bash
http://localhost:8088
```

------------------------------------------------------------------------

# MapReduce Logic

``` text
              input.txt
                  |
                  v
              HDFS Input
                  |
                  v
               Mapper
                  |
          word → 1 pairs
                  |
                  v
           Shuffle & Sort
                  |
       same words are grouped
                  |
                  v
               Reducer
                  |
             sum(values)
                  |
                  v
              HDFS Output
```

Example:

``` text
Mapper:

hadoop  1
hadoop  1
hadoop  1

Reducer:

hadoop  3
```

------------------------------------------------------------------------

# Common Errors

## `ssh localhost` → `Permission denied (publickey)`

Run:

``` bash
ssh-keygen -t ed25519
cat ~/.ssh/id_ed25519.pub >> ~/.ssh/authorized_keys
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

Test:

``` bash
ssh localhost
```

Then:

``` bash
exit
```

------------------------------------------------------------------------

## `Connection refused` on HDFS

Check:

``` bash
jps
```

If `NameNode` or `DataNode` is missing:

``` bash
start-dfs.sh
```

Test:

``` bash
hdfs dfs -ls /
```

------------------------------------------------------------------------

## ResourceManager: `0.0.0.0:8032`

If you see:

``` text
Connecting to ResourceManager at /0.0.0.0:8032
```

check:

``` bash
hadoop getconf -confKey yarn.resourcemanager.address
```

It must return:

``` text
localhost:8032
```

Check `yarn-site.xml`:

``` bash
nano $HADOOP_HOME/etc/hadoop/yarn-site.xml
```

Make sure it contains:

``` xml
<property>
    <name>yarn.resourcemanager.hostname</name>
    <value>localhost</value>
</property>

<property>
    <name>yarn.resourcemanager.address</name>
    <value>localhost:8032</value>
</property>
```

Restart YARN:

``` bash
stop-yarn.sh
start-yarn.sh
```

------------------------------------------------------------------------

## `Could not find or load main class org.apache.hadoop.mapreduce.v2.app.MRAppMaster`

Check:

``` bash
echo $HADOOP_MAPRED_HOME
```

Expected:

``` text
/home/<username>/hadoop
```

Check `mapred-site.xml` and ensure the MapReduce application classpath
and `HADOOP_MAPRED_HOME` environment settings from the installation
guide are present.

Restart YARN:

``` bash
stop-yarn.sh
start-yarn.sh
```

------------------------------------------------------------------------

## Output directory already exists

Remove it:

``` bash
hdfs dfs -rm -r -f /wordcount/output
```

Then run the streaming command again.

------------------------------------------------------------------------

## Mapper permission denied

Run:

``` bash
chmod +x mapper.py reducer.py
```

------------------------------------------------------------------------

## `hdfs` command points to the wrong program

Check:

``` bash
which hdfs
```

It should point to:

``` text
$HADOOP_HOME/bin/hdfs
```

Do not install Ubuntu's unrelated `hdfs-cli` package for this exercise.

------------------------------------------------------------------------

# Practical Exam Short Version

``` bash
mkdir -p ~/hadoop-wordcount
cd ~/hadoop-wordcount

nano input.txt
nano mapper.py
nano reducer.py

chmod +x mapper.py reducer.py

cat input.txt | ./mapper.py | sort | ./reducer.py

start-dfs.sh
start-yarn.sh

hdfs dfs -mkdir -p /wordcount/input
hdfs dfs -put -f input.txt /wordcount/input/

hdfs dfs -rm -r -f /wordcount/output

hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
-input /wordcount/input \
-output /wordcount/output \
-mapper ~/hadoop-wordcount/mapper.py \
-reducer ~/hadoop-wordcount/reducer.py

hdfs dfs -cat /wordcount/output/part-00000
```

------------------------------------------------------------------------

# Result

The Hadoop MapReduce Word Count program was successfully implemented
using Python Mapper and Reducer programs with Hadoop Streaming. The
input corpus was stored in HDFS, processed through Mapper, Shuffle and
Sort, and Reducer stages, and the final word frequencies were stored in
HDFS.
