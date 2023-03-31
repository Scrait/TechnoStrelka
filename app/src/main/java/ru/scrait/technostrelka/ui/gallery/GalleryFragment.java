package ru.scrait.technostrelka.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ru.scrait.technostrelka.databinding.FragmentGalleryBinding;
import ru.scrait.technostrelka.models.Transaction;
import ru.scrait.technostrelka.ui.auth.AuthActivity;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private ArrayList<String> listDates = new ArrayList<String>();
    private ArrayList<String> listTypes = new ArrayList<String>();
    private ArrayList<String> listCategories = new ArrayList<String>();
    private ArrayList<String> listSums = new ArrayList<String>();
    private RecyclerViewHistoryAdapter recyclerViewHistoryAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;
        final RecyclerView rvHistory = binding.history;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewHistoryAdapter = new RecyclerViewHistoryAdapter(
                getActivity(),
                listDates,
                listSums,
                listCategories,
                listTypes);

        rvHistory.setAdapter(recyclerViewHistoryAdapter);
        getTransactionHistory();
        return root;
    }

    private void getTransactionHistory() {

        DatabaseReference databaseReferenceTransactions = FirebaseDatabase.getInstance().getReference(AuthActivity.USER_KEY).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("TransactionsOfUserMoney");

        databaseReferenceTransactions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                listCategories.clear();
                listSums.clear();
                listTypes.clear();
                listDates.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Transaction transactionOfUserMoney = dataSnapshot.getValue(Transaction.class);

                    listCategories.add(0, transactionOfUserMoney.category);
                    listSums.add(0, String.valueOf(transactionOfUserMoney.sum));
                    listTypes.add(0, transactionOfUserMoney.type);
                    listDates.add(0, transactionOfUserMoney.date);
                }

                recyclerViewHistoryAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}