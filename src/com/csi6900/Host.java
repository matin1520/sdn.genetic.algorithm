package com.csi6900;

public class Host extends Node
{
    private String ipAddress;

    public String getIpAddress() { return ipAddress; }

    public Host(String n, String ip)
    {
        super(n);
        ipAddress = ip;
    }
}
