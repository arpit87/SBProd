package in.co.hopin.Activities;

import android.app.ListActivity;

public class ContactsFragment extends ListActivity {
   /* public static final String TAG = "in.co.hopin.Activities.ContactsActivity";

    private final ChatServiceConnection mChatServiceConnection = new ChatServiceConnection();
    private IXMPPAPIs xmppApis;
    private boolean mBinded;
    private List<RosterEntry> contacts;


    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!mBinded) {
            bindToService();
        }
    }

    private void bindToService() {
        Logger.d(TAG, "binding chat to service");
        Intent i = new Intent(getApplicationContext(), SBChatService.class);
        getApplicationContext().bindService(i, mChatServiceConnection, BIND_AUTO_CREATE);
        mBinded = true;
    }

    protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id) {
        RosterEntry entry = contacts.get(position);
        String fbid = entry.getUser().split("@")[0];
        String name = entry.getName();
        //CommunicationHelper.getInstance().onChatClickWithUser(this,fbid,name);
    }


    private final class ChatServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName className, IBinder boundService) {
            Logger.d(TAG, "onServiceConnected called");
            xmppApis = IXMPPAPIs.Stub.asInterface(boundService);
            Logger.d(TAG, "service connected");
            try {
                final Roster roster = ((SBChatManager) xmppApis.getChatManager()).getXMPPConnection().getRoster();
                roster.addRosterListener(new RosterListener() {
                    @Override
                    public void entriesAdded(Collection<String> strings) {
                        Logger.d(TAG, "Entries added :" + strings.size());
                        updateUI(roster);
                    }

                    @Override
                    public void entriesUpdated(Collection<String> strings) {
                        Logger.d(TAG, "Entries updated :" + strings.size());
                        updateUI(roster);
                    }

                    @Override
                    public void entriesDeleted(Collection<String> strings) {
                        Logger.d(TAG, "Entries deleted :" + strings.size());
                        updateUI(roster);
                    }

                    @Override
                    public void presenceChanged(Presence presence) {
                        Logger.d(TAG, "Presence changed for" + presence.getFrom());
                    }
                }

                );

            } catch (RemoteException e) {
                Logger.d(TAG, e.getMessage());
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            xmppApis = null;
            Logger.d(TAG, "service disconnected");
        }

        public void updateUI(Roster roster) {
            RosterEntry[] entries = (RosterEntry[])roster.getEntries().toArray();
            for (RosterEntry entry : entries) {
                Logger.d(TAG, entry.toString());
            }
            final List<RosterEntry> contacts = Arrays.asList(entries);
            for (RosterEntry entry : contacts) {
                Logger.d(TAG, entry.toString());
            }
            ContactsFragment.this.contacts = contacts;

            final Handler handler = Platform.getInstance().getHandler();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ContactsListAdapter adapter = new ContactsListAdapter(ContactsFragment.this, contacts);
                            ContactsFragment.this.setListAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                    });

                }
            });

            t.start();
        }
    }
*/
}
