package edu.uncc.hw07;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.uncc.hw07.databinding.FragmentCreateForumBinding;
import edu.uncc.hw07.databinding.FragmentLoginBinding;


public class CreateForumFragment extends Fragment {


  FragmentCreateForumBinding binding;
    FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateForumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        binding.buttonCAncel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.popBackStack();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.editTextTextTitle.getText().toString();
                String desc = binding.editTextTextDesc.getText().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                //Confirm title and message description are not empty
                if(title.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid title!", Toast.LENGTH_SHORT).show();
                } else if (desc.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid description!", Toast.LENGTH_SHORT).show();
                } else {
                    //Retrieve current user from database
                    db.collection("users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            Forum forum = new Forum(title, desc, mAuth.getUid());
                            forum.createdBy=user.name;

                            //Add new forum to forum collection
                            db.collection("forums").add(forum).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("d", "Forum created succesfully");
                                    mListener.popBackStack();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("d", "onFailure: "+e.getMessage());
                                }
                            });
                        }
                    });

                }
            }
        });

    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreatForumListener) context;
    }

    CreatForumListener mListener;
    public interface CreatForumListener{
        void popBackStack();
    }
}