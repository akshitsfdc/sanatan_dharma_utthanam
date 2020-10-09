package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.models.CurrentDownloadingModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.UserDataModel;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";

    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle toggle;
    protected static int REQUEST_PERMISSION = 1;
    protected boolean boolean_permission;

    public static boolean DOWNLOAD_IN_PROGRESS = false;
    public static int CURRENT_DOWNLOAD_PROGRESS = 0;
    public static CurrentDownloadingModel CURRENT_DOWNLOADING_MODEL;

    public static UserDataModel USER_DATA = new UserDataModel();

    private Uri mInvitationUrl;
    protected NavigationView navigationView;
    protected View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        permission_fn();

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.off_notification_color));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        headerView = navigationView.getHeaderView(0);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //hideKeyboard();
                // Do whatever you want here
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here
            }
        };

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.off_notification_color));

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

       if (id == R.id.nav_home) {
            //startPollFishSurvey();
           if (!(this instanceof HomeActivity)) {
               navigateToHome();
           }
        }else if(id == R.id.nav_puran){
                navigateToSoftPuranaDashBoard("purans");
        }else if(id == R.id.nav_veda){
               navigateToSoftPuranaDashBoard("vedas");
       }else if(id == R.id.nav_hdBook){
               navigateToSoftPuranaDashBoard("hd_books");
       }else if(id == R.id.nav_festive){
               navigateToSoftPuranaDashBoard("festive");

       }else if(id == R.id.nav_geeta){
               navigateToSoftPuranaDashBoard("geeta");
       }else if(id == R.id.nav_others){
               navigateToSoftPuranaDashBoard("more");
       }else if(id == R.id.nav_prime){
           if (!(this instanceof PrimeActivity)) {
               navigateToPrimeActivity();
           }
       }else if(id == R.id.nav_buy){
               navigateToHardCopyDashBoard();
       }else if(id == R.id.nav_orders){
           if (!(this instanceof UserOrdersActivity)) {
               navigateToOrders();
           }
       }else if(id == R.id.nav_favorite){
           navigateToSoftPuranaDashBoard("favorite_books");
       }else if(id == R.id.nav_offline){
           navigateToSoftPuranaDashBoard("offline");
       }else if(id == R.id.nav_share){
           createLink();
       }else if(id == R.id.nav_contact){
           if (!(this instanceof ContactUsActivity)) {
               navigateToContactUs();
           }
       }else if(id == R.id.nav_updesh){
           if (!(this instanceof UpdeshActivity)) {
               navigateToUpdesh();
           }
       }
        return true;
    }
    private void navigateToUpdesh(){
        Intent intent = new Intent(MainActivity.this, UpdeshActivity.class);
        startActivity(intent);
        //finish();

    }
    private void navigateToContactUs(){
        Intent intent = new Intent(MainActivity.this, ContactUsActivity.class);
        startActivity(intent);
        //finish();

    }
    private void navigateToOrders(){
        Intent intent = new Intent(MainActivity.this, UserOrdersActivity.class);
        startActivity(intent);
        finish();

    }
    private void navigateToHome(){

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        super.onBackPressed();

    }
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }


    private void navigateToPrimeActivity(){
        Intent intent = new Intent(MainActivity.this, PrimeActivity.class);
        intent.putExtra("fromHome",true);
        startActivity(intent);
        finish();
    }
    private void navigateToSoftPuranaDashBoard(String type){
        Intent intent = new Intent(MainActivity.this, SoftPuranaDashboardActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
        //finish();
    }
    private void navigateToHardCopyDashBoard(){
        Intent intent = new Intent(MainActivity.this, HardCopyBookDashboardActivity.class);
        intent.putExtra("fromHome", false);
        startActivity(intent);
       //finish();
    }

    private void permission_fn(){

        if((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){

            boolean_permission = false;

            if((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))){

            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION );
            }

        }else{

            boolean_permission = true;
        }
        if((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){

            boolean_permission = false;

            if((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))){

            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION );
            }

        }else{

            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                boolean_permission = true;
            }
            else{

                Toast.makeText(MainActivity.this, "Please grant permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void createLink() {

        String link = "https://allpuranasinhindi.page.link";
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://allpuranasinhindi.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.akshit.akshitsfdc.allpuranasinhindi")
                                .setMinimumVersion(1)
                                .build())
                .setIosParameters(
                        new DynamicLink.IosParameters.Builder("com.akshit.akshitsfdc.allpuranasinhindi")
                                .setAppStoreId("123456789")
                                .setMinimumVersion("1.0.1")
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl = shortDynamicLink.getShortLink();
                        Log.d(TAG, "onSuccess: mInvitationUrl : \n"+mInvitationUrl);
                        sendInvitation();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new FileUtils(MainActivity.this).showShortToast("Something went wrong try again later!");
                e.printStackTrace();
            }
        });
        // [END ddl_referral_create_link]
    }

    public void sendInvitation() {

        try {

            String invitationLink = mInvitationUrl.toString();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey I am using this amazing app: "+getString(R.string.app_name));
            String shareMessage = "Hey I am using this amazing app: \n\nInstall "+getString(R.string.app_name)+" app from store and login.\n\nThis app provides a huge library of Hindu Spiritual books at one place.\n\nThis is very portable to use, you can read any book anytime at one place.\n\n\n"
                    + invitationLink;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
            new FileUtils(MainActivity.this).showShortToast("Something went wrong try again later!");
        }
    }

}
