package ru.scrait.technostrelka.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;

import ru.scrait.technostrelka.MainActivity;
import ru.scrait.technostrelka.databinding.FragmentHomeBinding;
import ru.scrait.technostrelka.utils.DialogUtils;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CodeScanner codeScanner;
    private String sumFromReceipt;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        startScanning();
        return root;
    }

    private void startScanning() {
        CodeScannerView scannerView = binding.scannerView;

        codeScanner = new CodeScanner(getActivity(), scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NotNull final Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(ScanQrActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        String rawSumFromCheque = result.getText().substring(
                                result.getText().indexOf("s")+2,
                                result.getText().indexOf("f")-1);

                        sumFromReceipt = rawSumFromCheque.substring(0, rawSumFromCheque.indexOf("."));
                        //sumFromReceiptFloat = Float.parseFloat(sumFromReceipt);

                        DialogUtils.onNewTransaction(sumFromReceipt, getActivity(), getView());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}