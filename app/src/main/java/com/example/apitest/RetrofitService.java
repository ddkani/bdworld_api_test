package com.example.apitest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    // 파라미터 이름 대소문자는 서버 계약: KEY / Type / pIndex / pSize / SIGUN_NM
    @GET("/TfcacdarM")
    Call<PageListModel> getList(@Query("KEY") String key,
                                @Query("Type") String type,
                                @Query("pIndex") int pIndex,
                                @Query("pSize") int pSize,
                                @Query("SIGUN_NM") String sigunNm);
}
