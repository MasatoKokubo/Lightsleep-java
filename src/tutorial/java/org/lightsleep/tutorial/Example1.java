package org.lightsleep.tutorial;

import java.util.ArrayList;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.Transaction;
import org.lightsleep.tutorial.entity.Contact;

public class Example1 {
    public static void main(String[] args) {
        try {
            List<Contact> contacts = new ArrayList<>();
            Transaction.execute(conn -> {
                new Sql<>(Contact.class).connection(conn)
                    .select(contacts::add);
            });

            for (int index = 0; index < contacts.size(); ++index) {
                Contact contact = contacts.get(index);
                System.out.println(
                    index
                    + ": Name: " + contact.firstName + " " + contact.lastName
                    + ", Birthday: " + contact.birthday
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
