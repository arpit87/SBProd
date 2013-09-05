package in.co.hopin.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import in.co.hopin.Fragments.ContactListFragment;
import in.co.hopin.HelperClasses.CommunicationHelper;
import in.co.hopin.R;

public class ContactsActivity extends FragmentActivity {

    FragmentManager fm = this.getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactlist_layout);
    }

    @Override
    public void onResume(){
        super.onResume();
        showContactListLayout();
    }

    public void showContactListLayout()
    {
        if (fm != null) {
            FragmentTransaction fragTrans = fm.beginTransaction();
            fragTrans.replace(R.id.contactslist_layout_content, new ContactListFragment());
            fragTrans.commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommunicationHelper.getInstance().authorizeCallback(this,requestCode, resultCode, data);
    }

    @Override
    public void onPause(){
        super.onPause();
        CommunicationHelper.getInstance().FBLoginpromptPopup_show(this, false);
    }
}
