package com.hsu.davincicode;

import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class NetworkUtils {

    public NetworkObj networkObj;

    public NetworkUtils(NetworkObj networkObj) {
        this.networkObj = networkObj;
    }

    public void logout(ChatMsg cm) {
        new Thread() {
            public void run() {
                try {
                    sendChatMsg(cm);
                    networkObj.getOos().close();
                    networkObj.getOis().close();
                    networkObj.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void sendChatMsg(ChatMsg cm) {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    networkObj.getOos().writeObject(cm.code);
                    networkObj.getOos().writeObject(cm.UserName);
                    networkObj.getOos().writeObject(cm.data);
                    if (cm.code.equals("300")) // Image 첨부인 경우
                        networkObj.getOos().writeObject(cm.imgbytes);
                    //oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // ChatMsg 를 읽어서 Return, Java 호환성 문제로 field별로 수신해서 ChatMsg 로 만들어 Return
    public ChatMsg readChatMsg()  {
        String code = null, userName = null, data = null;
        ChatMsg cm = new ChatMsg("","","");
        try {
            cm.code = (String) networkObj.getOis().readObject();
            cm.UserName = (String) networkObj.getOis().readObject();
            cm.data = (String) networkObj.getOis().readObject();
            if (cm.code.matches("300")) {
                cm.imgbytes = (byte[]) networkObj.getOis().readObject();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logout(cm);
            e.printStackTrace();
        }
        return cm;
    }

}
