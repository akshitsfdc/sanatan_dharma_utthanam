package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.SliderAdapterExample;
import com.akshit.akshitsfdc.allpuranasinhindi.models.HardCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SliderItem;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smarteist.autoimageslider.IndicatorAnimations;

import java.util.Objects;

public class ContactUsActivity extends MainActivity {

    FileUtils fileUtils;

    String SUPPORT_EMAIL_ADDRESS = "support@allpuranashindi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_contact_us);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_contact_us, null, false);
        drawer.addView(contentView, 0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        fileUtils = new FileUtils(ContactUsActivity.this);


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle("");
        toolbar.setSubtitle("");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.off_notification_color));
        toggle.setDrawerIndicatorEnabled(false);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        findViewById(R.id.emailUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailToSupport(SUPPORT_EMAIL_ADDRESS);
            }
        });
    }

    private void sendEmailToSupport(String toEmail){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FileUtils fileUtils = new FileUtils(ContactUsActivity.this);

        if(user == null){
            fileUtils.showLongToast("You are not logged in!");
            return;
        }
        String subject = user.getDisplayName()+" Query @"+user.getEmail();
        String message = "Hi Support,";
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",toEmail, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }



    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
