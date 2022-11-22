package edu.uncc.hw07;

import static android.view.View.GONE;

import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import edu.uncc.hw07.databinding.ForumRowItemBinding;
import edu.uncc.hw07.databinding.FragmentForumsBinding;

public class ForumsFragment extends Fragment {

FragmentForumsBinding binding;
ArrayList<Forum> mForums = new ArrayList<>();
ForumsAdapter adapter;
FirebaseAuth mAuth;
FirebaseFirestore db;
    public static ForumsFragment newInstance(String param1, String param2) {
        ForumsFragment fragment = new ForumsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForumsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumsFragListener) context;
    }
    ForumsFragListener mListener;
    public interface ForumsFragListener{
        void createForum();
        void logout();
        void launchForum(Forum forum);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
       ForumsAdapter adapter = new ForumsAdapter();
        binding.recyclerViewforums.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewforums.setAdapter(adapter);

        //load forums from forum collection
        db.collection("forums").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               int counter = 0;
               mForums.clear();
                for(QueryDocumentSnapshot docs: value){
                    Forum forum = docs.toObject(Forum.class);
                    forum.forum_id = docs.getId();
                    mForums.add(forum);



                }

                adapter.notifyDataSetChanged();
            }
        });
        binding.buttonCreateForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.createForum();
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });

    }




    class ForumsAdapter extends RecyclerView.Adapter<ForumsAdapter.ForumViewHolder> {
        @NonNull

        String name ="";
        public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ForumRowItemBinding binding = ForumRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ForumViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {
            Forum forum = mForums.get(position);
            holder.setupUI(forum);
        }

        @Override
        public int getItemCount() {

            return mForums.size();
        }

        class ForumViewHolder extends RecyclerView.ViewHolder {
            ForumRowItemBinding mBinding;
            Forum mForum;
            public ForumViewHolder(ForumRowItemBinding b) {
                super(b.getRoot());
                mBinding = b;
            }

            public void setupUI(Forum forum){

                mForum = forum;
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                mBinding.textViewForumCreatedBy.setText(forum.createdBy);

                mBinding.textViewForumLikesDate.setText(forum.likes+" Likes | " +forum.date);
                if(forum.desc.length()>200){
                    String text = forum.desc.substring(0,200) + "...";
                    mBinding.textViewForumText.setText(text);

                }else{
                    mBinding.textViewForumText.setText(forum.desc);

                }
                mBinding.textViewForumTitle.setText(forum.title);

                if(!mAuth.getUid().equals(forum.user_id)){
                    mBinding.imageViewDelete.setVisibility(GONE);
                }
                mBinding.rowContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.launchForum(forum);
                    }
                });
                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("forums").document(forum.forum_id).delete();


                    }
                });
                mBinding.imageViewLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if map does not contains key (Uid) then like
                        //else unlike
                        if(!forum.userLikes.containsKey(mAuth.getUid())) {
                            forum.userLikes.put(mAuth.getUid(), 1);
                            forum.likes++;
                            db.collection("forums").document(forum.forum_id).set(forum);
                        }else{
                            forum.userLikes.remove(mAuth.getUid());
                            forum.likes--;
                            db.collection("forums").document(forum.forum_id).set(forum);
                        }
                    }
                });

                if(forum.userLikes.containsKey(mAuth.getUid())){
                    mBinding.imageViewLike.setImageResource(R.drawable.like_favorite);
                }else{
                    mBinding.imageViewLike.setImageResource(R.drawable.like_not_favorite);

                }


            }
        }

    }
}