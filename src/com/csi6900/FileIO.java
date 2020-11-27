package com.csi6900;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class FileIO
{
    public static boolean CreateDirectory(String dirName)
    {
        var directory = new File(dirName);
        if (!directory.exists())
            return directory.mkdir();

        return true;
    }

    public static boolean CreateSubDirectory(String dirName, String subDirName)
    {
        var dir = dirName + "/" + subDirName;
        var directory = new File(dir);
        if (!directory.exists())
            return directory.mkdir();

        return true;
    }

    public static boolean CopyFile(String src, String dest)
    {
        try
        {
            Files.copy(new File(src).toPath(), new File(dest).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e)
        {
            Logger.Error("Could not copy from '" + src + "' to '" + dest + "'");
            return false;
        }

        return true;
    }
}
