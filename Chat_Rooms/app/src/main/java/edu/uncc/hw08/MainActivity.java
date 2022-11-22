/*

a. HW08.
b. Group6_HW08.
c. Success Odoemena.
 */


package edu.uncc.hw08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements MyChatsFragment.MyChatsFragListener, CreateChatFragment.CreateChatFragListener,
        ChatFragment.ChatFragListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new MyChatsFragment())
                .commit();
    }

    //launch CreateChatFragment
    @Override
    public void makeChat() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateChatFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void popBackStack() {
    getSupportFragmentManager().popBackStack();
    }

    @Override
    //sign out
    //return to AuthActivity

    public void signOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> online = new HashMap<>();
        online.put("online", false);
        db.collection("users").document(mAuth.getUid()).update(online);
        mAuth.signOut();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    //launch Chat Fragment
    //pass chat object
    public void openChat(Chat chat) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ChatFragment.newInstance(chat))
                .addToBackStack(null).commit();
    }
}