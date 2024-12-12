package com.example.mysos;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class TutorialActivity extends AppCompatActivity {
    TextView tvGestures, tvEyesMovement, tvHeadMovement;
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        tvGestures = (TextView) findViewById(R.id.tvGestures);
        tvEyesMovement = (TextView) findViewById(R.id.tvEyeMovement);
        tvHeadMovement = (TextView) findViewById(R.id.tvHeadMovement);
        youTubePlayerView = findViewById(R.id.youtube_player);
        getLifecycle().addObserver(youTubePlayerView);
        clickListener();

    }

    private void clickListener() {
        tvGestures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null)
                    player.loadVideo("GjYpE4vAHn4", 0);
                else
                    playVideo("GjYpE4vAHn4");
            }
        });
        tvEyesMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null)
                    player.loadVideo("2_G0qU293fs", 0);
                else
                    playVideo("2_G0qU293fs");

            }
        });
        tvHeadMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null)
                    player.loadVideo("P_9Oc9vLSaM", 0);
                else
                    playVideo("P_9Oc9vLSaM");


            }
        });
    }


    private void playVideo(String id) {
        youTubePlayerView.setVisibility(View.VISIBLE);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                TutorialActivity.this.player = youTubePlayer;
                youTubePlayer.loadVideo(id, 0);
                youTubePlayer.play();
            }
        });
    }
}