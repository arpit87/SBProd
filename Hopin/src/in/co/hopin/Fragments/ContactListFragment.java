package in.co.hopin.Fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import in.co.hopin.Adapter.ContactsListAdapter;
import in.co.hopin.ChatService.IXMPPAPIs;
import in.co.hopin.ChatService.SBChatManager;
import in.co.hopin.ChatService.SBChatService;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.HelperClasses.ProgressHandler;
import in.co.hopin.Platform.Platform;
import in.co.hopin.R;
import in.co.hopin.Util.Logger;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ContactListFragment extends ListFragment {

    private static final String TAG = "in.co.hopin.Fragments.ContactListFragment";
    private final ChatServiceConnection mChatServiceConnection = new ChatServiceConnection();
    private IXMPPAPIs xmppApis;
    private boolean mBinded;
    private List<Contact> contacts;
    //private HashMap<String,Contact> 
   
    ContactsListAdapter adapter = null;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(null);
        if (!mBinded) {
            bindToService();
        }
        ProgressHandler.showInfiniteProgressDialoge(getActivity(), "Please wait", "fetching contacts", null);
    }

    private void bindToService() {
        Logger.d(TAG, "binding chat to service");
        Intent i = new Intent(getActivity().getApplicationContext(), SBChatService.class);
        getActivity().getApplicationContext().bindService(i, mChatServiceConnection, Context.BIND_AUTO_CREATE);
        mBinded = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, null);
        ViewGroup listViewContainer = (ViewGroup) inflater.inflate(R.layout.contactfragment_listview, null);
        return listViewContainer;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        RosterEntry rosterEntry = contacts.get(position).getRosterEntry();
        String fbid = rosterEntry.getUser().split("@")[0];
        String name = rosterEntry.getName();
        CommunicationHelper.getInstance().onChatClickWithUser(getActivity(), fbid, name);
    }

    private final class ChatServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName className, IBinder boundService) {
            Logger.d(TAG, "onServiceConnected called");
            xmppApis = IXMPPAPIs.Stub.asInterface(boundService);
            Logger.d(TAG, "service connected");
            try {
                final Roster roster = ((SBChatManager) xmppApis.getChatManager()).getXMPPConnection().getRoster();
                updateUI();
                roster.addRosterListener(new RosterListener() {
                    @Override
                    public void entriesAdded(Collection<String> strings) {
                        Logger.d(TAG, "Entries added :" + strings.size());
                        //updateUI(roster);
                    }

                    @Override
                    public void entriesUpdated(Collection<String> strings) {
                        Logger.d(TAG, "Entries updated :" + strings.size());
                        //updateUI(roster);
                    }

                    @Override
                    public void entriesDeleted(Collection<String> strings) {
                        Logger.d(TAG, "Entries deleted :" + strings.size());
                        //updateUI(roster);
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

        public void updateUI() {
        	Roster roster;
        	ProgressHandler.dismissDialoge();
        	try {
				roster = ((SBChatManager) xmppApis.getChatManager()).getXMPPConnection().getRoster();			
        	 //safe to call even if not showing
            int entryCount = roster.getEntryCount();
            List<Contact> contacts = new ArrayList<Contact> (entryCount);
            for (RosterEntry rosterEntry : roster.getEntries()) {
            	Contact c= new Contact(rosterEntry);
            	c.setPresence(roster.getPresence(rosterEntry.getUser()));
                contacts.add(c);                
            }

            Collections.sort(contacts);
            ContactListFragment.this.contacts = contacts;
            if(adapter == null)
            {
            	adapter = new ContactsListAdapter(getActivity(), ContactListFragment.this.contacts);
            	setListAdapter(adapter);
            }
            else
            {
	            adapter.updateContents(contacts);
	            adapter.notifyDataSetChanged();
            }
            } catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           
        }
    }
}
