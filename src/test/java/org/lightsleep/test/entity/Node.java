// Contact.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

/**
 * The entity of Node table.
 */
public class Node extends Common {
    /** Parent ID */
    private int parentId;

    /** Name */
    private String name;

    /**
     * Constructs a Node.
     */
    public Node() {
    }

    /**
     * Constructs a Node.
     *
     * @param parentId the parent id
     * @param name the name
     */
    public Node(int parentId, String name) {
        this.parentId = parentId;
        this.name = name;
    }

    /**
     * @return the parentId
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
