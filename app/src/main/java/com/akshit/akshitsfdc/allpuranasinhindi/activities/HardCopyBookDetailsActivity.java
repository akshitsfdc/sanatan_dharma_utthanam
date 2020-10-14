package com.akshit.akshitsfdc.allpuranasinhindi.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akshit.akshitsfdc.allpuranasinhindi.R;
import com.akshit.akshitsfdc.allpuranasinhindi.adapters.SliderAdapterExample;
import com.akshit.akshitsfdc.allpuranasinhindi.fragments.BookDetailSlideFragment;
import com.akshit.akshitsfdc.allpuranasinhindi.models.HardCopyModel;
import com.akshit.akshitsfdc.allpuranasinhindi.models.SliderItem;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.ClickableViewPager;
import com.akshit.akshitsfdc.allpuranasinhindi.utils.FileUtils;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class HardCopyBookDetailsActivity extends MainActivity {

    FileUtils fileUtils;
    ArrayList<String> imageUrls;

    private HardCopyModel hardCopyModel;

    private SliderView imageSlider;

    private TextView title;
    private TextView price;
    private TextView language;
    private TextView descriptionText;
    private TextView deliveryCharge;
    private TextView pages;
    private Button addToCartButton;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_hard_copy_book_details);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_hard_copy_book_details, null, false);
        drawer.addView(contentView, 0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        fileUtils = new FileUtils(HardCopyBookDetailsActivity.this);

        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        language = findViewById(R.id.language);
        descriptionText = findViewById(R.id.descriptionText);
        addToCartButton = findViewById(R.id.addToCartButton);
        buyButton = findViewById(R.id.buyButton);
        deliveryCharge = findViewById(R.id.deliveryCharge);
        pages = findViewById(R.id.pages);

        Intent intent=getIntent();
        hardCopyModel =(HardCopyModel) intent.getSerializableExtra("hardCopyModel");

        imageUrls = hardCopyModel.getAllPicsUrls();

        setDetails();

        SliderAdapterExample sliderAdapterExample = new SliderAdapterExample(HardCopyBookDetailsActivity.this);
        imageSlider = findViewById(R.id.imageSlider);
        for(int i = 0; i < imageUrls.size(); ++i){
            sliderAdapterExample.addItem(new SliderItem("",imageUrls.get(i), false, ""));
        }

        imageSlider.setSliderAdapter(sliderAdapterExample);
        imageSlider.setIndicatorAnimation(IndicatorAnimations.DROP);

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
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //addToCartClick();
            }
        });
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyButtonClick();
            }
        });
    }
    private void navigateToAddressDetails(HardCopyModel hardCopyModel){
        Intent intent = new Intent(HardCopyBookDetailsActivity.this, AddressDetailsActivity.class);
        intent.putExtra("hardCopyModel",hardCopyModel);
        startActivity(intent);
        // overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        //finish();
    }
    private void outOfStockButtonChanges(){
        //addToCartButton.setText("Out Of Stock");
        buyButton.setText("Notify Me");
        buyButton.setBackgroundColor(getResources().getColor(R.color.color_leaderboard_yellow));
        addToCartButton.setTextColor(getResources().getColor(R.color.color_leaderboard_red));

    }

    private void addToCartClick(){
        if(!hardCopyModel.isAvailable() || hardCopyModel.getStock() <= 0){
            fileUtils.showLongToast("This item is currently Out Of Stock");
        }
    }
    private void buyButtonClick(){
        if(!hardCopyModel.isAvailable() || hardCopyModel.getStock() <= 0){
            fileUtils.showLongToast("This item is currently Out Of Stock, you will be notified");
            return;
        }
        navigateToAddressDetails(hardCopyModel);
    }
    private void setDetails(){

        title.setText(hardCopyModel.getName());

         String priceTxt;
        if(hardCopyModel.getDiscount() > 0){
            DecimalFormat df = new DecimalFormat("#");
            DecimalFormat df2 = new DecimalFormat("#.#");
            priceTxt = getString(R.string.rs)+""+  df.format(hardCopyModel.getPrice())+" (-"+df2.format(hardCopyModel.getDiscount())+"%)";
            price.setText(priceTxt);
        }else{
            DecimalFormat df = new DecimalFormat("#");
            priceTxt = getString(R.string.rs)+""+ df.format(hardCopyModel.getPrice());
            price.setText(priceTxt);
        }
        if(!hardCopyModel.isAvailable() || hardCopyModel.getStock() <=0){
            outOfStockButtonChanges();
        }else {
        }

        DecimalFormat df = new DecimalFormat("#");
        priceTxt = "Delivery Charge: "+getString(R.string.rs)+""+df.format(hardCopyModel.getDeliveryCharge());
        deliveryCharge.setText(priceTxt);

        String languageText;

        if(hardCopyModel.isIsBook()){
            languageText = "Language: "+hardCopyModel.getLanguage();
            String pagesText = "  |  "+"Pages: "+hardCopyModel.getPages();
            pages.setText(pagesText);
        }else{
            languageText = "Material: "+hardCopyModel.getMaterial();
            pages.setVisibility(View.GONE);
        }

        language.setText(languageText);


        descriptionText.setText(hardCopyModel.getDescription());

    }
    @Override
    public void onBackPressed() {
       super.onBackPressed();
    }

}
