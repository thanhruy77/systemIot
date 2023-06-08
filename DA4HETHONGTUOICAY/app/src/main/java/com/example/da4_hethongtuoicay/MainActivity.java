package com.example.da4_hethongtuoicay;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    private TabLayout mtablayout;
    private ViewPager mviewpager;
    private int backPressedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtablayout = findViewById(R.id.tablayout);
        mviewpager = findViewById(R.id.view_pager);
        viewpage viewpage = new viewpage(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mviewpager.setAdapter(viewpage);
        mtablayout.setupWithViewPager(mviewpager);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // dữ liệu database, nếu chưa có sẽ thêm.
        DatabaseReference mdatabase = database.getReference("control/maybom");
        // gửi thông báo độ ẩm
        // Khai báo biến để kiểm tra xem thông báo đã được gửi hay chưa
        final boolean[] notificationSent = {false};
        DatabaseReference databasekiemtra = FirebaseDatabase.getInstance().getReference();
        databasekiemtra.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int gioiHanDoAm = Integer.parseInt(dataSnapshot.child("value/max").getValue().toString());
                int doAmDat = Integer.parseInt(dataSnapshot.child("monitor/doamdat").getValue().toString());
                if (gioiHanDoAm <= doAmDat && !notificationSent[0]) { // Kiểm tra xem độ ẩm có vượt quá ngưỡng và chưa gửi thông báo trước đó
                    // Gửi thông báo
                    mdatabase.setValue(0);
                    int NOTIFICATION_ID = 2;
                    String channelId = "channel_id";
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channelId)
                            .setContentTitle("Độ Ẩm")
                            .setContentText("Đã Đạt Ngưỡng")
                            .setSmallIcon(R.mipmap.tuoicay_foreground)
                            .setAutoCancel(true);
                    NotificationManager notificationManager = (NotificationManager)
                            MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(channelId,
                                "Hệ thống tưới cây", NotificationManager.IMPORTANCE_DEFAULT);
                        if (notificationManager != null) {
                            notificationManager.createNotificationChannel(channel);
                        }
                    }
                    if (notificationManager != null) {
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    }
                    // Đặt biến thông báo đã được gửi thành true
                    notificationSent[0] = true;
                } else if (gioiHanDoAm > doAmDat && notificationSent[0]) { // Kiểm tra xem độ ẩm đã giảm xuống dưới ngưỡng
                    // Đặt biến thông báo đã được gửi thành false
                    notificationSent[0] = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




        final boolean[] notificationSentt = {false};
        DatabaseReference mucnuocdatabase = FirebaseDatabase.getInstance().getReference();
        mucnuocdatabase.child("monitor/mucnuoc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int data = Integer.parseInt(snapshot.getValue().toString());
                if (data >= 8) {
                    notificationSentt[0] = false; // Đặt biến thông báo đã được gửi thành false khi mức nước dưới 8
                } else {
                    if (!notificationSentt[0]) { // Kiểm tra xem thông báo đã được gửi hay chưa
                        thongbaonuoc();
                        notificationSentt[0] = true; // Đặt biến thông báo đã được gửi thành true
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    // gửi thông báo hết nước
    private void thongbaonuoc() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.water_foreground);
        String channelId = "channel_id";
        int NOTIFICATION_ID = 1;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channelId)
                .setContentTitle("Nước")
                .setContentText("Đã Hết")
                .setSmallIcon(R.mipmap.tuoicay_foreground)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null))
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Hệ thống tưới cây", NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
    @Override
    public void onBackPressed() {
        if (backPressedCount < 1) {
            backPressedCount++;
            Toast.makeText(this, "Nhấn back lần nữa để thoát", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();    // Thoát ứng dụng
            finishAndRemoveTask();
        }
    }
}
