package com.csi6900;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

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
            // TODO: Send this network to be run and get back the Result.txt
            // CreateTopology(randomNetwork);
            // var currFileName = RunTopo();
            var fitness = GetFitness("/Users/matin.mansouri/csi6900/SDN/FUNCflowResult.txt");
            individuals.put(randomNetwork, fitness);
        }
    }

    private double GetFitness(String fileName) throws Exception
    {
        int executedNb = -1;
        int failedNb = -1;
        Scanner scanner = new Scanner(new File(fileName));
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (line.startsWith("[Total]"))
            {
                var values = line.split(" ");
                for (String value : values)
                {
                    if (value.startsWith("[Executed]"))
                    {
                        var executed = value.split(":");
                        executedNb = Integer.parseInt(executed[1]);
                    }

                    if (value.startsWith("[Failed]"))
                    {
                        var failed = value.split(":");
                        failedNb = Integer.parseInt(failed[1]);
                    }
                }
                break;
            }
        }

        if (executedNb == -1 || failedNb == -1)
        {
            throw new Exception("Could not calculate fitness for file " + fileName);
        }

        return (double) failedNb / executedNb;
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
