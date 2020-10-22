package com.csi6900;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Population
{
    // Map of individuals (network) to their fitness (double)
    // Fitness should be minimized.
    // i.e.: Trying to minimize f = passedTests/totalTests
    private HashMap<Network, Double> individuals = new HashMap<>();
    private int hostCount = 1;
    private int switchCount = 1;

    public Population() throws Exception
    {
        for (int i = 0; i < Config.getPopulationSize(); i++)
        {
            var randomNetwork = GenerateRandomNetwork();
            var fitness = Randomize.generateDouble() * 0.025d + 0.49d;
            individuals.put(randomNetwork, fitness);
        }
    }

    private Network GenerateRandomNetwork() throws Exception
    {
        var networkBuilder = new Network.Builder();
        for (int i = 0; i < Randomize.generateInteger(1, Config.getMaxHostGenerationCount()); i++)
        {
            var hostName = "h" + hostCount++;
            hostName = hostName.replace("-", "");
            networkBuilder.withHost(hostName);
        }

        for (int i = 0; i < Randomize.generateInteger(1, Config.getMaxSwitchGenerationCount()); i++)
        {
            var switchName = "s" + switchCount++;
            switchName = switchName.replace("-", "");
            networkBuilder.withSwitch(switchName);
        }
        var network = networkBuilder.build();

        for (var h : network.getHosts())
        {
            Randomize.addRandomLinks(network, h);
        }

        for (var s : network.getSwitches())
        {
            Randomize.addRandomLinks(network, s);
        }

        return network;
    }

    public void Graph()
    {
        var datetime = LocalDateTime.now();
        var timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss").format(datetime);
        var gv = new GraphViz(timestamp);

        var it = individuals.entrySet().iterator();
        var networkCount = 1;
        while (it.hasNext())
        {
            var network = it.next().getKey();
            gv.getGraph(network.getGraphOutput(), "network" + networkCount++);
            network.getGraphOutput();
        }
    }
}
