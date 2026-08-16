package com.example.apitest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// 원본 API 문서 (경기데이터드림 "사고다발지 현황" — data.go.kr 15057393 의 실제 서비스):
// https://data.gg.go.kr/portal/data/service/selectServicePage.do?page=1&sortColumn=&sortDirection=&infId=9HJ306A05WF6RS2560PG21056899&infSeq=3
public interface RetrofitService {
    // 파라미터 이름 대소문자는 서버 계약: KEY / Type / pIndex / pSize / SIGUN_NM
    @GET("/TfcacdarM")
    Call<PageListModel> getList(@Query("KEY") String key,
                                @Query("Type") String type,
                                @Query("pIndex") int pIndex,
                                @Query("pSize") int pSize,
                                @Query("SIGUN_NM") String sigunNm);
}
