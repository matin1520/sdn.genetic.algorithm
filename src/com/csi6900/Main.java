package com.csi6900;

public class Main
{
    public static void main(String[] args)
    {
        if (!Config.TryInitialize("app.config"))
            return;

        try
        {
            /*var network = new Network
                    .Builder()
                    .withSwitch("s1")
                    .withSwitch("s2")
                    .withSwitch("s3")
                    .withHost("h1")
                    .withHost("h2")
                    .withLink("h1", "s1")
                    .withLink("h2", "s3")
                    .withLinks("s1", "s2")
                    .withLink("s2", "s3")
                    .build();

            var gv = new GraphViz();
            var mutation = new Mutation(network);

            network.print();
            gv.getGraph(network.getGraphOutput(), "before");

            mutation.MutateSwitches();
            mutation.MutateHosts();

            System.out.println("--------------------");
            network.print();
            gv.getGraph(network.getGraphOutput(), "after");*/

            var population = new Population();
            //population.Graph();
        }
        catch (Exception e)
        {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}