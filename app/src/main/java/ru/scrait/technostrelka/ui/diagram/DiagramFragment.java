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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.Map;
import java.util.stream.Collectors;

import ru.scrait.technostrelka.databinding.FragmentDiagramBinding;

public class DiagramFragment extends Fragment {

    private FragmentDiagramBinding binding;
    private ArrayList<String> categories;
    private ArrayList<BarEntry> ds1 = new ArrayList<>();
    private int x = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DiagramViewModel diagramViewModel =
                new ViewModelProvider(this).get(DiagramViewModel.class);

        binding = FragmentDiagramBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        diagramViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        createDiagram();
        return root;
    }

    private void createDiagram() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference databaseReferenceChild = databaseReference.child("User").child(currentUser.getUid());


        databaseReferenceChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //collectCategories((Map<String,Object>) snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        List<String> list = Arrays.asList(
                "Здоровье", "Здоровье", "Пизда", "Здоровье", "Хуй");
        List<Integer> list2 = Arrays.asList(
                202, 22, 32, 44, 33);

        Map<String, Integer> frequency = list.stream()
                .collect(Collectors.toMap(
                        e -> e,
                        e -> 1,
                        Integer::sum));
        frequency.forEach((k, v) -> trahalka(k, list, list2));

        final BarChart barChart = binding.chart;
        BarDataSet barDataSet = new BarDataSet(ds1, "Diagram");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(20f);

        BarData barData = new BarData(barDataSet);

        Legend legend = barChart.getLegend();
        legend.setEnabled(true);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Категории трат");
        barChart.animateY(2000);
    }

    private void trahalka(String k, List<String> list, List<Integer> list2) {
        int pizda = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(k)) pizda += Integer.parseInt(String.valueOf(list2.get(i)));
        }
        ds1.add(new BarEntry(x, pizda));
        x += 10 / list.size();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}