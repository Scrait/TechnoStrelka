package ru.scrait.technostrelka.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.scrait.technostrelka.MainActivity;
import ru.scrait.technostrelka.ui.auth.AuthActivity;

public class ProfileUtils {
    private static String uid;
    private static String email;
    private static float balance;
    private static float reserved;
    private static DatabaseReference databaseReferenceBalance;
    private static DatabaseReference databaseReferenceReserved;

    public static void setupProfile(Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase databaseReferences = FirebaseDatabase.getInstance();
        final DatabaseReference myRefetance = databaseReferences.getReference(AuthActivity.USER_KEY);

        if (currentUser != null) {
            email = currentUser.getEmail();
            uid = currentUser.getUid();
            databaseReferenceBalance = myRefetance.child(uid).child("balance");
            databaseReferenceReserved = myRefetance.child(uid).child("reservedSum");
            /*replenish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    balance += Integer.parseInt(replenish.getText().toString());
                    databaseReference.setValue(balance);
                    dashboardViewModel.update(balance, email);
                }
            });*/
            databaseReferenceBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    balance = snapshot.getValue(Float.class);
                    update(balance, reserved, email);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Произошла ошибка", Toast.LENGTH_SHORT);
                    //toast1.show();
                }
            });
            databaseReferenceReserved.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    reserved = snapshot.getValue(Float.class);
                    update(balance, reserved, email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            //Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Вы не авторизованы!", Toast.LENGTH_SHORT);
            //toast1.show();
        }
    }

    static void update(float balance, float reserved, String email) {
        MainActivity.textBalance.setText("Баланс:" + String.valueOf(balance) + "руб, Зарезервированно: " + reserved);
        MainActivity.textEmail.setText(email);
    }
}
