package edu.uncc.hw07;

import static android.view.View.GONE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import edu.uncc.hw07.databinding.CommentRowItemBinding;
import edu.uncc.hw07.databinding.ForumRowItemBinding;
import edu.uncc.hw07.databinding.FragmentForumBinding;

public class ForumFragment extends Fragment {


    private static final String ARG_FORUM = "FORUM";

    // TODO: Rename and change types of parameters
    private Forum forum;
    FragmentForumBinding binding;
    ArrayList<Comment> mComments = new ArrayList<>();
    CommentAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public ForumFragment() {
        // Required empty public constructor
    }

    // new instance method to pass forum object
    public static ForumFragment newInstance(Forum forum) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FORUM, forum);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forum = (Forum) getArguments().getSerializable(ARG_FORUM);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForumBinding.inflate(inflater, container, false);
        return(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        binding.textViewForumCreatedBy.setText(forum.createdBy);
        binding.textViewForumTitle.setText(forum.title);
        binding.textViewForumText.setText(forum.desc);

        //setup comment adapter
        CommentAdapter adapter = new CommentAdapter();
        binding.recyclerViewforums.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewforums.setAdapter(adapter);


        binding.buttonSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = binding.editTextComment.getText().toString();

                //Confirm if description text is not empty
                if(desc.isEmpty()) {
                    Toast.makeText(getActivity(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    //retrieve current user from users database
                    db.collection("users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            Log.d("u", "user recived ");
                            Comment comment = new Comment(user.name, desc, mAuth.getUid());

                            //add new comment to comments database
                            db.collection("comments").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("u", "comment added ");
                                }
                            });

                        }
                    });

                }
            }
        });

        //load comments from database
        db.collection("comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mComments.clear();
                for(QueryDocumentSnapshot docs: value){
                    Comment comment = docs.toObject(Comment.class);
                    comment.comment_id = docs.getId();
                    mComments.add(comment);



                }
                binding.textViewCommentsCount.setText(mComments.size() + " Comments");

                adapter.notifyDataSetChanged();
            }
        });

    }


    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
        @NonNull

        String name ="";
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CommentRowItemBinding binding = CommentRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new CommentViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
           Comment comment = mComments.get(position);
            holder.setupUI(comment);
        }

        @Override
        public int getItemCount() {

            return mComments.size();
        }

        class CommentViewHolder extends RecyclerView.ViewHolder {
            CommentRowItemBinding mBinding;
            Comment mComment;
            public CommentViewHolder(CommentRowItemBinding b) {
                super(b.getRoot());
                mBinding = b;
            }

            public void setupUI(Comment comment){

                mComment = comment;
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                mBinding.textViewCommentCreatedAt.setText(mComment.date);
                mBinding.textViewCommentCreatedBy.setText(mComment.name);
                mBinding.textViewCommentText.setText(mComment.desc);

                if(!mAuth.getUid().equals(comment.user_id)){
                    mBinding.imageViewDelete.setVisibility(GONE);
                }

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("comments").document(comment.comment_id).delete();


                    }
                });



            }
        }

    }

}






