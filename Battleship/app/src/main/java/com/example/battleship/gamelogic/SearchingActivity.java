package com.example.battleship.gamelogic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.battleship.R;
import com.example.battleship.gamelogic.MatchmakingManager.MatchmakingCallback;
import com.google.firebase.firestore.ListenerRegistration;

public class SearchingActivity extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "user_id";
    private MatchmakingManager matchmakingManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        String userId = getIntent().getStringExtra(EXTRA_USER_ID);
        matchmakingManager = new MatchmakingManager(userId);
        matchmakingManager.findMatch(new MatchmakingCallback() {
            @Override
            public void onMatchFound(String roomId) {
                startGameActivity(roomId);
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        matchmakingManager.cancelMatchmaking();
//    }

    private void startGameActivity(String roomId) {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("roomId", roomId);
        startActivity(intent);
        finish();
    }
}
