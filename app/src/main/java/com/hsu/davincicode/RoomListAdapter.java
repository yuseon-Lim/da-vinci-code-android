package com.hsu.davincicode;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hsu.davincicode.R;
import com.hsu.davincicode.Room;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> {

    private ArrayList<Room> roomList;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRoomName;
        private final TextView tvMaxCount;
        private final Button btnRequestRoomEnterance;
        private String roomId;

        private UserInfo userInfo = UserInfo.getInstance();
        private NetworkObj networkObj = NetworkObj.getInstance();
        private NetworkUtils networkUtils;
        private String userName;

        public ViewHolder(View view) {
            super(view);

            tvRoomName = view.findViewById(R.id.tv_room_name);
            tvMaxCount = view.findViewById(R.id.tv_max_count);
            btnRequestRoomEnterance = view.findViewById(R.id.btn_request_room_enterance);

            userName = userInfo.getUserName();
            networkUtils = new NetworkUtils(networkObj);

            btnRequestRoomEnterance.setOnClickListener(v -> {
                String[] counts = getTvMaxCount().getText().toString().split("/");
                int curCount = Integer.parseInt(counts[0]);
                int maxCount = Integer.parseInt(counts[1]);

                if (curCount < maxCount) {
                    View dialogView = View.inflate(view.getContext(), R.layout.dialog_input_passwd, null);
                    EditText etPw = dialogView.findViewById(R.id.et_dialog_passwd);
                    AlertDialog builder = new AlertDialog.Builder(view.getContext())
                            .setView(dialogView)
                            .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("확인", (dialog, which) -> {
                                String msg = String.format("%s//%s", roomId, etPw.getText().toString());
                                ChatMsg obj = new ChatMsg(userName, "ROOMIN", msg);
                                networkUtils.sendChatMsg(obj); // 서버로 msg 전송
                                dialog.dismiss();
                            }).show();
                } else {
                    Snackbar.make(view, "방 인원수를 초과할 수 없습니다.", Snackbar.LENGTH_LONG).show();
                }
            });
        }

        public TextView getTvRoomName() {
            return tvRoomName;
        }

        public TextView getTvMaxCount() {
            return tvMaxCount;
        }
    }

    public RoomListAdapter(ArrayList<Room> roomList) {
        this.roomList = roomList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_roomlist, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.roomId = roomList.get(position).getRoomId();
        holder.getTvRoomName().setText(roomList.get(position).getRoomName());
        holder.getTvMaxCount().setText(String.format("%s/%s", roomList.get(position).getCurCount(), roomList.get(position).getMaxCount()));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}