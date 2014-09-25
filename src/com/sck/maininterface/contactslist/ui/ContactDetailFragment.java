

package com.sck.maininterface.contactslist.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.provider.ContactsContract.Data;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.HeterogeneousExpandableList;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sck.maininterface.BuildConfig;
import com.sck.maininterface.R;
import com.sck.maininterface.contactslist.HomeActivity;
import com.sck.maininterface.contactslist.util.ImageLoader;
import com.sck.maininterface.contactslist.util.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.Inflater;

import android.R.*;

public class ContactDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_CONTACT_URI =
            "com.example.android.contactslist.ui.EXTRA_CONTACT_URI";
    private static final String TAG = "ContactDetailFragment";
    private static final String GEO_URI_SCHEME_PREFIX = "geo:0,0?q=";
    private Uri mContactUri; // Stores the contact Uri for this fragment instance
    private Uri newContactUri;
    private ImageLoader mImageLoader; // Handles loading the contact image in a background thread
    private ImageView mImageView;
    private LinearLayout mDetailsLayout;
    private LinearLayout mInviteLayout;
    private TextView mEmptyView;
    private TextView  mTextNumber;
    private TextView mContactName;
    private MenuItem mEditContactMenuItem;



    public static ContactDetailFragment newInstance(Uri contactUri) {

        final ContactDetailFragment fragment = new ContactDetailFragment();
        final Bundle args = new Bundle();
       args.putParcelable(EXTRA_CONTACT_URI, contactUri);

        fragment.setArguments(args);
        return fragment;
    }
    
    
    public ContactDetailFragment() {}

    public void setContact(Uri contactLookupUri) {
        if (Utils.hasHoneycomb()) {
            mContactUri = contactLookupUri;
        } else {
         
        	//mContactUri= ContactsContract.Contacts.CONTENT_URI);
         mContactUri = Contacts.lookupContact(getActivity().getContentResolver(),
                    contactLookupUri);
        }

        // If the Uri contains data, load the contact's image and load contact details.
        if (contactLookupUri != null) {
            // Asynchronously loads the contact image
            mImageLoader.loadImage(mContactUri, mImageView);

            mImageView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);

            // Shows the edit contact action/menu item
            if (mEditContactMenuItem != null) {
                mEditContactMenuItem.setVisible(true);
            }
       getLoaderManager().restartLoader(ContactDetailQuery.QUERY_ID, null, this);
            //getLoaderManager().restartLoader(ContactAddressQuery.QUERY_ID, null, this);
          getLoaderManager().restartLoader(ContactNumberQuery.QUERY_ID,null,this) ;
        }
        else {
            mImageView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            mDetailsLayout.removeAllViews();
            mInviteLayout.removeAllViews();
            if (mContactName != null) {
                mContactName.setText("");
            }
            if (mEditContactMenuItem != null) {
                mEditContactMenuItem.setVisible(false);
            }
        }
        
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
       
        mImageLoader = new ImageLoader(getActivity(), getLargestScreenDimension()) {
            @Override
            protected Bitmap processBitmap(Object data) {
                return loadContactPhoto((Uri) data, getImageSize());

            }
        };
        mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_180_holo_light);
        mImageLoader.setImageFadeIn(false);

        
    }


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View detailView =
                inflater.inflate(R.layout.m_contact_detail_fragment, container, false);

       
        mImageView = (ImageView) detailView.findViewById(R.id.contact_image);
        mDetailsLayout = (LinearLayout) detailView.findViewById(R.id.contact_details_layout);
        mEmptyView = (TextView) detailView.findViewById(android.R.id.empty);
        mInviteLayout = (LinearLayout) detailView.findViewById(R.id.invite_details_layout);
  
        return detailView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {

            setContact(getArguments() != null ?
                    (Uri) getArguments().getParcelable(EXTRA_CONTACT_URI) : null);
        } 
        else {

            setContact((Uri) savedInstanceState.getParcelable(EXTRA_CONTACT_URI));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saves the contact Uri
        outState.putParcelable(EXTRA_CONTACT_URI, mContactUri);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_contact:

                Intent intent = new Intent(Intent.ACTION_EDIT, mContactUri);
                intent.putExtra("finishActivityOnSaveCompleted", true);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contact_detail_menu, menu);

        mEditContactMenuItem = menu.findItem(R.id.menu_edit_contact);

        mEditContactMenuItem.setVisible(mContactUri != null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
        case ContactDetailQuery.QUERY_ID:
        	
        	return new CursorLoader(getActivity(),mContactUri,
                        ContactDetailQuery.PROJECTION,
                        null, null, null);
        	
        case ContactNumberQuery.QUERY_ID:
        	  final Uri uri = Uri.withAppendedPath(mContactUri,ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
          	
        	  return new CursorLoader(getActivity(),uri,
                      ContactNumberQuery.PROJECTION,
                      ContactNumberQuery.SELECTION, null, null);  
               	
        }
        
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mContactUri == null) {
            return;
        }

        switch (loader.getId()) {
            case ContactDetailQuery.QUERY_ID:
                // Moves to the first row in the Cursor
                if (data.moveToFirst()) {
                    final String contactName = data.getString(ContactDetailQuery.DISPLAY_NAME);
                 
                       getActivity().setTitle(contactName);
                }
                break;
            case ContactNumberQuery.QUERY_ID:
            	 final LinearLayout.LayoutParams layoutParams =
                 new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                         ViewGroup.LayoutParams.WRAP_CONTENT);
            	 mDetailsLayout.removeAllViews();
            	 mInviteLayout.removeAllViews();
            	 
            	 if (data.moveToFirst()) {
                         // Builds the address layout
                         final LinearLayout layout = buildContactNo(
                                 data.getInt(ContactNumberQuery.TYPE),
                                 data.getString(ContactNumberQuery.LABEL),
                                 data.getString(ContactNumberQuery.CONNUMBER));
                         final LinearLayout layoutInvite = buildInviteLog();
                         // Adds the new address layout to the details layout
                         mDetailsLayout.addView(layout, layoutParams);
                         mInviteLayout.addView(layoutInvite,layoutParams);
                      
                 } 
            	 else 
                 {
                     // If nothing found, adds an empty address layout
                     mDetailsLayout.addView(buildEmptyContactNo(), layoutParams);
                 }
                 break;

            	 
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nothing to do here. The Cursor does not need to be released as it was never directly
        // bound to anything (like an adapter).
    }

    /*private LinearLayout buildEmptyAddressLayout() {
        return buildAddressLayout(0, null, null);
    }
    */
	private LinearLayout buildEmptyContactNo(){
    	
		return buildContactNo(0,null,null);
	}
	
     // ********************* method that assigns number and type for textviews and labels ***************	
    
	  private LinearLayout buildContactNo(int contactType,String contactTypeLabel,final String conNumber)
    {
    	
    	final LinearLayout numberLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.m_contact_detail_item, mDetailsLayout,false);
    	final TextView headerTextView= (TextView) numberLayout.findViewById(R.id.contact_detail_header);
    	final TextView numberTextView= (TextView) numberLayout.findViewById(R.id.contact_detail_item);
    	
    	final ImageButton callNumber = (ImageButton) numberLayout.findViewById(R.id.button_make_call); 
    	final ImageButton msgNumber = (ImageButton) numberLayout.findViewById(R.id.button_make_message);
    	//---
    	
    	//final LinearLayout 
    	if(contactType==0 && contactTypeLabel == null){
    		
    		
    		headerTextView.setVisibility(View.GONE);
    		numberTextView.setText(R.string.no_number);
    	}
    	else{
    		
    	//	CharSequence label = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), contactType, contactTypeLabel);
    		CharSequence label = StructuredPostal.getTypeLabel(getResources(), contactType, contactTypeLabel);
    		headerTextView.setText(label);
    		numberTextView.setText(conNumber);
    	
    		callNumber.setOnClickListener(new View.OnClickListener() {
				
				@SuppressLint("InlinedApi")
				@Override
				public void onClick(View arg0) {
					
		            Intent call = new Intent(Intent.ACTION_VIEW, mContactUri);
		           
		            call.putExtra("finishActivityOnSaveCompleted", true);
		            startActivity(call);
				}
			});
    		
    		msgNumber.setOnClickListener(new View.OnClickListener() {
    			
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					  Intent message = new Intent();
					//  message.setComponent(new ComponentName("com.example.android.contactlist","com.example.android.contactlist.HomeActivity.class"));
			        //  startActivity(message);
					//v.getContext().startActivity(message);
				}
			});
            

    		

    	}
    	
    	return numberLayout;
    }  
	
	  
	  private LinearLayout buildInviteLog()
	  {
		  final LinearLayout inviteLayout =(LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.m_contact_detail_item_ib, mInviteLayout,false); 
	      final ImageButton inviteMail= (ImageButton) inviteLayout.findViewById(R.id.button_invite_mail);
	      final ImageButton inviteMsg =(ImageButton) inviteLayout.findViewById(R.id.button_invite_message);
	      final String emailAdd[] ={ ContactsContract.CommonDataKinds.Email.DATA};
		

  		inviteMail.setOnClickListener(new View.OnClickListener() {
				
				@SuppressLint("InlinedApi")
				@Override
				public void onClick(View arg0) {
					
		            Intent email = new Intent(Intent.ACTION_SEND,mContactUri);
		            email.putExtra(Intent.EXTRA_SUBJECT, "Invitation to Global messaging App");
		            email.putExtra(Intent.EXTRA_EMAIL,emailAdd);
		            email.putExtra(Intent.EXTRA_TEXT, "Hey I Started using Global Messaging App");
		          startActivity(email);
				}
			});
  		
  		inviteMsg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
		      /* Intent invMsg = new Intent(Intent.ACTION_SENDTO,Uri.withAppendedPath(mContactUri,ContactsContract.Contacts.Data.CONTENT_DIRECTORY));
		        invMsg.putExtra("sms_body", "value");
			    startActivity(invMsg);
			*/
				/* Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, "textMessage");
				//sendIntent.setType(HTTP.PLAIN_TEXT_TYPE); // "text/plain" MIME type
			
				//Verify that the intent will resolve to an activity
			//	if (sendIntent.resolveActivity(getPackageManager()) != null) {
				//    startActivity(sendIntent);*/
			}
		});
  		
  		return inviteLayout;
	  }	
//	  private LinearLayout buildInviteFriends(){
		  
		  
		  
	  //} 

	  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructs a geo scheme Uri from a postal address.
     *
     * @param postalAddress A postal address.
     * @return the geo:// Uri for the postal address.
     */
    private Uri constructGeoUri(String postalAddress) {
        // Concatenates the geo:// prefix to the postal address. The postal address must be
        // converted to Uri format and encoded for special characters.
        return Uri.parse(GEO_URI_SCHEME_PREFIX + Uri.encode(postalAddress));
    }


    private int getLargestScreenDimension() {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        return height > width ? height : width;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Bitmap loadContactPhoto(Uri contactUri, int imageSize) {

        if (!isAdded() || getActivity() == null) {
            return null;
        }

        // Instantiates a ContentResolver for retrieving the Uri of the image
        final ContentResolver contentResolver = getActivity().getContentResolver();
        AssetFileDescriptor afd = null;

        if (Utils.hasICS()) {
            try {
                Uri displayImageUri = Uri.withAppendedPath(contactUri, Photo.DISPLAY_PHOTO);
                afd = contentResolver.openAssetFileDescriptor(displayImageUri, "r");
                if (afd != null) {
                    return ImageLoader.decodeSampledBitmapFromDescriptor(
                            afd.getFileDescriptor(), imageSize, imageSize);
                }
            } catch (FileNotFoundException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Contact photo not found for contact " + contactUri.toString()
                            + ": " + e.toString());
                }
            } finally {
                if (afd != null) {
                    try {
                        afd.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        try {
            Uri imageUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);
            afd = getActivity().getContentResolver().openAssetFileDescriptor(imageUri, "r");
            if (afd != null) {
                return ImageLoader.decodeSampledBitmapFromDescriptor(
                        afd.getFileDescriptor(), imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Contact photo not found for contact " + contactUri.toString()
                        + ": " + e.toString());
            }
        } finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                }
            }
        }

        return null;
    }

    /**
     * This interface defines constants used by contact retrieval queries.
     */
    public interface ContactDetailQuery {
        // A unique query ID to distinguish queries being run by the
        // LoaderManager.
        final static int QUERY_ID = 1;

        // The query projection (columns to fetch from the provider)
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {
        	Contacts._ID,
        	Contacts.DISPLAY_NAME,
        	
        //	ContactsContract.Contacts._ID, 1.1working
          // ContactsContract.Contacts.Data.DATA1, 1.2 working 
       // 	ContactsContract.CommonDataKinds.Phone._ID, 2.1 works
         //   ContactsContract.CommonDataKinds.Phone.NUMBER, 2.2 works
        	
        	//StructuredName._ID, works
    		//StructuredName.DATA1, works
        };

        // The query column numbers which map to each value in the projection
        final static int ID = 0;
        final static int DISPLAY_NAME = 1;
    }
    public interface ContactNumberQuery{
    	final static int QUERY_ID=2;
    	
    	// The query projection (columns to fetch from the provider)
      
    	final static String[] PROJECTION ={
        	ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.LABEL,
    		
    	};
        final static String SELECTION= ContactsContract.CommonDataKinds.Phone.NUMBER;
    	
    	final static int ID= 0;
    	final static int CONNUMBER=1;
    	final static int TYPE  =2;
     	final static int LABEL =3;
    	
    }
    
    
    /*
     
        mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_180_holo_light);

        mImageLoader.setImageFadeIn(false);
        
        final LinearLayout inviteLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.contact_detail_item_ib, mInviteLayout,false);
        
    	
        final ImageButton imgBtn = (ImageButton) inviteLayout.findViewById(R.id.button_invite_mail);
        final Context con = getActivity();
        
        imgBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(Intent.ACTION_VIEW,null, null, HomeActivity.class);
				startActivity(in);
			}
		});
     */
}


