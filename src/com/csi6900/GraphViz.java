package com.csi6900;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GraphViz
{
    private final String dirName = "graphs";

    public GraphViz()
    {
        var directory = new File(dirName);
        if (!directory.exists())
            directory.mkdir();
    }

    public void getGraph(HashMap<Node, Node> sortedLinks, String name)
    {
        var graphName = dirName + "/" + name;
        if (sortedLinks == null)
        {
            Logger.Warn("No links found to make a graph.");
            return;
        }

        var stringBuilder = new StringBuilder();
        stringBuilder.append("graph network {");
        stringBuilder.append("rankdir=LR;");
        stringBuilder.append("shape=diamond;");

        for (var kvp : sortedLinks.entrySet())
        {
            stringBuilder.append(kvp.getKey().getName() + " -- " + kvp.getValue().getName() + ";");
            if (kvp.getKey().getClass().isAssignableFrom(Host.class))
                stringBuilder.append(kvp.getKey().getName() + " [shape=circle, style=filled, fillcolor=cadetblue2]");
            else
                stringBuilder.append(kvp.getKey().getName() + " [shape=circle, style=filled, fillcolor=chartreuse2]");

            if (kvp.getValue().getClass().isAssignableFrom(Host.class))
                stringBuilder.append(kvp.getValue().getName() + " [shape=circle, style=filled, fillcolor=cadetblue2]");
            else
                stringBuilder.append(kvp.getValue().getName() + " [shape=circle, style=filled, fillcolor=chartreuse2]");
        }

        stringBuilder.append("}");

        try (var fw = new FileWriter(graphName + ".gv"))
        {
            fw.write(stringBuilder.toString());
        }
        catch (Exception e)
        {
            Logger.Warn("Could not generate '" + graphName + ".gv' file. Details: " + e.getMessage());
            return;
        }

        GenerateGraphImage(graphName);
    }

    private void GenerateGraphImage(String graphName)
    {
        try
        {
            Runtime.getRuntime().exec("dot -Tpng " + graphName + ".gv -o " + graphName + ".png");
        }
        catch (IOException e)
        {
            Logger.Warn("Could not generate graph. Details: " + e.getMessage());
        }
    }
}
