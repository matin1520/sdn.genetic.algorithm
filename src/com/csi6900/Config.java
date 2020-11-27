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
    private static int maxSwitchGenerationCount;
    private static int maxHostGenerationCount;
    private static int populationSize;
    private static String testOnTestPath;
    private static String testOnLogsPath;
    private static String testOnBinPath;
    private static String networkTopologyPath;
    private static double alpha;

    public static double getAddSwitchProbability() { return addSwitchProbability; }
    public static double getRemoveSwitchProbability() { return removeSwitchProbability; }
    public static double getAddHostProbability() { return addHostProbability; }
    public static double getRemoveHostProbability() { return removeHostProbability; }
    public static int getMaxSwitchGenerationCount() { return maxSwitchGenerationCount; }
    public static int getMaxHostGenerationCount() { return maxHostGenerationCount; }
    public static int getPopulationSize() { return populationSize; }
    public static String getTestOnTestPath() { return testOnTestPath; }
    public static String getTestOnLogsPath() { return testOnLogsPath; }
    public static String getTestOnBinPath() { return testOnBinPath; }
    public static String getnetworkTopologyPath() { return networkTopologyPath; }
    public static double getAlpha() { return alpha; }

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
        maxSwitchGenerationCount = Integer.parseInt(properties.getProperty("maxSwitchGenerationCount"));
        maxHostGenerationCount = Integer.parseInt(properties.getProperty("maxHostGenerationCount"));
        populationSize = Integer.parseInt(properties.getProperty("populationSize"));
        testOnTestPath = properties.getProperty("testOnTestPath");
        networkTopologyPath = properties.getProperty("networkTopologyPath");
        testOnLogsPath = testOnTestPath.substring(0, testOnTestPath.indexOf("TestON")) + "TestON/logs/";
        testOnBinPath = testOnTestPath.substring(0, testOnTestPath.indexOf("TestON")) + "TestON/bin/";
        alpha = Double.parseDouble(properties.getProperty("alpha"));
    }
}
