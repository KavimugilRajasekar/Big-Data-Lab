# Hadoop 3.5.0 Single-Node Setup on Ubuntu 24.04

A clean, practical installation guide for running Apache Hadoop 3.5.0 on
**one Ubuntu 24.04 laptop**.

This guide is intentionally designed for learning and laboratory
practice. The laptop acts as the complete Hadoop environment:

``` text
                    One Ubuntu Laptop
                           |
                    Apache Hadoop 3.5.0
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
Java     : OpenJDK 21
Python   : Python 3
Hadoop   : Apache Hadoop 3.5.0
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
sudo apt install openjdk-21-jdk python3 openssh-client openssh-server -y
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
Java 21.x
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
/usr/lib/jvm/java-21-openjdk-amd64/bin/java
```

Therefore:

``` text
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
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
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
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
/usr/lib/jvm/java-21-openjdk-amd64
```

If your Java path is different, use your actual path instead.

------------------------------------------------------------------------

# 5. Download Hadoop 3.5.0

Go to Downloads:

``` bash
cd ~/Downloads
```

Download the binary distribution:

``` bash
wget https://downloads.apache.org/hadoop/common/hadoop-3.5.0/hadoop-3.5.0.tar.gz
```

Check:

``` bash
ls -lh hadoop-3.5.0.tar.gz
```

Use:

``` text
hadoop-3.5.0.tar.gz
```

Do not use:

``` text
hadoop-3.5.0-src.tar.gz
```

The `-src` archive is the source distribution.

------------------------------------------------------------------------

# 6. Install Hadoop in the Home Directory

Extract:

``` bash
cd ~/Downloads
tar -xzf hadoop-3.5.0.tar.gz
```

Move it:

``` bash
mv hadoop-3.5.0 ~/hadoop
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
Hadoop 3.5.0
```

------------------------------------------------------------------------

# 8. Configure Hadoop's Java Environment

Open:

``` bash
nano $HADOOP_HOME/etc/hadoop/hadoop-env.sh
```

Make sure this line exists exactly once:

``` bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
```

Do NOT write:

``` bash
export JAVA_HOME=export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
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

### Meaning

``` text
hdfs://localhost:9000
```

means:

``` text
Protocol : HDFS
Host     : localhost
Port     : 9000
```

The NameNode is running on the same laptop.

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
        <value>file:///home/<YOUR_USERNAME>/hadoop_data/hdfs/namenode</value>
    </property>

    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:///home/<YOUR_USERNAME>/hadoop_data/hdfs/datanode</value>
    </property>

</configuration>
```

Replace `<YOUR_USERNAME>` with:

``` bash
whoami
```

For example:

``` xml
<value>file:///home/kavimugil-r/hadoop_data/hdfs/namenode</value>
```

and:

``` xml
<value>file:///home/kavimugil-r/hadoop_data/hdfs/datanode</value>
```

### Why replication is 1

This is a one-laptop environment with one DataNode, so:

``` xml
<name>dfs.replication</name>
<value>1</value>
```

is appropriate.

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

The first property tells MapReduce to run through YARN.

The classpath/environment entries prevent the common:

``` text
Could not find or load main class
org.apache.hadoop.mapreduce.v2.app.MRAppMaster
```

problem when running MapReduce on a manually configured installation.

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

### Important: `localhost`, not `0.0.0.0`

The ResourceManager must listen on the configured ports, but Hadoop
clients must have a real destination.

Correct:

``` text
localhost:8032
```

Incorrect as a client destination:

``` text
0.0.0.0:8032
```

If you ever see:

``` text
Connecting to ResourceManager at /0.0.0.0:8032
```

check `yarn-site.xml` immediately.

Verify the effective configuration:

``` bash
hadoop getconf -confKey yarn.resourcemanager.address
```

Expected:

``` text
localhost:8032
```

------------------------------------------------------------------------

# 14. Configure Passwordless localhost SSH

Hadoop's `start-dfs.sh` and `start-yarn.sh` scripts use SSH to
start/manage daemons.

Check SSH:

``` bash
which ssh
which sshd
systemctl is-active ssh
```

Expected:

``` text
/usr/bin/ssh
/usr/sbin/sshd
active
```

Test:

``` bash
ssh localhost
```

On the first connection, accept the host key:

``` text
yes
```

Then exit:

``` bash
exit
```

------------------------------------------------------------------------

## 14.1 If `ssh localhost` gives `Permission denied (publickey)`

This is a common Hadoop setup problem.

Generate a key:

``` bash
ssh-keygen -t ed25519
```

Press Enter to accept the default file location. For a simple lab setup,
you can leave the passphrase empty.

Add the public key:

``` bash
cat ~/.ssh/id_ed25519.pub >> ~/.ssh/authorized_keys
```

Set permissions:

``` bash
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

Test:

``` bash
ssh localhost
```

It should connect without asking for the Linux account password.

Exit:

``` bash
exit
```

### If the key already exists

Do not generate unnecessary additional keys. Check:

``` bash
ls -la ~/.ssh
```

If `id_ed25519` and `id_ed25519.pub` already exist, use them.

### If SSH still fails

Run:

``` bash
ssh -v localhost
```

Look for:

``` text
Offering public key
Server accepts key
Authenticated
```

Also check:

``` bash
ls -ld ~/.ssh
ls -l ~/.ssh/authorized_keys
```

------------------------------------------------------------------------

# 15. Verify Configuration Before Formatting

Check:

``` bash
hadoop version
```

``` bash
echo $JAVA_HOME
```

``` bash
echo $HADOOP_HOME
```

``` bash
echo $HADOOP_MAPRED_HOME
```

``` bash
hadoop getconf -confKey fs.defaultFS
```

Expected:

``` text
hdfs://localhost:9000
```

Check YARN:

``` bash
hadoop getconf -confKey yarn.resourcemanager.address
```

Expected:

``` text
localhost:8032
```

Test SSH:

``` bash
ssh localhost
```

Then:

``` bash
exit
```

Only continue when these checks are correct.

------------------------------------------------------------------------

# 16. Format the NameNode

Format the NameNode:

``` bash
hdfs namenode -format
```

At the end, look for a successful format message.

### Important

**Do not repeatedly run `hdfs namenode -format` during normal
practice.**

Formatting creates a new HDFS namespace. Re-formatting an existing
installation can make previously stored HDFS data inaccessible.

For normal exercises, format once during initial setup.

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

Expected:

``` text
NameNode
DataNode
SecondaryNameNode
Jps
```

Test HDFS:

``` bash
hdfs dfs -ls /
```

If this works, HDFS is running.

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

Expected:

``` text
NameNode
DataNode
SecondaryNameNode
ResourceManager
NodeManager
Jps
```

Test:

``` bash
yarn node -list
```

You should see one NodeManager.

Verify ResourceManager is listening:

``` bash
ss -lntp | grep 8032
```

------------------------------------------------------------------------

# 19. Stop Hadoop

When you are finished:

``` bash
stop-yarn.sh
stop-dfs.sh
```

Check:

``` bash
jps
```

Normally only:

``` text
Jps
```

should remain.

------------------------------------------------------------------------

# 20. Common Problems and Solutions

## Problem 1: `hadoop: command not found`

Check:

``` bash
echo $HADOOP_HOME
which hadoop
```

If incorrect, verify `~/.bashrc` contains:

``` bash
export HADOOP_HOME=$HOME/hadoop
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
```

Then:

``` bash
source ~/.bashrc
```

------------------------------------------------------------------------

## Problem 2: `JAVA_HOME is not set`

Check:

``` bash
echo $JAVA_HOME
```

Then:

``` bash
readlink -f "$(which java)"
```

Set the correct Java directory in:

``` text
~/.bashrc
```

and:

``` text
$HADOOP_HOME/etc/hadoop/hadoop-env.sh
```

------------------------------------------------------------------------

## Problem 3: `Permission denied (publickey)`

Run:

``` bash
ssh localhost
```

If it fails:

``` bash
ssh-keygen -t ed25519
cat ~/.ssh/id_ed25519.pub >> ~/.ssh/authorized_keys
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

Then:

``` bash
ssh localhost
```

------------------------------------------------------------------------

## Problem 4: `Connection refused` on `localhost:9000`

This normally means the NameNode is not running.

Check:

``` bash
jps
```

If `NameNode` is missing:

``` bash
start-dfs.sh
```

Then:

``` bash
jps
```

Check logs if necessary:

``` bash
ls $HADOOP_HOME/logs
```

------------------------------------------------------------------------

## Problem 5: ResourceManager connection to `0.0.0.0:8032`

Error pattern:

``` text
Connecting to ResourceManager at /0.0.0.0:8032
```

Fix `yarn-site.xml`.

Use:

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

Then restart YARN:

``` bash
stop-yarn.sh
start-yarn.sh
```

Verify:

``` bash
hadoop getconf -confKey yarn.resourcemanager.address
```

Expected:

``` text
localhost:8032
```

------------------------------------------------------------------------

## Problem 6: `Could not find or load main class org.apache.hadoop.mapreduce.v2.app.MRAppMaster`

Check:

``` bash
echo $HADOOP_MAPRED_HOME
```

It should point to the Hadoop installation.

Check `mapred-site.xml` and make sure the MapReduce application
classpath and `HADOOP_MAPRED_HOME` environment entries are configured as
shown in this guide.

Restart YARN after changing configuration:

``` bash
stop-yarn.sh
start-yarn.sh
```

------------------------------------------------------------------------

## Problem 7: Old MapReduce output already exists

Hadoop does not normally allow a MapReduce job to overwrite an existing
output directory.

Remove it:

``` bash
hdfs dfs -rm -r -f /wordcount/output
```

Then run the job again.

------------------------------------------------------------------------

## Problem 8: `hdfs-cli` confusion

Do not install Ubuntu's unrelated package:

``` bash
sudo apt install hdfs-cli
```

for this Hadoop setup.

Your Hadoop installation already provides:

``` bash
$HADOOP_HOME/bin/hdfs
```

Check:

``` bash
which hdfs
```

Expected:

``` text
/home/<username>/hadoop/bin/hdfs
```

------------------------------------------------------------------------

## Problem 9: Hadoop daemons start and immediately disappear

Check:

``` bash
jps
```

Then inspect Hadoop logs:

``` bash
ls -lh $HADOOP_HOME/logs
```

Useful commands:

``` bash
grep -i "error\|exception" $HADOOP_HOME/logs/* | tail -50
```

Also verify:

``` bash
echo $JAVA_HOME
echo $HADOOP_HOME
echo $HADOOP_MAPRED_HOME
```

and:

``` bash
ssh localhost
```

------------------------------------------------------------------------

# 21. Final Installation Verification

Run these commands:

``` bash
java -version
```

``` bash
python3 --version
```

``` bash
hadoop version
```

``` bash
which hdfs
```

``` bash
echo $JAVA_HOME
```

``` bash
echo $HADOOP_HOME
```

``` bash
ssh localhost
```

Then:

``` bash
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

Check HDFS:

``` bash
hdfs dfs -ls /
```

Check YARN:

``` bash
yarn node -list
```

Check ResourceManager address:

``` bash
hadoop getconf -confKey yarn.resourcemanager.address
```

Expected:

``` text
localhost:8032
```

If all of these work, the laptop is ready for Hadoop exercises.

------------------------------------------------------------------------

# 22. Useful Daily Startup/Shutdown

You do **not** need to reinstall Hadoop for every exercise.

### Start

``` bash
start-dfs.sh
start-yarn.sh
```

### Check

``` bash
jps
```

### Work

``` bash
hdfs dfs ...
yarn ...
hadoop ...
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
Java             : OpenJDK 21
Python           : Python 3
Hadoop           : Apache Hadoop 3.5.0
Storage          : HDFS
Resource Manager : YARN
Processing       : MapReduce
SSH              : OpenSSH
Deployment       : Single-node / pseudo-distributed
Machines         : 1 laptop
```

This setup is intended specifically for learning Hadoop on a single
laptop. A multi-machine cluster is not required.
