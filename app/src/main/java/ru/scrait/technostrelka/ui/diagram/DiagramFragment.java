package ru.scrait.technostrelka.ui.diagram;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import ru.scrait.technostrelka.databinding.FragmentDiagramBinding;
import ru.scrait.technostrelka.ui.auth.AuthActivity;

public class DiagramFragment extends Fragment {

    private FragmentDiagramBinding binding;
    private List<String> categories = new ArrayList<>();
    private List<String> sums = new ArrayList<>();
    private ArrayList<BarEntry> ds1 = new ArrayList<>();
    private ArrayList<BarEntry> ds2 = new ArrayList<>();
    private int x = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DiagramViewModel diagramViewModel =
                new ViewModelProvider(this).get(DiagramViewModel.class);

        binding = FragmentDiagramBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        diagramViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AuthActivity.USER_KEY);
        final DatabaseReference databaseReferenceChild = databaseReference.child(currentUser.getUid()).child("TransactionsOfUserMoney");;


        databaseReferenceChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot DataSnapshot : snapshot.getChildren()) {
                    if (!DataSnapshot.child("type").getValue().toString().equals("ДОХОД")) {
                        categories.add(DataSnapshot.child("category").getValue().toString());
                        sums.add(DataSnapshot.child("sum").getValue().toString());
                    }
                }
                Log.e("categories", categories.toString());
                Log.e("sums", sums.toString());
                Snackbar.make(getView(), "Добавилось", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(getView(), "Произошла ошибка", Snackbar.LENGTH_LONG).show();;
            }
        });
        binding.generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDiagram();
            }
        });
        return root;
    }

    private void createDiagram() {
        Map<String, Integer> frequency = categories.stream()
                .collect(Collectors.toMap(
                        e -> e,
                        e -> 1,
                        Integer::sum));

        frequency.forEach((k, v) -> trahalka(k));

        final BarChart barChart = binding.chart;
        BarDataSet barDataSet = new BarDataSet(ds1, "Diagram");
        BarDataSet barDataSet2 = new BarDataSet(ds2, "Pon");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(20f);

        BarData barData = new BarData(barDataSet);
        BarData barData2 = new BarData(barDataSet2);


        Legend legend = barChart.getLegend();
        legend.setEnabled(true);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Категории трат");
        barChart.animateY(2000);
        categories.clear();
        sums.clear();
    }

    private void trahalka(String k) {
        int pizda = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).equals(k)) pizda += Float.parseFloat(sums.get(i));
        }
        ds1.add(new BarEntry(x, pizda));
        ds2.add(new BarEntry(x - 1, pizda - 1));
        x += 10 / categories.size();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}