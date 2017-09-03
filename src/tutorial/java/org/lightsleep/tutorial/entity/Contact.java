package org.lightsleep.tutorial.entity;

import java.sql.Date;

import org.lightsleep.entity.*;

/**
 * Contact entity
 */
public class Contact {
    /** Contact ID */
    @Key
    public Integer id;

    /** First Name */
    public String firstName;

    /** Last Name */
    public String lastName;

    /** Birthday */
    public Date birthday;
}
