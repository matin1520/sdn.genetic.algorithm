package com.csi6900;

import javax.naming.NameNotFoundException;
import java.util.*;

public class Network
{
    private Set<Switch> switches;
    private Set<Host> hosts;
    private HashMap<Node, Set<Node>> links;

    public Set<Switch> getSwitches() { return switches; }
    public Set<Host> getHosts() { return hosts; }
    public HashMap<Node, Set<Node>> getLinks() { return links; }

    public boolean addSwitch(Switch s) { return switches.add(s); }
    public boolean addHost(Host h)
    {
        return hosts.add(h);
    }
    public void removeSwitch(Switch s)
    {
        removeNodeAndLinks(s);
    }
    public void removeHost(Host h)
    {
        removeNodeAndLinks(h);
    }

    public void addLink(Node nodeA, Node nodeB)
    {
        if (nodeA == nodeB)
            throw new IllegalStateException("Cannot link a node to itself.");

        var linkedValues = links.getOrDefault(nodeA, new HashSet<>());
        linkedValues.add(nodeB);
        links.put(nodeA, linkedValues);
    }

    public boolean areLinked(Node a, Node b)
    {
        var linksToA = links.get(a);
        var linksToB = links.get(b);

        if ((linksToA != null && linksToA.contains(b)) || (linksToB !=null && linksToB.contains(a)))
            return true;

        return false;
    }

    private void removeNodeAndLinks(Node n)
    {
        // remove all links from node n
        var it = links.entrySet().iterator();
        var mergedValues = new HashSet<>();
        while (it.hasNext())
        {
            var kvp = it.next();
            var key = kvp.getKey();
            if (key.equals(n))
            {
                it.remove();
                continue;
            }

            var value = kvp.getValue();
            if (value.contains(n))
            {
                value.remove(n);
                if (value.isEmpty())
                    it.remove();
            }

            mergedValues.addAll(value);
        }

        // Remove all dangling nodes
        switches.removeIf(s -> !links.containsKey(s) && !mergedValues.contains(s));
        hosts.removeIf(h -> !links.containsKey(h) && !mergedValues.contains(h));
    }

    private static Node findNodeByName(String name, Set<Switch> switches, Set<Host> hosts)
    {
        Node node = findSwitchByName(name, switches);
        if (node == null)
            node = findHostByName(name, hosts);

        return node;
    }

    private static Switch findSwitchByName(String name, Set<Switch> switches)
    {
        if (switches != null)
        {
            for (var n : switches)
            {
                var currentName = n.getName();
                if (currentName.equals(name))
                    return n;
            }
        }

        return null;
    }

    private static Host findHostByName(String name, Set<Host> hosts)
    {
        if (hosts != null)
        {
            for (var n : hosts)
            {
                var currentName = n.getName();
                if (currentName.equals(name))
                    return n;
            }
        }

        return null;
    }

    public HashMap<Node, Node> getGraphOutput()
    {
        HashMap<Node, Node> graphLinks = null;
        if (links != null && !links.isEmpty())
        {
            graphLinks = new HashMap<>();
            for(var key : links.keySet())
            {
                for(var value : links.get(key))
                {
                    graphLinks.put(key, value);
                }
            }
        }

        return graphLinks;
    }

    public void print()
    {
        if (switches != null && !switches.isEmpty())
        {
            System.out.println("*** Switches:");
            printNodes(switches);
            System.out.println();
        }

        if (hosts != null && !hosts.isEmpty())
        {
            System.out.println("*** Hosts:");
            printNodes(hosts);
            System.out.println();
        }

        if (links != null && !links.isEmpty())
        {
            System.out.println("*** Links:");
            for(var key : links.keySet())
            {
                for(var value : links.get(key))
                {
                    System.out.print("(" + key.getName() + ", " + value.getName() + ")");
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private <T extends Node> void printNodes(Set<T> nodes)
    {
        for(var s: nodes)
        {
            System.out.print(s.getName());
            System.out.print(" ");
        }
        System.out.println();
    }

    public static class Builder
    {
        private final Set<Switch> switches;
        private final Set<Host> hosts;
        private final HashMap<Node, Set<Node>> links;

        public Builder()
        {
            switches = new HashSet<>();
            hosts = new HashSet<>();
            links = new HashMap<>();
        }

        public Builder withSwitch(String name)
        {
            switches.add(new Switch(name));
            return this;
        }

        public Builder withHost(String name)
        {
            hosts.add(new Host(name));
            return this;
        }

        public Builder withLink(String nameA, String nameB) throws NameNotFoundException, IllegalStateException
        {
            var nodeA = findNodeByName(nameA, switches, hosts);
            var nodeB = findNodeByName(nameB, switches, hosts);

            if (nodeA == nodeB)
                throw new IllegalStateException("Cannot link a node to itself.");

            if (nodeA.getClass().isAssignableFrom(Host.class) && nodeB.getClass().isAssignableFrom(Host.class))
                throw new IllegalStateException("Cannot link a host to another host.");

            var linkedValues = links.getOrDefault(nodeA, new HashSet<>());
            linkedValues.add(nodeB);
            links.put(nodeA, linkedValues);

            return this;
        }

        public Builder withLinks(String nameA, String nameB) throws NameNotFoundException, IllegalStateException
        {
            var namesB = nameB.split(",");

            for (String n: namesB)
                withLink(nameA, n);

            return this;
        }

        public Network build()
        {
            var network = new Network();
            network.switches = this.switches;
            network.hosts = this.hosts;
            network.links = this.links;

            return network;
        }
    }
}
