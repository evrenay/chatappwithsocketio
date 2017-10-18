package com.example.evren.chatappwithsocketio;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by EVREN on 15.10.2017.
 */

class MainFragment extends Fragment {
    private RecyclerView.Adapter mAdapter;
    private static final int REQUEST_LOGIN = 0;
    private ArrayList<Message> messageArrayList = new ArrayList<Message>();
    private Socket mSocket;
    RecyclerView recyclerView;
    EditText mInputMessageView;
    private String mUsername,nRoom;
    {
        try {
            //sunucudaki chat yazılımının url set ettik
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public MainFragment() {
        super(); // fragment class metotları kullanabilmek için super metot ürettik.
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Mesajları listelicek olan adapter tanımladık
        mAdapter = new MessageAdapter(messageArrayList,activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        //Socket sınıfına işlevseliği olan listener metodlarını set ettik
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        mSocket.on("message", onNewMessage);
        //Socket ile bağlantı kurdum..
        mSocket.connect();

        startSignIn();//sınıf seç

    }





    //Socket ile bağlantığı kurulduğunda bir hatayla karşılasınca uyarı veren metod
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Bağlantı Hatası", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    //Kullanıcıya cevap olarak verilen yeni mesajları socket'den  dinleyen metod
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("dfdsfsdf","fsdfsd");
                    JSONObject data = (JSONObject) args[0];

                    String message;
                    try {
                        message = data.getString("mesaj");

                    } catch (JSONException e) {
                        return;
                    }


                    //Kullanıcıya cevap olarak verilen yeni mesajları ve cevap veren kullanıcıyı, mesaj listesine eklemesini sağlıyoruz
                    addMessage(message);
                }
            });
        }
    };

    private void  addMessage(String message){
        messageArrayList.add(new Message(message));
        mAdapter.notifyItemInserted(messageArrayList.size()-1);
        scrollToBottom();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
    }

    //Kullanıcının girdiği mesajı  soket sınıfına set eden metod
    private void attemptSend() {
        if (null == mUsername) return;
        //Kullanıcı adı ve socket ile baglantı kontrol ediyoruz...
        if (!mSocket.connected()) return;

        //Edittext'den mesaj alındı
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");


        //Kullanıcının girdiği mesajı  soket sınıfına set ettim.
        mSocket.emit("message", message);

        addMessage(message);
        Log.i("Liste : ", String.valueOf(messageArrayList));
    }
    private void scrollToBottom() {
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("message", onNewMessage);
    }

    private void startSignIn() {
        mUsername = null;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent,REQUEST_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            getActivity().finish();
            return;
        }

        mUsername = data.getStringExtra("username");
        nRoom  = data.getStringExtra("room");



    }

}
