package com.example.kicklog.util;

import android.content.Context;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class SimpleCallback<T> implements Callback<T> {
    private final Context ctx;
    public SimpleCallback(Context ctx){ this.ctx = ctx; }

    @Override public void onFailure(Call<T> c, Throwable t) {
        Toast.makeText(ctx,"通信エラー:"+t.getMessage(),Toast.LENGTH_SHORT).show();
    }
    @Override public void onResponse(Call<T> c, Response<T> r) {
        if (r.isSuccessful() && r.body()!=null) success(r.body());
        else Toast.makeText(ctx,"取得失敗:"+r.code(),Toast.LENGTH_SHORT).show();
    }
    protected abstract void success(T body);
}
