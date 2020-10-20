package com.csi6900;

import java.util.Objects;

public class Node
{
    private String name;

    public String getName() { return name; }

    public Node(String n)
    {
        name = n;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        final Node other = (Node) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
