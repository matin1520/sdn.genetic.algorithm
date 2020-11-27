package com.csi6900;

import java.util.HashSet;
import java.util.Set;

public class Mutation
{
    private Network network;
    private static int addedSwitchCount = 0;
    private static int addedHostCount = 0;

    public Mutation(Network n)
    {
        network = n;
    }

    public void MutateSwitches() throws Exception
    {
        if (Randomize.generateDouble() < Config.getAddSwitchProbability())
        {
            var addedSwitch = addRandomSwitch();
            if (addedSwitch == null)
                throw new Exception("Could not add a random switch.");

            Randomize.addRandomLinks(network, addedSwitch);
        }

        if (Randomize.generateDouble() < Config.getRemoveSwitchProbability())
        {
            removeRandomSwitch();
        }
    }

    public void MutateHosts() throws Exception
    {
        if (Randomize.generateDouble() < Config.getAddHostProbability())
        {
            var addedHost = addRandomHost();
            if (addedHost == null)
                throw new Exception("Could not add a random host.");

            Randomize.addRandomLinks(network, addedHost);
        }

        if (Randomize.generateDouble() < Config.getRemoveHostProbability())
        {
            removeRandomHost();
        }
    }

    private void removeRandomSwitch()
    {
        var switchToRemove = Randomize.selectFromSet(network.getSwitches());
        network.removeSwitch(switchToRemove);
        Logger.Info("Removed switch " + switchToRemove.getName());
    }

    private Switch addRandomSwitch()
    {
        var switchName = "sx" + ++addedSwitchCount;
        var switchToAdd = new Switch(switchName);

        if (network.addSwitch(switchToAdd))
        {
            Logger.Info("Added a random switch " + switchToAdd.getName());
            return switchToAdd;
        }

        return null;
    }

    private void removeRandomHost()
    {
        var hostToRemove = Randomize.selectFromSet(network.getHosts());
        network.removeHost(hostToRemove);
        Logger.Info("Removed host " + hostToRemove.getName());
    }

    private Host addRandomHost() throws Exception
    {
        var hostName = "hx" + ++addedHostCount;
        var hostToAdd = new Host(hostName, Randomize.getRandomIp());

        if (network.addHost(hostToAdd))
        {
            Logger.Info("Added a random host " + hostToAdd.getName());
            return  hostToAdd;
        }

        return null;
    }
}
