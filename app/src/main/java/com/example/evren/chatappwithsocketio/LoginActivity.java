package com.example.evren.chatappwithsocketio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by EVREN on 17.10.2017.
 */

public class LoginActivity extends Activity {
    private EditText mUsernameView,mRoomView;

    private String mUsername,mRoomname;
    //Socket sınıfını kullanarak, sunucudaki chat yazılımı ile bağlantı kuruyoruz
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mUsernameView = (EditText) findViewById(R.id.username_input);
        mRoomView = (EditText) findViewById(R.id.room_input);
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                attemptLogin2();
                Intent intent = new Intent();
                intent.putExtra("username", mUsername);
                intent.putExtra("room", mRoomname);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }

    private void attemptLogin() {
        mRoomView.setError(null);

        String room = mRoomView.getText().toString().trim();
        // Kulanıcı adı kontrol ediliyor
        if (!mSocket.connected()){
            Toast.makeText(getApplicationContext(),"Bağlantı Hatası",Toast.LENGTH_SHORT).show();
            return;}
        if (TextUtils.isEmpty(room)) {
            mRoomView.setError("Bu alanı boş bırakmayın");
            mRoomView.requestFocus();
            return;
        }
        mRoomname = room;

        //Kullanıcı adı, socket'e ekleniyor
        mSocket.emit("channelfixer", room);
    }
    private void attemptLogin2() {
        mUsernameView.setError(null);
        String username = mUsernameView.getText().toString().trim();

        if (!mSocket.connected()){
            Toast.makeText(getApplicationContext(),"Bağlantı Hatası",Toast.LENGTH_SHORT).show();
            return;}
        // Kulanıcı adı kontrol ediliyor
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError("Bu alanı boş bırakmayın");
            mUsernameView.requestFocus();
            return;
        }
        mUsername = username;

        //Kullanıcı adı, socket'e ekleniyor
        mSocket.emit("user", username);
    }

}
