package ru.scrait.technostrelka.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import ru.scrait.technostrelka.R;
import ru.scrait.technostrelka.models.Transaction;
import ru.scrait.technostrelka.ui.auth.AuthActivity;

public class DialogUtils {
    private static float balance;
    private static float reserved;
    private static DatabaseReference databaseReferenceBalance = null;
    private static DatabaseReference databaseReferenceReserved = null;

    public static void onNewTransaction(String sumFromReceipt, Context context, View view) {
        updateBD(context);
        Dialog dialog = new Dialog(context);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference userReferance = FirebaseDatabase.getInstance().getReference(AuthActivity.USER_KEY).child(currentUser.getUid());
        final DatabaseReference moneyReferance = userReferance.child("TransactionsOfUserMoney");
        Date date = new Date();
        if (sumFromReceipt != null) {
            dialog.setContentView(R.layout.dialog_transaction_receipt);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Spinner category = dialog.findViewById(R.id.category);
            Button confirm = dialog.findViewById(R.id.confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Transaction transaction = new Transaction(category.getSelectedItem().toString(), Float.parseFloat(sumFromReceipt), "рассход".toUpperCase());
                    moneyReferance.child(String.valueOf(date)).setValue(transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            databaseReferenceBalance.setValue(balance - Float.parseFloat(sumFromReceipt));
                            Snackbar.make(view, "Трата успешно добавлена", Snackbar.LENGTH_LONG).show();
                            updateBD(context);
                        }
                    });
                    dialog.dismiss();
                }
            });
        } else {
            dialog.setContentView(R.layout.dialog_transaction);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Spinner category = dialog.findViewById(R.id.category);
            EditText sum = dialog.findViewById(R.id.sum);
            Switch value_of_transaction = dialog.findViewById(R.id.value_of_transaction);
            Button confirm = dialog.findViewById(R.id.confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Transaction transaction;
                    if (value_of_transaction.isChecked()) {
                        transaction = new Transaction("доход".toUpperCase(), Float.parseFloat(sum.getText().toString()), "доход".toUpperCase());
                    } else {
                        transaction = new Transaction(category.getSelectedItem().toString(), Float.parseFloat(sum.getText().toString()), "рассход".toUpperCase());
                    }
                    moneyReferance.child(String.valueOf(date)).setValue(transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if (value_of_transaction.isChecked()) {
                                databaseReferenceBalance.setValue(balance + Float.parseFloat(sum.getText().toString()));
                            } else {
                                databaseReferenceBalance.setValue(balance - Float.parseFloat(sum.getText().toString()));
                            }
                            Snackbar.make(view, "Трата успешно добавлена", Snackbar.LENGTH_LONG).show();
                            updateBD(context);
                        }
                    });
                    dialog.dismiss();
                }
            });
        }
    }

    public static void onReplenishReserve(Context context) {
        updateBD(context);
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_reserve);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        EditText reserve_sum = dialog.findViewById(R.id.sum_reserve);
        dialog.findViewById(R.id.reserve_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reservedSum = String.valueOf(reserve_sum.getText());

                float reservedSumFloat = Float.parseFloat(reservedSum) + reserved;
                databaseReferenceReserved.setValue(reservedSumFloat);
                databaseReferenceBalance.setValue(balance - Float.parseFloat(reservedSum));

                dialog.dismiss();
                updateBD(context);
            }
        });
    }

    public static void updateBD(Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase databaseReferences = FirebaseDatabase.getInstance();
        final DatabaseReference myRefetance = databaseReferences.getReference(AuthActivity.USER_KEY);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WalletChannel")
                .setContentTitle("БАЛАНС ОТРИЦАТИЛЕН!")
                .setContentText("Необходимо внести большк средств")
                .setOngoing(true)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.img)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (currentUser != null) {
            databaseReferenceBalance = myRefetance.child(currentUser.getUid()).child("balance");
            databaseReferenceReserved = myRefetance.child(currentUser.getUid()).child("reservedSum");
            databaseReferenceBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    balance = snapshot.getValue(Float.class);
                    ProfileUtils.update(balance, reserved, currentUser.getEmail());
                    if (balance < 100.0f) {
                        // Show notification
                        notificationManagerCompat.notify(101, builder.build());

                    } else {
                        // Close notification
                        notificationManagerCompat.cancel(101);

                    }
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
                    ProfileUtils.update(balance, reserved, currentUser.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
