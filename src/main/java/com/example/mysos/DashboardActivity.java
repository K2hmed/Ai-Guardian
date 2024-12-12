package com.example.mysos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mysos.ShakeServices.ReactivateService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DashboardActivity extends AppCompatActivity {
    DrawerLayout drawer;
    ImageView btnMenu, contactIcon, tutorialIcon;
    TextView tvObjective, tvCreators, tvTutorials, tvContactList;
    View contactBox, tutorialBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        btnMenu = (ImageView) findViewById(R.id.btnMenu);
        contactIcon = (ImageView) findViewById(R.id.contactIcon);
        tutorialIcon = (ImageView) findViewById(R.id.tutorialIcon);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        tvObjective = (TextView) findViewById(R.id.tvObjetive);
        tvCreators = (TextView) findViewById(R.id.tvCreators);
        tvTutorials = (TextView) findViewById(R.id.tvTutorial);
        tvContactList = (TextView) findViewById(R.id.tvContactList);
        contactBox = (View) findViewById(R.id.contactBox);
        tutorialBox = (View) findViewById(R.id.tutorialBox);

        checkNotificationPermission();
        clickListener();

    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1122);
            }
        }
    }

    private void clickListener() {
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        tvCreators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet("Creators", "Khushnud Ahmed (191221)\n\nAminah Ibrar (191224)\n\nMomna Ali (191226)");
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        tvObjective.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet("Objective", "For the ease and safety of your loved ones....\nYour Ai Guardian is here...");
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        tvTutorials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, TutorialActivity.class));

            }
        });
        tutorialIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, TutorialActivity.class));

            }
        });
        tutorialBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, TutorialActivity.class));

            }
        });
        tvContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            }
        });
        contactIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            }
        });
        contactBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            }
        });

    }

    private void openBottomSheet(String myTitle, String myContent) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_info);
        TextView title, content;
        title = bottomSheetDialog.findViewById(R.id.title);
        content = bottomSheetDialog.findViewById(R.id.text);
        title.setText(myTitle);
        content.setText(myContent);
        bottomSheetDialog.show();
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ReactivateService.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }
}