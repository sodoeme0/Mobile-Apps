package edu.uncc.hw08;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import edu.uncc.hw08.databinding.FragmentChatBinding;
import edu.uncc.hw08.databinding.FragmentCreateChatBinding;
import edu.uncc.hw08.databinding.FragmentMyChatsBinding;
import edu.uncc.hw08.databinding.MyChatsListItemBinding;


public class MyChatsFragment extends Fragment {

   FragmentMyChatsBinding binding;


   FirebaseFirestore db;
   FirebaseAuth mAuth;

    ArrayList<Chat> chats = new ArrayList<>();
    ChatsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onAttach(@NonNull Context context) {
        mListener = (MyChatsFragListener) context;
        super.onAttach(context);
    }
    MyChatsFragListener mListener;
    public interface MyChatsFragListener{
        void makeChat();
        void popBackStack();
        void signOut();
        void openChat(Chat chat);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = binding.inflate(inflater, container, false);
        adapter = new ChatsAdapter(getContext(), R.layout.my_chats_list_item, chats);
        binding.listView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Chats");
        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.signOut();
            }
        });
        binding.buttonNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.makeChat();
            }
        });

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Chat chat = chats.get(i);
                mListener.openChat(chat);
            }
        });
        db.collection("chatrooms").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                chats.clear();
                for(QueryDocumentSnapshot doc: value){
                        Chat chat = doc.toObject(Chat.class);
                        chat.chat_id = doc.getId();
                        chat.sortMessages();
                        Log.d("zzzz", "onEvent: "+chat);
                        if ( (chat.started_by_id.equals(mAuth.getUid())) || chat.recieved_by_id.equals(mAuth.getUid())) {
                            chats.add(chat);
                        }

                    }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    chats.sort(new Comparator<Chat>() {
                         @Override
                         public int compare(Chat chat, Chat t1) {

                             int x =0;
                             if (chat.messages.size()==0){
                                 x=1;
                             }
                             else if(t1.messages.size()==0){
                                     x=-1;
                             }else{
                                 x=  -1*(chat.messages.get(chat.messages.size()-1).timeSTamp
                                         .compareTo(t1.messages.get(t1.messages.size()-1).timeSTamp));
                             }
                                  return x;
                         }
                     });
                }
                Log.d("zzz", "compare: "+chats);

                adapter.notifyDataSetChanged();
            }
        });

    }


    class ChatsAdapter extends ArrayAdapter<Chat>{

        public ChatsAdapter(@NonNull Context context, int resource, @NonNull List<Chat> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView ==null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_chats_list_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.textViewMsgText = convertView.findViewById(R.id.textViewMsgText);
                viewHolder.textViewMsgOn = convertView.findViewById(R.id.textViewMsgOn);
                viewHolder.textViewMsgBy = convertView.findViewById(R.id.textViewMsgBy);

                convertView.setTag(viewHolder);
            }
            Chat chat = getItem(position);

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            if(mAuth.getUid().equals(chat.started_by_id)){
                viewHolder.textViewMsgBy.setText(chat.recieved_by_name);

            }else {
                viewHolder.textViewMsgBy.setText(chat.started_by_name);

            }
            if(chat.messages.size()>0) {
                Message message = chat.messages.get(chat.messages.size() - 1);
                viewHolder.textViewMsgText.setText(message.message);
                viewHolder.textViewMsgOn.setText(message.getDateFormat());
            }else{
                viewHolder.textViewMsgText.setText("");
                viewHolder.textViewMsgOn.setText("");
            }
            return convertView;

        }

        public class ViewHolder{
            TextView textViewMsgBy;
            TextView textViewMsgText;
            TextView textViewMsgOn;

        }
    }
}