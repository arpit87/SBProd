package in.co.hopin.Util;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

public class Contact implements Comparable<Contact> {

    private RosterEntry rosterEntry;
    private Presence presence;

    public RosterEntry getRosterEntry() {
        return rosterEntry;
    }

    public Contact(RosterEntry rosterEntry) {
        this.rosterEntry = rosterEntry;
    }

    public Presence getPresence() {
		return presence;
	}

	public void setPresence(Presence presence) {
		this.presence = presence;
	}

	@Override
    public int compareTo(Contact contact) {
        return rosterEntry.getName().compareTo(contact.rosterEntry.getName());
    }
}
