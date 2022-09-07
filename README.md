# Zarka
## The Topology for Zarka
![image](https://user-images.githubusercontent.com/58489322/188975883-3087e7ae-b692-41dd-818c-28810cbe50fb.png)

## The Operations and Data Flow
Zarka have two kinds of applications:
- Zarka Server: a node that holds data partitions and replicas. You should provide enough
logs in the console so your TA can see the state of the server (e.g., received requests,
incoming data, replicas created, …. etc.)
- Zarka Client: a command line interface where user can send two types of requests:
  - `add`( key, value)
  - `get`( key )
Both key and value are string values.

- Zarka Client will pick any Zarka Server nodes at random and it will be the coordinator to
execute his request.

## The Configuration File
There will be a configuration file used by all nodes to define system parameters such as:
- Number of Nodes
- TCP ports for the nodes
- Quorum Write
- Quorum Read
- Replication Factor
