package in.co.hopin.Fragments;

import org.jivesoftware.smack.RosterEntry;

public class Contact implements Comparable<Contact> {

    private RosterEntry rosterEntry;

    public RosterEntry getRosterEntry() {
        return rosterEntry;
    }

    public Contact(RosterEntry rosterEntry) {
        this.rosterEntry = rosterEntry;
    }

    @Override
    public int compareTo(Contact contact) {
        return rosterEntry.getName().compareTo(contact.rosterEntry.getName());
    }
}
