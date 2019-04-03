package com.example.wurood.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private List<Contact> allItems;
    private ContactAdapter mAdapter;
    private Callback mCallBack;
    private List<Contact> rowListItem;
    private static final int LOADER_ID = 1;
    private static final int REQUEST_PERMISSION = 2001;
    DBHelper mDBHelper;
    Bundle mBundle;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindArray(R.array.array_of_Choice)
    String[] choices;
    @BindView(R.id.innerRecyclerView)
    RecyclerView mViewRecycle;


    public HomeFragment() {
    }

    public void setDatabase(DBHelper d) {
        mDBHelper = d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        ButterKnife.bind(this, view);
        mBundle = savedInstanceState;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{
                            Manifest.permission.READ_CONTACTS
                    },
                    REQUEST_PERMISSION
            );
        } else {
            getLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
        }
        init();
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length != 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLoaderManager().initLoader(LOADER_ID, mBundle, this);
                } else {
                    getActivity().finish();
                }
            }
        }
    }

    private void init() {

        mDBHelper = new DBHelper(getContext());
        allItems = new ArrayList<Contact>();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rowListItem = getAllItemList();
        mAdapter = new ContactAdapter(getActivity(), rowListItem, rowListItem);
        mAdapter.setHomeListener(mCallBack);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                String NAME = allItems.get(position).getName();
                mDBHelper.deleteContact(NAME);
                allItems.remove(position);
                ((MainActivity) getActivity()).removeSelected(NAME);
                mAdapter.notifyDataSetChanged();


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    public void notifyChange(int pos) {
        allItems.remove(pos);
        mAdapter.notifyDataSetChanged();
    }

    public void onRemovedFromFavorite(int position, List<Contact> favoriteList, Contact obj) {
        UpdateCard(obj.getName());
        favoriteList.remove(position);
        mAdapter.notifyDataSetChanged();


    }
    public void notifychange(){
        mAdapter.setl(mDBHelper.allContacts());
        mAdapter.notifyDataSetChanged();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallBack = (Callback) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeFragment.Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    public List<Contact> getAllItemList() {

        allItems = mDBHelper.allContacts();
        return allItems;
    }

    public void UpdateCard(String name) {
        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i).getName().equals(name)) {
                allItems.get(i).setPhotoFav(R.mipmap.ic_home);

            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                return new android.support.v4.content.CursorLoader(getContext(), CONTENT_URI, null, null, null, null);
            default:
                if (BuildConfig.DEBUG)
                    throw new IllegalArgumentException("no id handled!");
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<String> names = new ArrayList<>(), numbers = new ArrayList<>(), emails = new ArrayList<>();
        ArrayList<byte[]> photos = new ArrayList<>();
        // mAdapter.setCursor(cursor);
        String phoneNo = null;
        String email = null;
        String image_thumb = null;
        Bitmap bit_thumb = null;
        long id = 0;
        byte[] img = new byte[0];
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                id = cur.getLong(cur
                        .getColumnIndex(ContactsContract.Contacts._ID));

                String name = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer
                        .parseInt(cur.getString(cur
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[]{String.valueOf(id)}, null);
                    assert pCur != null;
                    while (pCur.moveToNext()) {
                        phoneNo = pCur
                                .getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        image_thumb = pCur
                                .getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));


                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{String.valueOf(id)}, null);
                        while (emailCur.moveToNext()) {
                            email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));


                        }
                        emailCur.close();
                    }
                    pCur.close();
                }
                try {
                    if (image_thumb != null) {
                        bit_thumb = MediaStore.Images.Media.getBitmap(cr, Uri.parse(image_thumb));
                        img = getBytes(bit_thumb);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                names.add(name);
                emails.add(email);
                numbers.add(phoneNo);
                photos.add(img);

                email = null;
                img = null;
                phoneNo = null;
            }

        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!prefs.getBoolean("state4", false)) {
            // run your one time code
            database(names, emails, numbers, photos);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("state4", true);
            editor.apply();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Callback received when a permissions request has been completed.
     */


    public interface Callback {
        void onCardSelected(int position, String contactName, String contactNumber, Bitmap contactImage, long contactId, int favoriteImage, String contactEmail);

        void onItemClick(int position, Contact obj,ImageView image);

        void onItemLongClick(int position, Contact obj);


    }


    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }


    public void database(ArrayList<String> name, ArrayList<String> email, ArrayList<String> phone, ArrayList<byte[]> image) {
        for (int i = 0; i < name.size(); i++) {
            mDBHelper.addContacts(name.get(i), email.get(i), phone.get(i), image.get(i));

        }

    }


}
