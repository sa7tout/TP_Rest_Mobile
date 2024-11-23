package com.example.banqueapp.api;

import com.example.banqueapp.model.Compte;
import com.example.banqueapp.model.CompteList;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface CompteApiService {
    @GET("banque/comptes")
    @Headers({
            "Accept: application/json"
    })
    Call<List<Compte>> getAllComptesJSON();

    @GET("banque/comptes")
    @Headers({
            "Accept: application/xml"
    })
    Call<CompteList> getAllComptesXML();

    @POST("banque/comptes")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Call<Compte> createCompteJSON(@Body Compte compte);

    @POST("banque/comptes")
    @Headers({
            "Accept: application/xml",
            "Content-Type: application/xml"
    })
    Call<Compte> createCompteXML(@Body Compte compte);

    @PUT("banque/comptes/{id}")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Call<Compte> updateCompteJSON(@Path("id") Long id, @Body Compte compte);

    @PUT("banque/comptes/{id}")
    @Headers({
            "Accept: application/xml",
            "Content-Type: application/xml"
    })
    Call<Compte> updateCompteXML(@Path("id") Long id, @Body Compte compte);

    @DELETE("banque/comptes/{id}")
    @Headers({
            "Accept: application/json"
    })
    Call<Void> deleteCompteJSON(@Path("id") Long id);

    @DELETE("banque/comptes/{id}")
    @Headers({
            "Accept: application/xml"
    })
    Call<Void> deleteCompteXML(@Path("id") Long id);
}