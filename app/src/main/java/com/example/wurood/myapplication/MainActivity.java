package com.example.wurood.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.Callback, FavoritesFragment.CallbackFavorites, View.OnClickListener {
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private ViewPagerAdapter viewPagerAdapter;
    private Contact obj;
    private List<Contact> favoritesItem;
    private List<Fragment> fragments;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 1;
    private static final int database_update = 1;

    private GoogleApiClient mGoogleSignInClient;
    boolean flag;
    ArrayList<String> data;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    DBHelper mDBHelper;
    TextView facebookName;
    String userName, userPhoto;
    ImageView profilePictureView;
    FavoritesFragment favoriteFragment;
    HomeFragment homeFragment;
    AddContact addContactFragment;
    Profile profile;
    TwitterLoginButton button;
    TwitterAuthClient client;
    String a,b,c;
    Bitmap d;
    int e,f;
    long g;
    @BindView(R.id.view_pager)
    NonSwipeAbleViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawer;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindArray(R.array.array_of_tabs)
    String[] pageTitle;
    @BindArray(R.array.array_of_Choice)
    String[] choices;
    public static final String EXTRA_ANIMAL_ITEM = "animal_image_url";
    public static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "animal_image_transition_name";
    ViewActivity viewactivity;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socialInt();

        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        ButterKnife.bind(this);
        mDBHelper = new DBHelper(this);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, AlarmManagerBroadcastReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);
        Calendar cal = Calendar.getInstance();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 5 * 60 * 1000, broadcast);


        init();

        homeFragment = (HomeFragment) viewPagerAdapter.getItem(0);
        favoriteFragment = (FavoritesFragment) viewPagerAdapter.getItem(1);
        addContactFragment = (AddContact) viewPagerAdapter.getItem(2);
        homeFragment.setDatabase(mDBHelper);
        addContactFragment.setDatabase(mDBHelper);
         viewactivity =new ViewActivity();
        viewactivity.setDatabase(mDBHelper);
        String ProfileName = mSharedPreferences.getString("name", "username");
        String ProfilePhoto = mSharedPreferences.getString("photo", "username");
        facebookName.setText(ProfileName);
        Picasso.with(getApplicationContext()).load(ProfilePhoto).into(profilePictureView);

    }
    public DBHelper getDatabase(){
        return mDBHelper;
    }
    public void socialInt() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d("hi1", "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d("hi2", "onAuthStateChanged:signed_out");
                }

            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        client = new TwitterAuthClient();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home_fragment) {
            setTitle(pageTitle[0]);
            viewPager.setCurrentItem(0);
        } else if (id == R.id.favorites_fragment) {
            setTitle(pageTitle[1]);
            viewPager.setCurrentItem(1);
        } else if (id == R.id.add_contact) {
            setTitle(pageTitle[2]);
            viewPager.setCurrentItem(2);
        } else if(id==R.id.verfiy){
            startActivity(new Intent(this, PhoneActivity.class));

        }else if (id == R.id.close) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    @Override
    public void onBackPressed() {
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }

        Intent intent = getIntent();
        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        flag = false;
        data = new ArrayList<>();
        setTitle(pageTitle[0]);
        mSharedPreferences = getSharedPreferences("info", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        favoritesItem = new ArrayList<Contact>();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        facebookName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name_contact);
        profilePictureView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.image_contact);
        for (int i = 0; i < 3; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(pageTitle[i]));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);


        fragments = Arrays.asList(new HomeFragment(), new FavoritesFragment(), new AddContact());
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPagingEnabled(false);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;

            }


        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setTitle(pageTitle[tab.getPosition()]);
                if (tab.getPosition() == 2) {
                    addContactFragment = (AddContact) viewPagerAdapter.getItem(2);
                    addContactFragment.update();

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (!mSharedPreferences.getBoolean("stateFacebook", false)) {
            onFacebookLoading();
            mEditor = mSharedPreferences.edit();
            mEditor.putBoolean("stateFacebook", true);
            mEditor.apply();
        }


    }




    @Override
    public void onCardSelected(int position, String contactName, String contactNumber, Bitmap contactImage, long contactId, int favoriteImage, String contactEmail) {
        Log.i("hello","done");

      obj = new Contact(contactName, contactNumber, contactImage, contactId, favoriteImage, contactEmail);
        if (!flag) {
            if (!onCheckIfItemExists(obj, favoritesItem)) {

                favoritesItem.add(obj);
                favoriteFragment.addToFavorites(favoritesItem);
                flag = true;
            }
        } else {
            homeFragment.UpdateCard(obj.getName());
            for (int i = 0; i < favoritesItem.size(); i++) {
                if (obj.getName().equals(favoritesItem.get(i).getName())) {
                    favoritesItem.remove(i);
                    favoriteFragment.notifyItem();
                }
            }
            flag = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemClick(int position, Contact obj, ImageView image) {


        //alertActivity(position, obj);
        Intent intent = new Intent(this, viewactivity.getClass());
        byte[] byteArray ;
        if (obj.getPhoto() != null) {
            byteArray = getBytes(obj.getPhoto());

        }else {
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_user);
            byteArray= getBytes(bitmap);
        }
        intent.putExtra("contactPhoto", byteArray);
        intent.putExtra("contactName", obj.getName());
        intent.putExtra("contactNumber", obj.getContactNumber());
        intent.putExtra("contactEmail", obj.getEmail());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                image,
                ViewCompat.getTransitionName(image));

        ActivityCompat.startActivityForResult(this, intent,database_update,options.toBundle());
    }



    @Override
    public void onItemLongClick(int position, Contact obj) {
        ContactProfile nextFrag = new ContactProfile();
        Bundle bundle = new Bundle();
       // BusStation.getBus().post(new Message("Description"));
        bundle.putString("contactName", obj.getName());
        bundle.putString("contactNumber", obj.getContactNumber());
        bundle.putString("contactEmail", obj.getEmail());
        byte[] byteArray;
        if (obj.getPhoto() != null) {
            byteArray = getBytes(obj.getPhoto());

        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_user);
            byteArray = getBytes(bitmap);
        }
        bundle.putByteArray("contactPhoto", byteArray);
        nextFrag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_content, nextFrag)
                .addToBackStack(null)
                .commit();

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)




    @Override
    public void onCardDeletes(Contact contact, int position) {
        homeFragment.onRemovedFromFavorite(position, favoritesItem, contact);
    }

    public boolean onCheckIfItemExists(Contact contact, List<Contact> favoritesList) {
        boolean exist = false;
        long id;
        for (int i = 0; i < favoritesList.size(); i++) {
            id = favoritesList.get(i).getid();
            if (contact.getid() == id) {
                exist = true;
            } else exist = false;
        }
        return exist;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        client.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("hi", "Google sign in failed", e);
            }
        } if(requestCode==database_update){
//            Intent intent = getIntent();
//            finish();
//
//            overridePendingTransition( 0, 0);
//            startActivity(getIntent());
//            overridePendingTransition( 0, 0);
            homeFragment.notifychange();
        }


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                        } else {

                            Log.w("failed", "signInWithCredential:failure", task.getException());

                        }
                    }
                });

    }


    public ArrayList<String> onFacebookLoading() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        if (Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {

                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                                    Log.v("facebook - profile", currentProfile.getFirstName());
                                    mProfileTracker.startTracking();
                                    Profile profile = Profile.getCurrentProfile();
                                    userName = profile.getName();
                                    userPhoto = profile.getProfilePictureUri(150, 150).toString();
                                    mEditor.putString("name", userName);
                                    mEditor.putString("photo", userPhoto);
                                    mEditor.apply();
                                    facebookName.setText(userName);
                                    Picasso.with(getApplicationContext()).load(userPhoto).into(profilePictureView);

                                    handleFacebookAccessToken(loginResult.getAccessToken());


                                }
                            };

                        } else {
                            Profile profile = Profile.getCurrentProfile();
                            userName = profile.getName();
                            userPhoto = profile.getProfilePictureUri(150, 150).toString();
                            Log.d("wdf", userName);
//
                        }

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        return data;
    }


    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void alertActivity(final int objPosition, final Contact obj) {
        final AlertDialog.Builder AlertDialogBuilder = new AlertDialog.Builder(this);

        final byte[][] arr = new byte[1][1];
        AlertDialogBuilder.setTitle("Select Option : ");

        AlertDialogBuilder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (obj.getPhoto() != null) {
                            arr[0] = getBytes(obj.getPhoto());

                        } else {
                            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_user);
                            arr[0] = getBytes(bitmap);
                        }
                        addContactFragment = (AddContact) viewPagerAdapter.getItem(2);
                        addContactFragment.addData(obj.getName(), obj.getContactNumber(), obj.getEmail(), arr[0]);

                        dialog.dismiss();
                        viewPager.setCurrentItem(2);

                        break;
                    case 1:
                        mDBHelper.deleteContact(obj.getName());
                        removeSelected(obj.getName());
                        homeFragment.notifyChange(objPosition);
                        Toast.makeText(getApplicationContext(), "Contact Deleted Successfully ", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        AlertDialog dialog = AlertDialogBuilder.create();
        dialog.show();
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    @Override
    public void onResume() {
        super.onResume();
        profile = Profile.getCurrentProfile();

    }


    public void removeSelected(String name) {
        if (favoritesItem != null) {
            for (int i = 0; i < favoritesItem.size(); i++) {
                if (name.equals(favoritesItem.get(i).getName())) {
                    favoritesItem.remove(i);
                    favoriteFragment.notifyItem();
                }
            }
        }
    }

    public void onTwitterLoading() {
        client.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                handleTwitterSession(result.data);

            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this, "Authentication failed!", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void handleTwitterSession(TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signInWithGoogle();
                        } else {
                            Log.w("hi3", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setToolbar(int color, int textColor) {
        toolbar.setBackgroundColor(color);
        toolbar.setTitleTextColor(textColor);
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onTwitterLoading();

                        } else {

                        }
                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListner);
        BusStation.getBus().register(this);

    }



    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListner);
        BusStation.getBus().unregister(this);

    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void onPause() {
        super.onPause();
    }



    @Subscribe
    public void recivedMessage(Message msg){
       // obj = new Contact(msg.getNamea(), msg.getNumbera(),msg.getImagea(), msg.getIda(), msg.getFavoriteImagea(), msg.getEmaila());
    }
}