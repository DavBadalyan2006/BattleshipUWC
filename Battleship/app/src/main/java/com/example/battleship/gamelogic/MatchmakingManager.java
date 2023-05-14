package com.example.battleship.gamelogic;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class MatchmakingManager {
    private static final String TAG = "MatchmakingManager";

    private String userId;
    private FirebaseFirestore db;
    private DocumentReference matchmakingRef;
    private ListenerRegistration matchmakingListenerRegistration;

    public interface MatchmakingCallback {
        void onMatchFound(String roomId);
    }

    public MatchmakingManager(String userId) {
        this.userId = userId;
        this.db = FirebaseFirestore.getInstance();
        this.matchmakingRef = db.collection("matchmaking").document();
    }

    public void findMatch(final MatchmakingCallback callback) {
        db.collection("matchmaking").whereEqualTo("player2Id", null).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documents = task.getResult();
                if (!documents.isEmpty()) {
                    // Join an existing match.
                    DocumentSnapshot matchSnapshot = documents.getDocuments().get(0);
                    Match match = matchSnapshot.toObject(Match.class);
                    if (match != null && !match.hasSecondPlayer()) {
                        match.setPlayer2Id(userId);
                        matchSnapshot.getReference().set(match)
                                .addOnSuccessListener(aVoid -> callback.onMatchFound(matchSnapshot.getId()))
                                .addOnFailureListener(e -> Log.w(TAG, "Error updating match", e));
                    }
                } else {
                    // Create a new match and listen for a second player.
                    Match match = new Match(userId);
                    matchmakingRef.set(match)
                            .addOnSuccessListener(aVoid -> listenForSecondPlayer(callback))
                            .addOnFailureListener(e -> Log.w(TAG, "Error creating match", e));
                }
            } else {
                Log.w(TAG, "Error finding match", task.getException());
            }
        });
    }

    private void listenForSecondPlayer(MatchmakingCallback callback) {
        matchmakingListenerRegistration = matchmakingRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                Match match = snapshot.toObject(Match.class);
                if (match != null && match.hasSecondPlayer()) {
                    callback.onMatchFound(snapshot.getId());
                    stopMatchmaking();
                }
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

    public void cancelMatchmaking() {
        stopMatchmaking();
        matchmakingRef.delete();
    }

    private void stopMatchmaking() {
        if (matchmakingListenerRegistration != null) {
            matchmakingListenerRegistration.remove();
            matchmakingListenerRegistration = null;
        }
    }
}
