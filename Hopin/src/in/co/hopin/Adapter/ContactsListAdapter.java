package in.co.hopin.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import in.co.hopin.Fragments.Contact;
import in.co.hopin.HelperClasses.SBImageLoader;
import in.co.hopin.R;
import org.jivesoftware.smack.RosterEntry;

import java.util.List;

public class ContactsListAdapter extends BaseAdapter {

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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.contactlist_row, null);
        }

        TextView userName = (TextView) view.findViewById(R.id.contactlist_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.contactlist_image);
        RosterEntry rosterEntry = contacts.get(i).getRosterEntry();
        String name = rosterEntry.getName();
        String user = rosterEntry.getUser();
        String fbId = user.split("@")[0];

        userName.setText(name);
        String imageurl = "http://graph.facebook.com/" + fbId + "/picture?type=small";
        SBImageLoader.getInstance().displayImageElseStub(imageurl, imageView, R.drawable.userpicicon);
        return view;
    }
}
