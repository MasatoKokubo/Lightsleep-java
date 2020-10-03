// Contact.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

/**
 * The entity of Leaf table.
 */
public class Leaf extends Node {
    /** Content */
    private String content;

    /**
     * Constructs a Leaf.
     */
    public Leaf() {
    }

    /**
     * Constructs a Leaf.
     *
     * @param parentId the parent id
     * @param name the name
     * @param content the content
     */
    public Leaf(int parentId, String name, String content) {
        super(parentId, name);
        this.content = content;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
}
