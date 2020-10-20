package com.csi6900;

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
            throw new Exception("(generateInteger) min value must be greater than max");

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
}
