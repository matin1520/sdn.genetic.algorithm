# SDN Genetic Algorithm
This repository contains the algorithm used to generate SDNs (using genetic algorithms) that causes most test failures.

## Network validity criteria
### A network is considered to be valid while it respects the following criteria:
- Contains at least 1 switch node
- Contains at least 1 host node
- All nodes within a network are linked (connected) to another
- A switch cannot be connected to itself
- A host cannot be connected to another host
- Given a network of N nodes, there is a path (through links) to all N-1 nodes from any chosen node n within the network.
![Generic Valid Network](https://github.com/matin1520/sdn.genetic.algorithm/blob/master/documentation/readme/validNetwork.png)

## UML Diagram
![UML Diagram](https://github.com/matin1520/sdn.genetic.algorithm/blob/master/documentation/readme/umlDiagram.png)
