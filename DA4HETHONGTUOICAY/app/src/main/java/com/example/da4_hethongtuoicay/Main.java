package com.example.da4_hethongtuoicay;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends Fragment {
    private Dialog dialog;
    Button bat;
    Button batled;
    TextView hienthinhietdo;
    TextView hienthidoam;
    TextView gioihandoam;
    DatabaseReference mdatabase;
    DatabaseReference leddatabase;
    final boolean[] maybomon = {false};
    private ProgressDialog setupdoaming;
    int ledon = 1;
    int ledoff = 0;
    final boolean[] denledon = {false};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutsetting);
        setupdoaming = new ProgressDialog(getContext());
        // Khởi tạo database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Truy cập đến child node "maybom" của DatabaseReference
        mdatabase = database.getReference("control/maybom");
        leddatabase = database.getReference("control/led");



        // tạo biến kiểm tra kết nối wifi.
        ImageView wifi = view.findViewById(R.id.wifi);
        DatabaseReference connectesp = database.getReference("connect/espcontrol");
        final int[] checkcount_wfi= new int[1];
        connectesp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int data = Integer.parseInt(snapshot.getValue().toString());
                checkcount_wfi[0]=data;
                if(checkcount_wfi[0]>=50 && checkcount_wfi[0] <=52){
                    wifi.setBackgroundResource(R.drawable.wifi);
                }
                else {
                    wifi.setBackgroundResource(R.drawable.diswifi);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        // Gửi yêu cầu heartbeat đến Firebase định kỳ
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Gửi yêu cầu tới Firebase
                checkcount_wfi[0]+=1;
                if(checkcount_wfi[0]>30 && checkcount_wfi[0]<40){
                    checkcount_wfi[0]=1;
                }
                connectesp.setValue(checkcount_wfi[0]);
            }
        },0,3800); // Gửi yêu cầu mỗi 3.8s




        // update trang thai may bom
        DatabaseReference finalMdatabase = FirebaseDatabase.getInstance().getReference();
        finalMdatabase.child("control/maybom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue().toString().equals("1")) {
                    bat.setBackgroundResource(R.drawable.pumpstart);
                } else {
                    bat.setBackgroundResource(R.drawable.pump);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        // update trang thai led
        DatabaseReference finalMdatabaseled = FirebaseDatabase.getInstance().getReference();
        finalMdatabaseled.child("control/led").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals("1")) {
                    batled.setBackgroundResource(R.drawable.ledon);
                } else {
                    batled.setBackgroundResource(R.drawable.led);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        // bat may bom
        bat = view.findViewById(R.id.bat);
        bat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maybomon[0] = !maybomon[0];
                int data = maybomon[0] ? 1: 0;
                mdatabase.setValue(data);
            }
        });


        // bat led
        batled = view.findViewById(R.id.batled);
        batled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                denledon[0] = !denledon[0]; // toggle the value of isLedOn
                int data = denledon[0] ? ledon : ledoff;
                leddatabase.setValue(data);
                if (denledon[0]) {
                    batled.setBackgroundResource(R.drawable.ledon);
                } else {
                    batled.setBackgroundResource(R.drawable.led);
                }
            }
        });


        // hien thi lưu lượng nước
        DatabaseReference tdatabase = FirebaseDatabase.getInstance().getReference();
        hienthinhietdo = view.findViewById(R.id.nhan1);
        tdatabase.child("monitor/luuluong").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hienthinhietdo.setText(snapshot.getValue().toString() + "L/M ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        //hiển thị lượng nước
        DatabaseReference hdatabase = FirebaseDatabase.getInstance().getReference();
        hienthidoam = view.findViewById(R.id.doam);
        hdatabase.child("monitor/luongnuoc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hienthidoam.setText(snapshot.getValue().toString() + "L ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        // hiển thị giới hạn độ ẩm
        DatabaseReference ghdatabase = FirebaseDatabase.getInstance().getReference();
        gioihandoam = view.findViewById(R.id.doamgioihan);
        ghdatabase.child("value/max").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gioihandoam.setText(snapshot.getValue().toString() + "% ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // hien thi do am dat
        TextView hienthidoamdat;
        DatabaseReference hdatdatabase = FirebaseDatabase.getInstance().getReference();
        hienthidoamdat = view.findViewById(R.id.doamdat);
        hdatdatabase.child("monitor/doamdat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hienthidoamdat.setText(snapshot.getValue().toString() + "% ");
               }
            @Override
                public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        // tat may bom khi da du do am
        DatabaseReference databasekiemtra1 = FirebaseDatabase.getInstance().getReference();
        databasekiemtra1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int gioiHanDoAm = Integer.parseInt(dataSnapshot.child("value/max").getValue().toString());
                int doAmDat = Integer.parseInt(dataSnapshot.child("monitor/doamdat").getValue().toString());
                if (gioiHanDoAm <= doAmDat) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mdatabase.setValue(0);
                        }
                    }, 3000); //tắt may bom sau 3 giây (3000ms)
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});



        // hien thi muc nuoc
        TextView mucnuoc;
        DatabaseReference mucnuocdatabase = FirebaseDatabase.getInstance().getReference();
        mucnuoc = view.findViewById(R.id.mucnuoc);
        mucnuocdatabase.child("monitor/mucnuoc").addValueEventListener(new ValueEventListener() {
            private static final int NOTIFICATION_ID = 1;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int data = Integer.parseInt(snapshot.getValue().toString());
                if(data >100){
                    mucnuoc.setText(100+"% ");
                }
                else{
                    mucnuoc.setText(data+"% ");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        // setup đọ ẩm {
        Button pushbtn = view.findViewById(R.id.setup);
        pushbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupdoam(Gravity.CENTER);
            }
        });




        return view;

    }



    // set up do ẩm
    private void setupdoam(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutsetting);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        EditText editText = dialog.findViewById(R.id.feedback);
        Button btnno = dialog.findViewById(R.id.no);
        Button btnyes = dialog.findViewById(R.id.yes);
        DatabaseReference finalMdatabase = FirebaseDatabase.getInstance().getReference().child("value/max");
        EditText finalEditText = editText;
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = finalEditText.getText().toString();
                if (data.isEmpty()) {
                    Toast.makeText(getContext(), "Không được bỏ trống", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    int dataint = Integer.parseInt(data);
                    if(dataint >100 || dataint <20){
                        Toast.makeText(getContext(), "Độ Ẩm Max Không Thể Nhỏ Hơn 20% Hoặc Lớn Hơn 100%", Toast.LENGTH_LONG).show();
                    }
                    else {
                        finalMdatabase.setValue(dataint).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                setupdoaming.setMessage("Đang thiết lập");
                                setupdoaming.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setupdoaming.dismiss();
                                    }
                                }, 1000); //tắt dialog sau 1 giây (1000ms)
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Setup Độ Ẩm Thành Công", Toast.LENGTH_LONG).show();
                                    }
                                }, 1000);
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý lỗi nếu có
                            }
                        });
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Dữ liệu nhập vào không hợp lệ", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();




    }

}

