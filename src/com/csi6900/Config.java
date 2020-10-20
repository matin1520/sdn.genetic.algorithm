package com.csi6900;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config
{
    private static Properties properties;

    private static double addSwitchProbability;
    private static double removeSwitchProbability;
    private static double addHostProbability;
    private static double removeHostProbability;
    private static int minimumLinkCount;
    private static int maximumLinkCount;

    public static double getAddSwitchProbability() { return addSwitchProbability; }
    public static double getRemoveSwitchProbability() { return removeSwitchProbability; }
    public static double getAddHostProbability() { return addHostProbability; }
    public static double getRemoveHostProbability() { return removeHostProbability; }
    public static int getMinimumLinkCount() { return minimumLinkCount; }
    public static int getMaximumLinkCount() { return maximumLinkCount; }

    public static boolean TryInitialize(String fileName)
    {
        properties = new Properties();
        InputStream inputStream;
        try
        {
            inputStream = new FileInputStream(fileName);
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("ERROR: Config file '" + fileName + "' not found");
            System.out.println(ex.getMessage());
            return false;
        }

        try
        {
            properties.load(inputStream);
        }
        catch (IOException ex)
        {
            System.err.println("ERROR: Exception caught while loading config");
            System.out.println(ex.getMessage());
            return false;
        }

        loadConfigs();
        return true;
    }

    private static void loadConfigs()
    {
        addSwitchProbability = Double.parseDouble(properties.getProperty("addSwitchProbability"));
        removeSwitchProbability = Double.parseDouble(properties.getProperty("removeSwitchProbability"));
        addHostProbability = Double.parseDouble(properties.getProperty("addHostProbability"));
        removeHostProbability = Double.parseDouble(properties.getProperty("removeHostProbability"));
        minimumLinkCount = Integer.parseInt(properties.getProperty("minimumLinkCount"));
        maximumLinkCount = Integer.parseInt(properties.getProperty("maximumLinkCount"));
    }
}
