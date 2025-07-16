package com.example.kicklog.network;

import com.example.kicklog.model.GeminiRequest;
import com.example.kicklog.model.GeminiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApiService {
    // ★★★ ここを 'models/gemini-1.5-flash-latest' に変更します ★★★
    // または 'models/gemini-1.5-pro-latest' も選択肢です。
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent") // これに修正
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey,
            @Body GeminiRequest request
    );
}