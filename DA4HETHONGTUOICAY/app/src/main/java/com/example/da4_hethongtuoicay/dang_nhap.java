package com.example.da4_hethongtuoicay;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dang_nhap extends AppCompatActivity {
    private DatabaseReference tkdatabase;
    private DatabaseReference mkdatabase;
    private ProgressDialog logining;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dang_nhap);
        EditText tk;
        EditText mk;
        TextView qmk;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutsetting);
        tk = (EditText) findViewById(R.id.tk);
        mk = (EditText) findViewById(R.id.mk);
        Button dangnhap;
        dangnhap = (Button) findViewById(R.id.dangnhap);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tkdatabase = database.getReference("login/tk");
        mkdatabase = database.getReference("login/mk");
        logining = new ProgressDialog(this);
        qmk = (TextView) findViewById(R.id.qmk);
        qmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quenmk(Gravity.CENTER);
            }
        });
        dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valuetk = tk.getText().toString().trim();
                String valuemk = mk.getText().toString().trim();
                tkdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String tk = dataSnapshot.getValue(String.class);
                        if (tk != null && tk.equals(valuetk)) {
                            mkdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String mk = dataSnapshot.getValue(String.class);
                                    if (mk != null && mk.equals(valuemk)) {
                                        logining.setMessage("Đang Đăng Nhập");
                                        logining.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                logining.dismiss();
                                            }
                                        }, 500);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent i = new Intent(dang_nhap.this, MainActivity.class);
                                                startActivity(i);
                                            }
                                        }, 150);
                                    } else {
                                        logining.setMessage("Đang Đăng Nhập");
                                        logining.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                logining.dismiss();
                                            }
                                        }, 1000);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(dang_nhap.this,
                                                        "Mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                            }
                                        }, 1000);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(dang_nhap.this, "Lỗi khi đọc dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(dang_nhap.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(dang_nhap.this, "Lỗi khi đọc dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void quenmk(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.qmk);
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
        EditText editText = dialog.findViewById(R.id.makh);
        Button btnno = dialog.findViewById(R.id.no);
        Button btnyes = dialog.findViewById(R.id.yes);
        EditText finalEditText = editText;
        DatabaseReference finalQmkdatabase = FirebaseDatabase.getInstance().getReference().child("login/mkh");
        DatabaseReference finalMdatabasemk = FirebaseDatabase.getInstance().getReference().child("login/mk");
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = finalEditText.getText().toString();
                if (data.isEmpty()) {
                    Toast.makeText(dang_nhap.this, "Bạn chưa nhập mã", Toast.LENGTH_LONG).show();
                    return;
                }
                finalQmkdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String qmk = snapshot.getValue(String.class);
                        if (qmk != null && qmk.equals(data)) {
                            finalMdatabasemk.setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(dang_nhap.this,"Mật Khẩu Của Bạn là: 1",Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Xử lý lỗi nếu có
                                }
                            });
                        } else {
                            Toast.makeText(dang_nhap.this, "Mã không chính xác!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu có
                    }
                });
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