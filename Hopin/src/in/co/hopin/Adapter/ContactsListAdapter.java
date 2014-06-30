package in.co.hopin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.Users.Friend;
import in.co.hopin.Util.Contact;
import in.co.hopin.Util.Logger;
import in.co.hopin.Util.StringUtils;
import in.co.hopin.R;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import java.util.List;

public class ContactsListAdapter extends BaseAdapter {

	 private static final String TAG = "in.co.hopin.Adapter.ContactListAdapter";
    private LayoutInflater inflater;
    private List<Contact> contacts;

    public ContactsListAdapter(Activity activity, List<Contact> contacts) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    
    public void updateContents(List<Contact> contactList)
	{
    	contacts.clear();
    	contacts.addAll(contactList);
		notifyDataSetChanged();
	}

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.contactlist_row, null);
        }

        TextView userName = (TextView) view.findViewById(R.id.contactlist_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.contactlist_image);
        ImageView offline = (ImageView)view.findViewById(R.id.contactlist_status_offline); 
        ImageView online = (ImageView)view.findViewById(R.id.contactlist_status_online);
        RosterEntry rosterEntry = contacts.get(i).getRosterEntry();
        String name = rosterEntry.getName();
        String user = rosterEntry.getUser();
        String fbId = user.split("@")[0];

        userName.setText(name);
        String imageurl = StringUtils.getFBPicURLFromFBID(fbId);
        SBImageLoader.getInstance().displayImageElseStub(imageurl, imageView, R.drawable.userpicicon);
        Presence p = contacts.get(i).getPresence();
        Logger.d(TAG, "Presence of:"+p.getFrom()+" is "+ p.getType());
        if(p.getType() == Presence.Type.available)
        {
        	online.setVisibility(View.VISIBLE);
        	offline.setVisibility(View.INVISIBLE);
        }        
        else
        {
        	online.setVisibility(View.INVISIBLE);
        	offline.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
