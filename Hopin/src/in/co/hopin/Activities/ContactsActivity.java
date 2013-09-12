package in.co.hopin.Activities;

import in.co.hopin.R;
import in.co.hopin.Fragments.ContactListFragment;
import in.co.hopin.HelperClasses.CommunicationHelper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ContactsActivity extends FragmentActivity {

    FragmentManager fm = this.getSupportFragmentManager();
    ImageButton refreshButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactlist_layout);
        refreshButton = (ImageButton)findViewById(R.id.contactslist_layout_refresh);
        refreshButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showContactListLayout();				
			}
		});
        showContactListLayout();
    }

    @Override
    public void onResume(){
        super.onResume();        
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
