package com.landleaf.mvvm.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.landleaf.mvvm.db.City;
import com.landleaf.mvvm.db.CityDao;
import com.landleaf.mvvm.db.County;
import com.landleaf.mvvm.db.CountyDao;
import com.landleaf.mvvm.db.DaoMaster;
import com.landleaf.mvvm.db.DaoSession;
import com.landleaf.mvvm.db.Province;
import com.landleaf.mvvm.db.ProvinceDao;

import java.io.InputStream;
import java.util.List;

public class DbUtil {

    private DaoSession daoSession;
    private DaoMaster.DevOpenHelper devOpenHelper;

    private DbUtil(){}

    public static DbUtil getInstance(){
        return ViewHolder.instance;
    }

    public void initGreenDao(Context context,String dbName){
        devOpenHelper = new DaoMaster.DevOpenHelper(context, dbName);
        SQLiteDatabase writableDatabase = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        daoSession = daoMaster.newSession();
    }

    public void saveProvince(Province province){
        ProvinceDao provinceDao = daoSession.getProvinceDao();
        provinceDao.insert(province);
    }

    public void saveCity(City city){
        CityDao cityDao = daoSession.getCityDao();
        cityDao.insert(city);
    }

    public void saveCounty(County county){
        CountyDao countyDao = daoSession.getCountyDao();
        countyDao.insert(county);
    }

    public List<Province> getAllProvince(){
        return daoSession.getProvinceDao().loadAll();
    }

    public List<City> getCityByProvinceId(Long provinceId){
        CityDao cityDao = daoSession.getCityDao();
        List<City> list = cityDao.queryBuilder().where(CityDao.Properties.ProvinceId.eq(provinceId)).list();
        return list;
    }

    public List<County> getCountyByCityId(Long cityId){
        CountyDao countyDao = daoSession.getCountyDao();
        List<County> list = countyDao.queryBuilder().where(CountyDao.Properties.CityId.eq(cityId)).list();
        return list;
    }

    private static class ViewHolder{
        static DbUtil instance = new DbUtil();
    }

    public void closeDb(){
        if (devOpenHelper != null){
            devOpenHelper.close();
            devOpenHelper = null;
        }
        if (daoSession!=null){
            daoSession.clear();
            daoSession = null;
        }
    }
}
