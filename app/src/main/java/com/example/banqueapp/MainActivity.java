package com.example.banqueapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banqueapp.adapter.CompteAdapter;
import com.example.banqueapp.api.RetrofitInstance;
import com.example.banqueapp.model.Compte;
import com.example.banqueapp.model.TypeCompte;
import com.example.banqueapp.viewmodel.CompteViewModel;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements CompteAdapter.OnCompteActionListener {
    private CompteViewModel viewModel;
    private CompteAdapter adapter;
    private Spinner formatSpinner;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        formatSpinner = findViewById(R.id.spinnerFormat);
        progressBar = findViewById(R.id.progressBar);

        viewModel = new ViewModelProvider(this).get(CompteViewModel.class);
        setupRecyclerView();
        setupFormatSpinner();
        observeViewModel();

        findViewById(R.id.btnAddAccount).setOnClickListener(v -> showAddAccountDialog(null));

        // Initial load
        loadComptesBasedOnFormat();
    }

    private void setupFormatSpinner() {
        ArrayAdapter<String> formatAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"JSON", "XML"});
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(formatAdapter);

        formatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadComptesBasedOnFormat();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadComptesBasedOnFormat() {
        String selectedFormat = formatSpinner.getSelectedItem().toString();
        RetrofitInstance.DataFormat dataFormat = getDataFormat(selectedFormat);
        viewModel.loadComptes(dataFormat);
    }

    private RetrofitInstance.DataFormat getDataFormat(String format) {
        return "XML".equals(format) ? RetrofitInstance.DataFormat.XML : RetrofitInstance.DataFormat.JSON;
    }

    private void observeViewModel() {
        viewModel.getComptes().observe(this, comptes -> {
            adapter.submitList(comptes);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void setupRecyclerView() {
        adapter = new CompteAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void showAddAccountDialog(Compte compteToEdit) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_account, null);
        EditText etSolde = dialogView.findViewById(R.id.etSolde);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

        ArrayAdapter<TypeCompte> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TypeCompte.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        if (compteToEdit != null) {
            etSolde.setText(String.valueOf(compteToEdit.getSolde()));
            spinnerType.setSelection(compteToEdit.getType().ordinal());
        }

        new AlertDialog.Builder(this)
                .setTitle(compteToEdit == null ? "Add New Account" : "Edit Account")
                .setView(dialogView)
                .setPositiveButton(compteToEdit == null ? "Add" : "Update", (dialog, which) -> {
                    try {
                        double solde = Double.parseDouble(etSolde.getText().toString());
                        TypeCompte type = (TypeCompte) spinnerType.getSelectedItem();
                        String selectedFormat = formatSpinner.getSelectedItem().toString();
                        RetrofitInstance.DataFormat dataFormat = getDataFormat(selectedFormat);

                        if (compteToEdit == null) {
                            Compte compte = new Compte(solde, LocalDateTime.now().toString(), type);
                            viewModel.createCompte(compte, dataFormat);
                        } else {
                            compteToEdit.setSolde(solde);
                            compteToEdit.setType(type);
                            viewModel.updateCompte(compteToEdit.getId(), compteToEdit, dataFormat);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onEdit(Compte compte) {
        showAddAccountDialog(compte);
    }

    @Override
    public void onDelete(Compte compte) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String selectedFormat = formatSpinner.getSelectedItem().toString();
                    RetrofitInstance.DataFormat dataFormat = getDataFormat(selectedFormat);
                    viewModel.deleteCompte(compte.getId(), dataFormat);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}