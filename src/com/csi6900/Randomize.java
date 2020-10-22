package com.csi6900;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Randomize
{
    private static Random random = new Random();

    public static double generateDouble()
    {
        return random.nextDouble();
    }

    public static int generateInteger(int min, int max) throws Exception
    {
        if (min == max)
            return min;

        if (min > max)
            throw new Exception("min value must be greater than max");

        return random.nextInt(max - min) + min;
    }

    public static <T extends Node> T selectFromSet(Set<T> set)
    {
        var size = set.size();
        var item = random.nextInt(size);
        var i = 0;
        for(T obj : set)
        {
            if (i == item)
                return obj;
            i++;
        }

        throw new RuntimeException("Not able to choose a random object from a set. This should not happen!");
    }

    public static void addRandomLinks(Network network, Node node) throws Exception
    {
        var isHostNode = node.getClass().isAssignableFrom(Host.class);
        var switchCount = network.getSwitches().size();
        var hostCount = network.getHosts().size();

        int lowerBound = 1;
        int upperBound = isHostNode ? switchCount : (hostCount + switchCount - 1);
        int randomNbOfLinks = Randomize.generateInteger(lowerBound, upperBound);

        var combinedNodes = new HashSet<Node>();
        combinedNodes.addAll(network.getSwitches());

        if (!isHostNode)
        {
            combinedNodes.addAll(network.getHosts());
            combinedNodes.remove(node);
        }

        while (randomNbOfLinks > 0)
        {
            Node nodeToLink = Randomize.selectFromSet(combinedNodes);
            while (network.areLinked(node, nodeToLink))
            {
                combinedNodes.remove(nodeToLink);
                randomNbOfLinks--;

                if (randomNbOfLinks > 0)
                    nodeToLink = Randomize.selectFromSet(combinedNodes);
                else
                    break;
            }

            if (randomNbOfLinks > 0)
            {
                network.addLink(node, nodeToLink);
                combinedNodes.remove(nodeToLink);
                randomNbOfLinks--;

                Logger.Info("Added a random link between " + node.getName() + " and " + nodeToLink.getName());
            }
        }
    }
}
