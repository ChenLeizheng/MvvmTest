package com.landleaf.normal.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Lei on 2019/4/23.
 */
@Entity(
        nameInDb = "room"
)
public class Room {
    @Id(autoincrement = true)
    private Long id;
    private int roomId;
    private String roomName;
    private int floorId;

    public Room(int roomId, String roomName, int floorId) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.floorId = floorId;
    }

    @Generated(hash = 2083405495)
    public Room(Long id, int roomId, String roomName, int floorId) {
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.floorId = floorId;
    }

    @Generated(hash = 703125385)
    public Room() {
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", roomName='" + roomName + '\'' +
                ", floorId=" + floorId +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRoomId() {
        return this.roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getFloorId() {
        return this.floorId;
    }

    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }
}
