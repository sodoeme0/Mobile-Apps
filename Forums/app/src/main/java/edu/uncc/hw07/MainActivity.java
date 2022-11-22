/*
 Homework 07
 Group6_HW07.zip
 Success Odoemena


 */



package edu.uncc.hw07;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, SignUpFragment.SignUpListener,
        CreateForumFragment.CreatForumListener, ForumsFragment.ForumsFragListener {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()==null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new LoginFragment())
                    .commit();
        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new ForumsFragment())
                    .commit();
        }
    }

    @Override
    public void createNewAccount() {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rootView, new SignUpFragment())
                    .addToBackStack(null).commit();
    }

    @Override
    public void login() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new ForumsFragment())
                .commit();
    }

        @Override
        public void popBackStack() {
            getSupportFragmentManager().popBackStack();
        }

    @Override
    public void launchForum(Forum forum) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView,  ForumFragment.newInstance(forum))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void createForum() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateForumFragment())
                .addToBackStack(null).commit();
    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }
}
