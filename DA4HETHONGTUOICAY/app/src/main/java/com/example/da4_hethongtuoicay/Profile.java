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


public class Profile extends Fragment {

    private Dialog dialog;
    Button button;
    Button sua;
    Button notifition;
    Button payment;
    private ProgressDialog doimking;

    private DatabaseReference mkdatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutsetting);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mkdatabase = database.getReference("mk");
        button=view.findViewById(R.id.doimk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {doimk(Gravity.CENTER);}
        });


        sua =view.findViewById(R.id.change);
        sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suatn(Gravity.CENTER);
            }
        });

// hiển thị tên lên trên cùng
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("user/name");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String last = snapshot.child("last").getValue(String.class);
                    // Hiển thị giá trị của key "first" và "last" trong TextView hoặc ListView
                    TextView textView = view.findViewById(R.id.name);
                    textView.setText(last);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });



        // hiển thị email lên trên cùng
        TextView emailtext = view.findViewById(R.id.email);
        DatabaseReference email = FirebaseDatabase.getInstance().getReference("user/email");
        email.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                emailtext.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });

// Hiển thị giá trị của key "first" và "last" trong key "name"
        // hiển thị họ và tên
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user/name");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String first = snapshot.child("first").getValue(String.class);
                    String last = snapshot.child("last").getValue(String.class);

                    // Hiển thị giá trị của key "first" và "last" trong TextView hoặc ListView
                    TextView textView = view.findViewById(R.id.lop);
                    textView.setText(first +" "+ last);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });



        // hien thi msv
        TextView masv;
        DatabaseReference viewmsv = FirebaseDatabase.getInstance().getReference();
        masv = view.findViewById(R.id.msv);
        viewmsv.child("user/date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                masv.setText(snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        // hien thi diachi
        TextView diachi;
        DatabaseReference viewdiachi = FirebaseDatabase.getInstance().getReference();
        diachi = view.findViewById(R.id.diachi);
        viewdiachi.child("user/diachi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                diachi.setText(snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        // hien thi msv
        TextView sdt;
        DatabaseReference viewsdt = FirebaseDatabase.getInstance().getReference();
        sdt = view.findViewById(R.id.sdt);
        viewsdt.child("user/sdt").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sdt.setText(snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




        // button payment

        payment=view.findViewById(R.id.payment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { setPayment(Gravity.CENTER);

            }
        });


        // button payment

        notifition=view.findViewById(R.id.notification);
        notifition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { xoa(Gravity.CENTER);

            }
        });



        return view;
    }


    private void doimk(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.doimk);
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
        EditText mkcu = dialog.findViewById(R.id.mkcu);
        EditText mkmoi = dialog.findViewById(R.id.mkmoi);
        EditText xacnhanmkmoi = dialog.findViewById(R.id.xacnhanmkmoi);
        Button btnno = dialog.findViewById(R.id.no);
        Button btnyes = dialog.findViewById(R.id.yes);
        DatabaseReference mkdatabase = FirebaseDatabase.getInstance().getReference().child("login/mk");
        DatabaseReference finalMdatabasemk = FirebaseDatabase.getInstance().getReference().child("login/mk");
        doimking= new ProgressDialog(getContext());
        EditText finalEditTextmkcu = mkcu;
        EditText finalEditTextmkmoi = mkmoi;
        EditText finalEditTextxacnhanmkmoi = xacnhanmkmoi;
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String datamkcu = finalEditTextmkcu.getText().toString();
                String datamkmoi = finalEditTextmkmoi.getText().toString();
                String dataxacnhanmkmoi = finalEditTextxacnhanmkmoi.getText().toString();

                mkdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String mk = dataSnapshot.getValue(String.class);

                        if (datamkcu.isEmpty() || datamkmoi.isEmpty() || dataxacnhanmkmoi.isEmpty()) {
                            Toast.makeText(getContext(), "Không được bỏ trống", Toast.LENGTH_LONG).show();
                            return;
                        } else if (!mk.equals(datamkcu)) {
                            Toast.makeText(getContext(), "Mật khẩu cũ không chính xác", Toast.LENGTH_LONG).show();
                            return;
                        } else if (datamkmoi.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?âăôêơưáảãạà ếểễệ].*")) {
                            Toast.makeText(getContext(), "Mật khẩu chứa ký tự không hợp lệ", Toast.LENGTH_LONG).show();
                            return;
                        }else if (!datamkmoi.equals(dataxacnhanmkmoi)) {
                            Toast.makeText(getContext(), "Mật khẩu xác nhận không trùng khớp!", Toast.LENGTH_LONG).show();
                            return;

                        } else if(datamkmoi.length() < 8){
                            Toast.makeText(getContext(), "Mật khẩu có ít nhất 8 kí tự.", Toast.LENGTH_LONG).show();
                            return;
                        }else if(datamkmoi.length() > 32){
                            Toast.makeText(getContext(), "Mật khẩu phải ít hơn 32 kí tự.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else if (datamkcu.equals(datamkmoi)){
                            Toast.makeText(getContext(), "Mật Khẩu Mới Phải Khác Mật Khẩu Cũ", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else {
                            finalMdatabasemk.setValue(datamkmoi).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    doimking.setMessage("Đang Đổi Mật Khẩu");
                                    doimking.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            doimking.dismiss();
                                        }
                                    }, 1000); //tắt dialog sau 3 giây (3000ms)
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "Đổi Mật Khẩu Thành Công", Toast.LENGTH_LONG).show();
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
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
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

    private void suatn(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.suatn);
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
        EditText fixten = dialog.findViewById(R.id.fixten);
        EditText fixdate = dialog.findViewById(R.id.fixdate);
        EditText fixsdt = dialog.findViewById(R.id.fixsdt);
        EditText fixdiachi = dialog.findViewById(R.id.fixdiachi);
        EditText fixemail = dialog.findViewById(R.id.fixemail);
        EditText fixlast = dialog.findViewById(R.id.fixlast);
        Button btnno = dialog.findViewById(R.id.no);
        Button btnyes = dialog.findViewById(R.id.yes);
        DatabaseReference fixdatalop = FirebaseDatabase.getInstance().getReference().child("user/name/first");
        DatabaseReference fixdatalast = FirebaseDatabase.getInstance().getReference().child("user/name/last");
        DatabaseReference fixdatasdt = FirebaseDatabase.getInstance().getReference().child("user/sdt");
        DatabaseReference fixdatadiachi = FirebaseDatabase.getInstance().getReference().child("user/diachi");
        DatabaseReference fixdatamsv = FirebaseDatabase.getInstance().getReference().child("user/date");
        DatabaseReference fixdataemail = FirebaseDatabase.getInstance().getReference().child("user/email");
        doimking= new ProgressDialog(getContext());
        EditText finalEditTextlop = fixten;
        EditText finalEditTextmsv = fixdate;
        EditText finalEditTextfixsdt = fixsdt;
        EditText finalEditTextdiachi = fixdiachi;
        EditText finalEditTextlast = fixlast;
        EditText finalEditTextemail = fixemail;



        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String datalop = finalEditTextlop.getText().toString().trim();
                String datamsv = finalEditTextmsv.getText().toString().trim();
                String datasdt = finalEditTextfixsdt.getText().toString().trim();
                String datadiachi = finalEditTextdiachi.getText().toString().trim();
                String datalast = finalEditTextlast.getText().toString().trim();
                String dataemail = finalEditTextemail.getText().toString().trim();

                mkdatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String mk = dataSnapshot.getValue(String.class);

                        if (datalop.isEmpty() || datamsv.isEmpty() || datasdt.isEmpty() || datadiachi.isEmpty()) {
                            Toast.makeText(getContext(), "Không được bỏ trống", Toast.LENGTH_LONG).show();
                            return;
                        } else if (datalop.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                            Toast.makeText(getContext(), "Họ chứa ký tự không hợp lệ", Toast.LENGTH_LONG).show();
                            return;
                        }else if(datasdt.length()<10 || datasdt.length() >10){
                            Toast.makeText(getContext(), "Số điện thoại chưa đúng!!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else if (datalast.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                            Toast.makeText(getContext(), "Tên chứa ký tự không hợp lệ", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else {
                            fixdataemail.setValue(dataemail);
                            fixdatalast.setValue(datalast);
                            fixdatadiachi.setValue(datadiachi);
                            fixdatalop.setValue(datalop);
                            fixdatamsv.setValue(datamsv);
                            fixdatasdt.setValue(datasdt).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    doimking.setMessage("Đang sửa thông tin");
                                    doimking.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            doimking.dismiss();
                                        }
                                    }, 1000); //tắt dialog sau 3 giây (3000ms)
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "Sửa thông tin thành công.", Toast.LENGTH_LONG).show();
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
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
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

    private void setPayment(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.thanhtoan);
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
        Button btnyes = dialog.findViewById(R.id.yes);
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void xoa(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.xoa);
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
        Button btnyes = dialog.findViewById(R.id.yes);
        Button btnno = dialog.findViewById(R.id.no);
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference xoadatabase = FirebaseDatabase.getInstance().getReference().child("monitor/luongnuoc");
                xoadatabase.setValue(0);
                dialog.dismiss();
                Toast.makeText(getContext(), "Xóa thành công !!!", Toast.LENGTH_LONG).show();
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