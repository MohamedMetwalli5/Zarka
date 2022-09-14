![License](https://img.shields.io/badge/License-MIT%20-red.svg)
![Language](https://img.shields.io/badge/language-Java%20-orange.svg)

# Zarka
## The Topology for Zarka
![image](https://user-images.githubusercontent.com/58489322/188975883-3087e7ae-b692-41dd-818c-28810cbe50fb.png)

## Features
1. LSM-Tree needs is used as our storage data structure.
2. Consistent Hashing is used as our partition rebalancing strategy (Only adding a
new node should be supported).
3. Leaderless replication is used with configurable quorum sizes.

## The Operations and Data Flow
Zarka have two kinds of applications:
- Zarka Server: a node that holds data partitions and replicas. You should provide enough
logs in the console so your TA can see the state of the server (e.g., received requests,
incoming data, replicas created, …. etc.)
- Zarka Client: a command line interface where user can send two types of requests:
  - `add`( key, value)
  - `get`( key )
<br>
Both key and value are string values.
<br>
Zarka Client will pick any Zarka Server nodes at random and it will be the coordinator to
execute his request.

## The Configuration File
There is a configuration file used by all nodes to define system parameters such as:
- Number of Nodes
- TCP ports for the nodes
- Quorum Write
- Quorum Read
- Replication Factor

## Its Created Logo
![Zarka](https://user-images.githubusercontent.com/58489322/190138362-be8061e4-dc33-4b03-999d-4fa5e56525b9.png)
