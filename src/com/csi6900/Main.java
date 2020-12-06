package com.csi6900;

public class Main
{
    public static void main(String[] args)
    {
        if (!Config.TryInitialize("app.config"))
            return;

        try
        {
            var population = new Population();

            for (var i = 0; i < Config.getGenerationNb(); i++)
            {
                System.out.println("Running Generation " + (i+1) + "...");
                population.Graph();
                population.CalculateFitness();
                var newSubPopulation = population.TournamentSelection();
                for (var network : newSubPopulation) {
                    var mutation = new Mutation(network);
                    mutation.MutateHosts();
                    mutation.MutateSwitches();
                }

                population.CreateNewGeneration(newSubPopulation);
            }
        }
        catch (Exception e)
        {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}