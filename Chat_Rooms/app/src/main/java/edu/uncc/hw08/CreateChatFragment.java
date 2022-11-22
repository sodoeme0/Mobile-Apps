package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import edu.uncc.hw08.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.uncc.hw08.databinding.FragmentCreateChatBinding;
import edu.uncc.hw08.databinding.UsersRowItemBinding;
import edu.uncc.hw08.databinding.UsersRowItemBinding;


public class CreateChatFragment extends Fragment {

FirebaseAuth mAuth = FirebaseAuth.getInstance();
FirebaseFirestore db = FirebaseFirestore.getInstance();
FragmentCreateChatBinding mBinding;
ArrayList<User> users = new ArrayList<>();
User currentUser = new User();
UserAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = mBinding.inflate(inflater, container, false);
       return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("New Chat");
        adapter = new UserAdapter(getContext(), R.layout.users_row_item, users);
        mBinding.listView.setAdapter(adapter);

        mBinding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentUser = users.get(i);
                mBinding.textViewSelectedUser.setText(currentUser.getName());
            }
        });
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                users.clear();
                for(QueryDocumentSnapshot docs: value){
                    if(mAuth.getCurrentUser()!=null&&!mAuth.getUid().equals(docs.getId())) {
                        User user = docs.toObject(User.class);
                        user.id = docs.getId();

                            users.add(user);

                    }


                }
                adapter.notifyDataSetChanged();
                Log.d("d", "onEvent: "+users);

            }

        });

        mBinding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mBinding.editTextMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(currentUser.name==null){
                    Toast.makeText(getContext(), "Must select a user", Toast.LENGTH_SHORT).show();

                }
               else{

                ArrayList<Message> messages = new ArrayList<>();
                Message messageObj = new Message(mAuth.getCurrentUser().getDisplayName(), mAuth.getUid(), currentUser.name, currentUser.id, message);
                messages.add(messageObj);
                HashMap<String, Object> chat = new HashMap<>();
                chat.put("started_by_id", mAuth.getUid());
                chat.put("started_by_name", mAuth.getCurrentUser().getDisplayName());
                chat.put("recieved_by_id", currentUser.id);
                chat.put("recieved_by_name", currentUser.name);
                chat.put("messages", messages);
                db.collection("chatrooms").add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        mListener.popBackStack();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("d", "create chat onFailure: "+e.getMessage());
                    }
                });
            }}
        });
        mBinding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.popBackStack();
            }
        });

    }

    class UserAdapter extends ArrayAdapter<User> {
        public UserAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView ==null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_row_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = convertView.findViewById(R.id.textViewName);
                viewHolder.online = convertView.findViewById(R.id.imageViewOnline);
                convertView.setTag(viewHolder);
            }
            User user = getItem(position);
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.name.setText(user.name);
            if(!user.online){
                viewHolder.online.setVisibility(View.GONE);
            }else {
                viewHolder.online.setVisibility(View.VISIBLE);

            }
            return convertView;

        }


        public  class ViewHolder{
            TextView name;
            ImageView online;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {

        mListener = (CreateChatFragListener) context;
        super.onAttach(context);
    }

    CreateChatFragListener mListener;
    public interface CreateChatFragListener{

        void popBackStack();
    }

}