package com.example.banqueapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.banqueapp.api.RetrofitInstance;
import com.example.banqueapp.model.Compte;
import com.example.banqueapp.model.CompteList;
import com.example.banqueapp.api.CompteApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompteViewModel extends ViewModel {
    private final MutableLiveData<List<Compte>> comptes = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<List<Compte>> getComptes() {
        return comptes;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadComptes(RetrofitInstance.DataFormat format) {
        isLoading.setValue(true);
        CompteApiService apiService = RetrofitInstance.getApi(format);

        if (format == RetrofitInstance.DataFormat.XML) {
            apiService.getAllComptesXML().enqueue(new Callback<CompteList>() {
                @Override
                public void onResponse(Call<CompteList> call, Response<CompteList> response) {
                    isLoading.setValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        List<Compte> comptesList = response.body().getComptes();
                        comptes.setValue(comptesList != null ? comptesList : new ArrayList<>());
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ?
                                    response.errorBody().string() : "Unknown error";
                            error.setValue("Error: " + response.code() + " - " + errorBody);
                        } catch (IOException e) {
                            error.setValue("Error: " + response.code());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CompteList> call, Throwable t) {
                    isLoading.setValue(false);
                    error.setValue("Network Error: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            apiService.getAllComptesJSON().enqueue(new Callback<List<Compte>>() {
                @Override
                public void onResponse(Call<List<Compte>> call, Response<List<Compte>> response) {
                    isLoading.setValue(false);
                    if (response.isSuccessful()) {
                        comptes.setValue(response.body() != null ? response.body() : new ArrayList<>());
                    } else {
                        error.setValue("Error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Compte>> call, Throwable t) {
                    isLoading.setValue(false);
                    error.setValue("Network Error: " + t.getMessage());
                }
            });
        }
    }

    public void createCompte(Compte compte, RetrofitInstance.DataFormat format) {
        isLoading.setValue(true);
        CompteApiService apiService = RetrofitInstance.getApi(format);

        Call<Compte> call;
        if (format == RetrofitInstance.DataFormat.XML) {
            call = apiService.createCompteXML(compte);
        } else {
            call = apiService.createCompteJSON(compte);
        }

        call.enqueue(new Callback<Compte>() {
            @Override
            public void onResponse(Call<Compte> call, Response<Compte> response) {
                if (response.isSuccessful()) {
                    loadComptes(format);
                } else {
                    isLoading.setValue(false);
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        error.setValue("Error creating account: " + response.code() +
                                " - " + errorBody);
                    } catch (IOException e) {
                        error.setValue("Error creating account: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Compte> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Error creating account: " + t.getMessage());
            }
        });
    }

    // Similarly update updateCompte and deleteCompte methods
    public void updateCompte(Long id, Compte compte, RetrofitInstance.DataFormat format) {
        isLoading.setValue(true);
        CompteApiService apiService = RetrofitInstance.getApi(format);

        Call<Compte> call;
        if (format == RetrofitInstance.DataFormat.XML) {
            call = apiService.updateCompteXML(id, compte);
        } else {
            call = apiService.updateCompteJSON(id, compte);
        }

        call.enqueue(new Callback<Compte>() {
            @Override
            public void onResponse(Call<Compte> call, Response<Compte> response) {
                if (response.isSuccessful()) {
                    loadComptes(format);
                } else {
                    isLoading.setValue(false);
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        error.setValue("Error updating account: " + response.code() +
                                " - " + errorBody);
                    } catch (IOException e) {
                        error.setValue("Error updating account: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Compte> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Error updating account: " + t.getMessage());
            }
        });
    }

    public void deleteCompte(Long id, RetrofitInstance.DataFormat format) {
        isLoading.setValue(true);
        CompteApiService apiService = RetrofitInstance.getApi(format);

        Call<Void> call;
        if (format == RetrofitInstance.DataFormat.XML) {
            call = apiService.deleteCompteXML(id);
        } else {
            call = apiService.deleteCompteJSON(id);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    List<Compte> currentList = comptes.getValue();
                    if (currentList != null) {
                        List<Compte> updatedList = new ArrayList<>(currentList);
                        updatedList.removeIf(compte -> compte.getId().equals(id));
                        comptes.setValue(updatedList);
                    }
                    loadComptes(format);
                } else {
                    isLoading.setValue(false);
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        error.setValue("Error deleting account: " + response.code() +
                                " - " + errorBody);
                    } catch (IOException e) {
                        error.setValue("Error deleting account: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Error deleting account: " + t.getMessage());
            }
        });
    }
}