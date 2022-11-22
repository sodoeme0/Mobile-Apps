package edu.uncc.hw08;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import edu.uncc.hw08.databinding.ChatListItemBinding;
import edu.uncc.hw08.databinding.FragmentChatBinding;
import edu.uncc.hw08.databinding.FragmentCreateChatBinding;


public class ChatFragment extends Fragment {




    private Chat chat;
    private final static String ARGCHAT="PARAMCHAT";
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FragmentCreateChatBinding mBinding;
    ArrayList<Message> messages = new ArrayList<>();
    MessageAdapter adapter;
    FragmentChatBinding binding;
    ChatFragListener mListener;
    public ChatFragment() {
        // Required empty public constructor
    }


    // newInstance method to pass chat object
    public static ChatFragment newInstance(Chat param1) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGCHAT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chat = (Chat) getArguments().getSerializable(ARGCHAT);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mListener = (ChatFragListener)context;
        super.onAttach(context);
    }


    public interface ChatFragListener{
        void popBackStack();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
            String friend;
            //friend = user who is not the current user
            if(mAuth.getUid().equals(chat.started_by_id)){
                friend = chat.recieved_by_name;
            }else{
                friend = chat.started_by_name;
            }
            getActivity().setTitle("Chat - "+  friend);

            //setup adapter
            adapter = new MessageAdapter();
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerView.setAdapter(adapter);

            //delete chat from chatrooms collection upon click
            binding.buttonDeleteChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("chatrooms").document(chat.chat_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            mListener.popBackStack();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            binding.buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.popBackStack();
                }
            });

            //add message to chatrooms message HashMap
            binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String msg = binding.editTextMessage.getText().toString();
                    if(!msg.isEmpty()) {
                        Message message = new Message(chat.started_by_name, chat.started_by_id, chat.recieved_by_name, chat.recieved_by_id, msg);
                        HashMap<String, Object> updateMsg = new HashMap<>();
                        messages.add(message);
                        updateMsg.put("messages", messages);
                        db.collection("chatrooms").document(chat.chat_id).update(updateMsg).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("zz", "onSuccess: "+updateMsg);
                                binding.editTextMessage.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    else{
                        Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //load messages from chatrooms collection
        db.collection("chatrooms").document(chat.chat_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    messages.clear();
                ArrayList<HashMap<String, Object>> hashMesgs = (ArrayList<HashMap<String, Object>>) value.get("messages");
                for (HashMap<String, Object> messageHashMap : hashMesgs) {
                    Message message = new Message();
                    message.message = (String) messageHashMap.get("message");
                    message.senderName = (String) messageHashMap.get("senderName");
                    message.senderId = (String) messageHashMap.get("senderId");
                    message.recieverName = (String) messageHashMap.get("recieverName");
                    message.recieverId = (String) messageHashMap.get("recieverId");
                    com.google.firebase.Timestamp javaDate = (com.google.firebase.Timestamp) messageHashMap.get("timeSTamp");
                    message.timeSTamp = javaDate.toDate();
                    messages.add(message);
                }

                //sort messages by time stamp
                messages.sort(new Comparator<Message>() {
                    @Override
                    public int compare(Message message, Message t1) {
                        int x = message.timeSTamp.compareTo(t1.timeSTamp);
                        return x;

                    }
                });
                adapter.notifyDataSetChanged();
            }
            }

        });

    }

    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChatListItemBinding binding = ChatListItemBinding.inflate(getLayoutInflater(), parent, false);

            return new MessageViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            Message message = messages.get(position);
            holder.setupUI(message, position);
            Log.d("p", "onEvent: "+message);

        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class MessageViewHolder extends RecyclerView.ViewHolder{
            ChatListItemBinding mBinding;
            Message mMessage;
            int mPosition;
            public MessageViewHolder(@NonNull ChatListItemBinding mBinding) {
                super(mBinding.getRoot());
                this.mBinding= mBinding;
            }

            public void setupUI(Message message, int position){
                mMessage = message;
                mPosition = position;

                //If message sent by current user display sender as "Me"
                //else display sender as user name
                if (mMessage.senderId.equals(mAuth.getUid())){
                    mBinding.textViewMsgBy.setText("Me");

                }else  {
                    mBinding.textViewMsgBy.setText(mMessage.senderName);
                    mBinding.imageViewDelete.setVisibility(View.GONE);

                }
                mBinding.textViewMsgOn.setText(mMessage.getDateFormat());
                mBinding.textViewMsgText.setText(mMessage.message);

                //onclick display alert dialogue to confirm user action of deleting message
                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setMessage("Delete message?");
                        dialog.setCancelable(true);
                        dialog.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            //remove message from list
                            // update list in messages collection
                            public void onClick(DialogInterface dialogInterface, int i) {
                                HashMap<String, Object> msgUpdate = new HashMap<>();
                                messages.remove(position);
                                msgUpdate.put("messages", messages);
                                db.collection("chatrooms").document(chat.chat_id)
                                        .update(msgUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        });
                        AlertDialog alert = dialog.create();
                        alert.show();


                    }
                });

            }
        }

    }
}