package com.example.banqueapp.api;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitInstance {
    private static final String BASE_URL = "http://10.0.2.2:8082/";

    private static Retrofit retrofitJson = null;
    private static Retrofit retrofitXml = null;
    private static CompteApiService apiJson = null;
    private static CompteApiService apiXml = null;

    public enum DataFormat {
        JSON,
        XML
    }

    public static CompteApiService getApi(DataFormat format) {
        if (format == DataFormat.JSON) {
            if (apiJson == null) {
                if (retrofitJson == null) {
                    retrofitJson = createRetrofitInstance(DataFormat.JSON);
                }
                apiJson = retrofitJson.create(CompteApiService.class);
            }
            return apiJson;
        } else {
            if (apiXml == null) {
                if (retrofitXml == null) {
                    retrofitXml = createRetrofitInstance(DataFormat.XML);
                }
                apiXml = retrofitXml.create(CompteApiService.class);
            }
            return apiXml;
        }
    }

    private static Retrofit createRetrofitInstance(DataFormat format) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient.build());

        if (format == DataFormat.JSON) {
            return builder.addConverterFactory(GsonConverterFactory.create()).build();
        } else {
            // Create a SimpleXml converter with proper configuration
            SimpleXmlConverterFactory xmlConverterFactory = SimpleXmlConverterFactory.createNonStrict(
                    new Persister(new AnnotationStrategy())
            );
            return builder.addConverterFactory(xmlConverterFactory).build();
        }
    }
}