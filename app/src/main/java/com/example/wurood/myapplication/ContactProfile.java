package com.example.wurood.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wurood on 7/10/2018.
 */

public class ContactProfile extends Fragment {
    @BindView(R.id.contact_profile_photo)
    ImageView contactPhoto;
    @BindView(R.id.contact_profile_name)
    TextView contactName;
    @BindView(R.id.user_profile_short_bio)
    TextView contactInfo;
    @BindView(R.id.name_card)
    TextView NAME;
    @BindView(R.id.number_card)
    TextView NUMBER;

    @BindView(R.id.email_card)
    TextView EMAIL;
    Bitmap bmp;

    public ContactProfile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String name = getArguments().getString("contactName");
        String email = getArguments().getString("contactEmail");
        String number = getArguments().getString("contactNumber");
        byte[] photo = getArguments().getByteArray("contactPhoto");
        ButterKnife.bind(this, view);
        setInformation(name, number, email, photo);
        Intent serviceIntent = new Intent(getContext(), MyService.class);
        getActivity().startService(serviceIntent);
    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void messageEventFromService(Message event){
//        contactInfo.setText(event.getMsg());
//    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//    @Override
//    public void onStop() {
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//    }



//    @Override
//    public void onResume() {
//        super.onResume();
//       // BusStation.getBus().register(this);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        //BusStation.getBus().unregister(this);
//    }



//    @Subscribe
//    public void recivedMessage(Message msg){
//       Toast.makeText(getContext(),msg.getMsg(),Toast.LENGTH_LONG).show();
//
//        contactInfo.setText(msg.getMsg());
//    }
    public void setInformation(String mName, String mNumber, String mEmail, byte[] mPhoto) {
        if (mName != null) {
            NAME.setText("NAME :" + mName);
            contactName.setText(mName);
        }
        if (mNumber != null) {
            NUMBER.setText("NUMBER :" + mNumber);
        } else NUMBER.setText("NUMBER :" + "-----");

        if (mEmail != null) {
            EMAIL.setText("EMAIL :" + mEmail);
        } else EMAIL.setText("EMAIL :" + "-----");
        if (mPhoto != null) {
            bmp = BitmapFactory.decodeByteArray(mPhoto, 0, mPhoto.length);
            contactPhoto.setImageBitmap(bmp);
            AsyncTask<Bitmap, Void, Palette> p = Palette.from(bmp).generate(new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette palette) {
                    Palette.Swatch vibrantSwatch = palette.getDarkMutedSwatch();
                    Palette.Swatch LightSwatch = palette.getLightVibrantSwatch();
                    if (vibrantSwatch != null) {
                        ((MainActivity) getActivity()).setToolbar(vibrantSwatch.getRgb(), vibrantSwatch.getTitleTextColor());
                    }
                }
            });
        }


    }


}
