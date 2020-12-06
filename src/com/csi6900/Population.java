package com.csi6900;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Population
{
    // Map of individuals (network) to their fitness (double). Fitness should be maximized.
    private ArrayList<Network> individuals = new ArrayList<>();
    private int hostCount = 1;
    private int switchCount = 1;

    public Population() throws Exception
    {
        if (!FileIO.CreateDirectory("logs") || !FileIO.CreateSubDirectory("logs", "testRuns"))
        {
            throw new Exception("Cannot create directories 'logs/testRuns'");
        }

        for (int i = 0; i < Config.getPopulationSize(); i++)
        {
            var randomNetwork = GenerateRandomNetwork();
            individuals.add(randomNetwork);
        }
    }

    public ArrayList<Network> TournamentSelection()
    {
        var selection = new ArrayList<Network>();
        for (var i = 0; i < Config.getSelectionSize(); i++)
        {
            var selected = Randomize.selectFromList(individuals, Config.getTournamentK());
            var highestFitness = -1.0;
            var highestIndex = 0;
            for (var j = 0; j < selected.size(); j++)
            {
                var networkFitness = selected.get(j).getFitness();
                if (networkFitness > highestFitness)
                {
                    highestFitness = networkFitness;
                    highestIndex = j;
                }
            }

            selection.add(selected.get(highestIndex));
        }

        return selection;
    }

    public void CreateNewGeneration(ArrayList<Network> offsprings) throws Exception
    {
        individuals = new ArrayList<>();
        for (var offspring : offsprings)
        {
            individuals.add(offspring);
        }

        var remainder = Config.getPopulationSize() - offsprings.size();
        for (int i = 0; i < remainder; i++)
        {
            var randomNetwork = GenerateRandomNetwork();
            individuals.add(randomNetwork);
        }
    }

    public void CalculateFitness() throws Exception
    {
        var topoFilePath = Config.getnetworkTopologyPath();
        var topoFileBackupPath = topoFilePath + ".backup";
        if (!FileIO.CopyFile(topoFilePath, topoFileBackupPath))
        {
            throw new Exception("Cannot back up file '"+ topoFilePath + "'");
        }

        for (int i = 0; i < individuals.size(); i++)
        {
            DeleteLogs();

            WriteNetworkToTopology(individuals.get(i), topoFilePath, topoFileBackupPath);
            RunTest(i + 1);

            var resultPath = GetTestResultFilePath();
            var fitness = GetFitness(resultPath);
            individuals.get(i).setFitness(fitness);
        }

        if (!FileIO.CopyFile(topoFileBackupPath, topoFilePath))
        {
            throw new Exception("Cannot replace back up file '" + topoFileBackupPath + "'");
        }
    }

    private void RunTest(int networkIndex) throws Exception
    {
        var testPaths = Config.getTestOnTestPath().split("/");
        var testName = testPaths[testPaths.length - 1];
        var command = Config.getTestOnBinPath() + "cli.py";

        try
        {
            ProcessBuilder builder = new ProcessBuilder(command, "run " + testName);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            var fw = new FileWriter("logs/testRuns/network" + networkIndex + ".log");
            var newLine = System.getProperty("line.separator");
            String line;
            while ((line = reader.readLine()) != null)
            {
                fw.write(line + newLine);
                fw.flush();
            }

            fw.close();
        }
        catch (IOException e)
        {
            Logger.Error("Could not run test. Details: " + e.getMessage());
            throw e;
        }
    }

    private void WriteNetworkToTopology(Network network, String topoFilePath, String topoFileBackupPath) throws FileNotFoundException
    {
        var fw = new PrintStream(new File(topoFilePath));
        Scanner scanner = new Scanner(new File(topoFileBackupPath));
        var capture = false;
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (line.isBlank())
                continue;

            if (capture && !line.startsWith("\t") && !line.startsWith(" "))
                capture = false;

            if (line.startsWith("class MyTopo"))
            {
                capture = true;
                WriteNewTopology(fw, network);
            }

            if (!capture)
                fw.println(line);
        }
        scanner.close();
        fw.close();
    }

    private void WriteNewTopology(PrintStream printStream, Network network)
    {
        printStream.println("class MyTopo( Topo ):");
        printStream.println("    def __init__( self ):");
        printStream.println("        Topo.__init__( self )");

        for (var h: network.getHosts())
        {
            printStream.println("        " + h.getName() + " = self.addHost( '" + h.getName() + "', ip='" + h.getIpAddress() + "' )");
        }

        for (var s: network.getSwitches())
        {
            printStream.println("        " + s.getName() + " = self.addSwitch( '" + s.getName() + "' )");
        }

        for (var src: network.getLinks().entrySet())
        {
            for (var dest: src.getValue())
            {
                printStream.println("        self.addLink( " + src.getKey().getName() + ", " + dest.getName() + " )");
            }
        }

        printStream.println("        topos = { 'mytopo': ( lambda: MyTopo() ) }");
    }

    private String GetTestResultFilePath()
    {
        var fileLists = new File(Config.getTestOnLogsPath()).listFiles(File::isDirectory);
        if (fileLists.length != 1)
        {
            throw new IndexOutOfBoundsException("There must only be 1 directory in TestON/logs/ folder.");
        }
        var files = fileLists[0].listFiles((dir, name) -> name.endsWith("Result.txt"));
        if (files.length != 1)
        {
            throw new IndexOutOfBoundsException("There must only be 1 '*Result.txt' file in '" + fileLists[0].getAbsolutePath() + "'.");
        }

        return files[0].getAbsolutePath();
    }

    private void DeleteLogs() throws Exception
    {
        var fileLists = new File(Config.getTestOnLogsPath()).listFiles(File::isDirectory);
        for (var file : fileLists)
        {
            FileUtils.deleteDirectory(file);
        }
    }

    private double GetFitness(String fileName) throws Exception
    {
        double executedNb = -1;
        double failedNb = -1;
        double totalNb = -1;

        Scanner scanner = new Scanner(new File(fileName));
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if (line.startsWith("[Total]"))
            {
                var values = line.split(" ");
                for (String value : values)
                {
                    if (value.startsWith("[Total]"))
                    {
                        var total = value.split(":");
                        totalNb = Double.parseDouble(total[1]);
                    }

                    if (value.startsWith("[Executed]"))
                    {
                        var executed = value.split(":");
                        executedNb = Double.parseDouble(executed[1]);
                    }

                    if (value.startsWith("[Failed]"))
                    {
                        var failed = value.split(":");
                        failedNb = Double.parseDouble(failed[1]);
                    }
                }
                break;
            }
        }

        if (executedNb == -1 || failedNb == -1 || totalNb == -1)
        {
            throw new Exception("Could not calculate fitness for file " + fileName);
        }

        var alpha = Config.getAlpha();
        var fitness = alpha * (failedNb / executedNb) + (1.0 - alpha) * (executedNb / totalNb);
        return fitness;
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

        var networkCount = 1;
        for (var network : individuals)
        {
            gv.getGraph(network.getGraphOutput(), "network" + networkCount++);
            network.getGraphOutput();
        }
    }
}
