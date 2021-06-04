package com.landleaf.normal.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.landleaf.normal.bean.DaoMaster;
import com.landleaf.normal.bean.DaoSession;
import com.landleaf.normal.bean.Room;
import com.landleaf.normal.bean.RoomDao;

import java.util.List;

public class DBUtil {

    private DaoSession mDaoSession;

    private DBUtil(){}

    public static DBUtil getInstance(){
        return ViewHolder.instance;
    }

    public void initGreenDao(Context context,String dbName){
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, dbName);
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    private DaoSession getSession() {
        DaoSession session = mDaoSession;
        session.clear();
        return session;
    }

    public boolean insertRoom(String deviceName) {
        RoomDao roomDao = getSession().getRoomDao();
        long count = roomDao.count();
        if (roomDao.queryBuilder().where(RoomDao.Properties.RoomName.eq(deviceName)).unique() == null) {
            roomDao.insert(new Room((int) (count + 1), deviceName, 1));
            return true;
        }
        return false;
    }

    public void updateRoom(String deviceName,String updateDeviceName){
        RoomDao roomDao = getSession().getRoomDao();
        Room room = roomDao.queryBuilder().where(RoomDao.Properties.RoomName.eq(deviceName)).unique();
        room.setRoomName(updateDeviceName);
        roomDao.update(room);
    }

    public void deleteRoomByName(String deviceName) {
        RoomDao roomDao = getSession().getRoomDao();
        Room unique = roomDao.queryBuilder().where(RoomDao.Properties.RoomName.eq(deviceName)).unique();
        roomDao.delete(unique);
    }

    public void clearAllRoom() {
        getSession().getRoomDao().deleteAll();
    }

    public List<Room> getAllRoom() {
        return getSession().getRoomDao().loadAll();
    }

    static class ViewHolder{
        static DBUtil instance = new DBUtil();
    }
}
