# SDN Genetic Algorithm
This repository contains the algorithm used to generate SDNs (using genetic algorithms) that causes most test failures.
To set up an environment for TestOn on Ubuntu 18.04, the following [instructions](https://github.com/matin1520/sdn.genetic.algorithm/blob/master/documentation/Ubuntu%2018.04%20Local%20Setup.pdf) maybe used.

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

## Mutation
The mutation types of the algorithm is the followings:
- Add a switch
- Remove a switch
- Add a host
- Remove a host

Each mutation type has a configurable probability of happening. The configurations are stored in *app.config* file. Note that there is no add/remove links operation. This is done implicitely through the 4 mutation types. Every time a switch or a host is added, there will be N numbers of link generated for that node, where N is a random number between 1 and the maximum possible links. Similarly, every time a switch. or a host is removed, all its links are also removed.

## Configuration
### app.config file
This file consists of the following configurations (as of now):

#### addSwitchProbability
This configuration sets the probability of adding a random switch to the network.
Value can be between 0.0 and 1.0

#### removeSwitchProbability
This configuration sets the probability of removing a random switch from the network.
Value can be between. 0.0 and 1.0

#### addHostProbability
This configuration sets the probability of adding a random host to the network.
Value can be between 0.0 and 1.0

#### removeHostProbability
This configuration sets the probability of removing a random host from the network.
Value can be between. 0.0 and 1.0

#### populationSize
This configuration sets the size of the population to be generated in the Genetic Algorithm

#### maxHostGenerationCount
This configuration sets the maximum number of hosts that can be generated for an individual network.
The number of hosts generated will be a random number between 1 and *maxHostGenerationCount*

#### maxSwitchGenerationCount
This configuration sets the maximum number of switches that can be generated for an individual network.
The number of switches generated will be a random number between 1 and *maxSwitchGenerationCount*
