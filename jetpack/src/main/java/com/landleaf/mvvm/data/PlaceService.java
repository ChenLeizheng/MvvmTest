package com.landleaf.mvvm.data;

import com.landleaf.mvvm.db.City;
import com.landleaf.mvvm.db.County;
import com.landleaf.mvvm.db.Province;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @Path
 * 替换 URL 中被 {} 包裹起来的字段
 * http://guolin.tech/api/china/32/311
 * [{"id":2332,"name":"兰州","weather_id":"CN101160101"},{"id":2333,"name":"皋兰","weather_id":"CN101160102"},{"id":2334,"name":"永登","weather_id":"CN101160103"},{"id":2335,"name":"榆中","weather_id":"CN101160104"}]
 */

public interface PlaceService {
    @GET("api/china")
    Call<List<Province>> getProvinces();

    @GET("api/china/{provinceId}")
    Call<List<City>> getCities(@Path("provinceId")long provinceId);

    @GET("api/china/{provinceId}/{cityId}")
    Call<List<County>> getCounties(@Path("provinceId") long provinceId,@Path("cityId") long cityId);
}
