# Hadoop 3.3.6 Single-Node Setup on Ubuntu 24.04

A clean, practical installation guide for running Apache Hadoop 3.3.6 on
**one Ubuntu 24.04 laptop**.

This guide is intentionally designed for learning and laboratory
practice. The laptop acts as the complete Hadoop environment:

``` text
                    One Ubuntu Laptop
                           |
                    Apache Hadoop 3.3.6
                           |
             +-------------+-------------+
             |                           |
            HDFS                        YARN
             |                           |
       +-----+------+              +-----+------+
       |            |              |            |
   NameNode      DataNode    ResourceManager  NodeManager
             \                           /
              \-------- MapReduce ------/
```

This is a **single-node / pseudo-distributed Hadoop setup**. No second
laptop or external Hadoop cluster is required.

------------------------------------------------------------------------

## 1. Tested Environment

Recommended/tested environment:

``` text
OS       : Ubuntu 24.04 LTS
Java     : OpenJDK 8
Python   : Python 3
Hadoop   : Apache Hadoop 3.3.6
SSH      : OpenSSH
Mode     : Single-node / pseudo-distributed
```

This setup is suitable for:

-   HDFS commands
-   YARN
-   MapReduce
-   Hadoop Streaming
-   Python Mapper/Reducer programs
-   Word Count and other MapReduce laboratory exercises

------------------------------------------------------------------------

# 2. Install Required Software

Update packages:

``` bash
sudo apt update
```

Install Java, Python, SSH client/server:

``` bash
sudo apt install openjdk-8-jdk python3 openssh-client openssh-server -y
```

Enable SSH:

``` bash
sudo systemctl enable --now ssh
```

Check:

``` bash
java -version
python3 --version
ssh -V
systemctl is-active ssh
```

Expected:

``` text
Java 1.8.x
Python 3.x
active
```

------------------------------------------------------------------------

# 3. Find JAVA_HOME

Run:

``` bash
readlink -f "$(which java)"
```

Example:

``` text
/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java
```

Therefore:

``` text
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

Do not blindly copy this path if another Java installation is active.

You can verify:

``` bash
ls /usr/lib/jvm/
```

------------------------------------------------------------------------

# 4. Configure JAVA_HOME

Add it to `~/.bashrc`:

``` bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64' >> ~/.bashrc
```

Reload:

``` bash
source ~/.bashrc
```

Check:

``` bash
echo $JAVA_HOME
$JAVA_HOME/bin/java -version
```

Expected:

``` text
/usr/lib/jvm/java-8-openjdk-amd64
```

If your Java path is different, use your actual path instead.

------------------------------------------------------------------------

# 5. Download Hadoop 3.3.6

Go to Downloads:

``` bash
cd ~/Downloads
```

Download the binary distribution:

``` bash
wget https://downloads.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
```

Check:

``` bash
ls -lh hadoop-3.3.6.tar.gz
```

Use:

``` text
hadoop-3.3.6.tar.gz
```

Do not use:

``` text
hadoop-3.3.6-src.tar.gz
```

The `-src` archive is the source distribution.

------------------------------------------------------------------------

# 6. Install Hadoop in the Home Directory

Extract:

``` bash
cd ~/Downloads
tar -xzf hadoop-3.3.6.tar.gz
```

Move it:

``` bash
mv hadoop-3.3.6 ~/hadoop
```

Check:

``` bash
ls ~/hadoop
```

You should see directories such as:

``` text
bin
etc
include
lib
libexec
sbin
share
```

------------------------------------------------------------------------

# 7. Configure Hadoop Environment Variables

Open:

``` bash
nano ~/.bashrc
```

Add:

``` bash
# Hadoop Environment
export HADOOP_HOME=$HOME/hadoop
export HADOOP_HDFS_HOME=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop

export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
```

Reload:

``` bash
source ~/.bashrc
```

Check:

``` bash
echo $HADOOP_HOME
echo $HADOOP_MAPRED_HOME
which hadoop
which hdfs
```

Expected pattern:

``` text
/home/<username>/hadoop
/home/<username>/hadoop
/home/<username>/hadoop/bin/hadoop
/home/<username>/hadoop/bin/hdfs
```

Check Hadoop:

``` bash
hadoop version
```

Expected:

``` text
Hadoop 3.3.6
```

------------------------------------------------------------------------

# 8. Configure Hadoop's Java Environment

Open:

``` bash
nano $HADOOP_HOME/etc/hadoop/hadoop-env.sh
```

Make sure this line exists exactly once:

``` bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

Do NOT write:

``` bash
export JAVA_HOME=export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

Verify:

``` bash
grep -n 'JAVA_HOME' $HADOOP_HOME/etc/hadoop/hadoop-env.sh
```

------------------------------------------------------------------------

# 9. Create Local HDFS Storage Directories

Create:

``` bash
mkdir -p ~/hadoop_data/hdfs/namenode
mkdir -p ~/hadoop_data/hdfs/datanode
```

Structure:

``` text
~/hadoop_data/
└── hdfs/
    ├── namenode/
    └── datanode/
```

------------------------------------------------------------------------

# 10. Configure core-site.xml

Open:

``` bash
nano $HADOOP_HOME/etc/hadoop/core-site.xml
```

Use:

``` xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>

    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>

</configuration>
```

------------------------------------------------------------------------

# 11. Configure hdfs-site.xml

Open:

``` bash
nano $HADOOP_HOME/etc/hadoop/hdfs-site.xml
```

Use:

``` xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>

    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>

    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:///home/kavimugil-r/hadoop_data/hdfs/namenode</value>
    </property>

    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:///home/kavimugil-r/hadoop_data/hdfs/datanode</value>
    </property>

</configuration>
```

Replace `<YOUR_USERNAME>` with:

``` bash
whoami
```

------------------------------------------------------------------------

# 12. Configure MapReduce

Open:

``` bash
nano $HADOOP_HOME/etc/hadoop/mapred-site.xml
```

Use:

``` xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>

    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>

    <property>
        <name>mapreduce.application.classpath</name>
        <value>$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*,$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*</value>
    </property>

    <property>
        <name>yarn.app.mapreduce.am.env</name>
        <value>HADOOP_MAPRED_HOME=$HADOOP_MAPRED_HOME</value>
    </property>

    <property>
        <name>mapreduce.map.env</name>
        <value>HADOOP_MAPRED_HOME=$HADOOP_MAPRED_HOME</value>
    </property>

    <property>
        <name>mapreduce.reduce.env</name>
        <value>HADOOP_MAPRED_HOME=$HADOOP_MAPRED_HOME</value>
    </property>

</configuration>
```

------------------------------------------------------------------------

# 13. Configure YARN

Open:

``` bash
nano $HADOOP_HOME/etc/hadoop/yarn-site.xml
```

Use:

``` xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>

    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>localhost</value>
    </property>

    <property>
        <name>yarn.resourcemanager.address</name>
        <value>localhost:8032</value>
    </property>

    <property>
        <name>yarn.resourcemanager.scheduler.address</name>
        <value>localhost:8030</value>
    </property>

    <property>
        <name>yarn.resourcemanager.resource-tracker.address</name>
        <value>localhost:8031</value>
    </property>

    <property>
        <name>yarn.resourcemanager.admin.address</name>
        <value>localhost:8033</value>
    </property>

    <property>
        <name>yarn.resourcemanager.webapp.address</name>
        <value>localhost:8088</value>
    </property>

    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>

    <property>
        <name>yarn.nodemanager.aux-services.mapreduce_shuffle.class</name>
        <value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>

</configuration>
```

------------------------------------------------------------------------

# 14. Configure Passwordless localhost SSH

Hadoop's `start-dfs.sh` and `start-yarn.sh` scripts use SSH to
start/manage daemons.

Test:

``` bash
ssh localhost
```

If it fails, generate a key:

``` bash
ssh-keygen -t ed25519
cat ~/.ssh/id_ed25519.pub >> ~/.ssh/authorized_keys
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

------------------------------------------------------------------------

# 15. Verify Configuration Before Formatting

Check:

``` bash
hadoop version
echo $JAVA_HOME
echo $HADOOP_HOME
echo $HADOOP_MAPRED_HOME
hadoop getconf -confKey fs.defaultFS
hadoop getconf -confKey yarn.resourcemanager.address
```

------------------------------------------------------------------------

# 16. Format the NameNode

Format the NameNode:

``` bash
hdfs namenode -format
```

------------------------------------------------------------------------

# 17. Start HDFS

Start:

``` bash
start-dfs.sh
```

Check:

``` bash
jps
```

Expected: NameNode, DataNode, SecondaryNameNode.

------------------------------------------------------------------------

# 18. Start YARN

Start:

``` bash
start-yarn.sh
```

Check:

``` bash
jps
```

Expected: NameNode, DataNode, SecondaryNameNode, ResourceManager, NodeManager.

------------------------------------------------------------------------

# 19. Stop Hadoop

``` bash
stop-yarn.sh
stop-dfs.sh
```

------------------------------------------------------------------------

# 20. Common Problems and Solutions

## NameNode PID Conflict
If you see an error like:
`namenode is running as process XXXX. Stop it first and ensure /tmp/hadoop-kavimugil-r-namenode.pid file is empty before retry.`

1. Check if the process is actually running: `ps -fp XXXX`
2. If not running, remove the stale PID file: `rm /tmp/hadoop-kavimugil-r-namenode.pid`
3. Start HDFS again: `start-dfs.sh`

------------------------------------------------------------------------

# 21. Final Installation Verification

Run these commands:

``` bash
java -version
hadoop version
echo $JAVA_HOME
echo $HADOOP_HOME
ssh localhost
exit
```

Start Hadoop:

``` bash
start-dfs.sh
start-yarn.sh
```

Check:

``` bash
jps
hdfs dfs -ls /
yarn node -list
```

------------------------------------------------------------------------

# 22. Useful Daily Startup/Shutdown

### Start
``` bash
start-dfs.sh
start-yarn.sh
```

### Stop
``` bash
stop-yarn.sh
stop-dfs.sh
```

------------------------------------------------------------------------

# 23. Final Environment

``` text
Operating System : Ubuntu 24.04 LTS
Java             : OpenJDK 8
Python           : Python 3
Hadoop           : Apache Hadoop 3.3.6
Storage          : HDFS
Resource Manager : YARN
Processing       : MapReduce
SSH              : OpenSSH
Deployment       : Single-node / pseudo-distributed
Machines         : 1 laptop
```
