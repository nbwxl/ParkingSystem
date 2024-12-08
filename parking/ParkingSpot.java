import java.io.Serializable;

public class ParkingSpot implements Serializable {
    private String spotId;        // 车位编号
    private String area;          // 区域
    private int floor;            // 层数
    private String status;        // 状态
    private String carNumber;     // 车牌号
    private String entryTime;     // 入场时间

    public ParkingSpot(String spotId, String area, int floor) {
        this.spotId = spotId;
        this.area = area;
        this.floor = floor;
        this.status = "空闲";
        this.carNumber = null;
        this.entryTime = null;
    }

    // Getters
    public String getSpotId() {
        return spotId;
    }

    public String getArea() {
        return area;
    }

    public int getFloor() {
        return floor;
    }

    public String getStatus() {
        return status;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public String getEntryTime() {
        return entryTime;
    }

    // Setters
    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    // 判断车位是否空闲
    public boolean isAvailable() {
        return "空闲".equals(status);
    }

    // 占用车位
    public void occupy(String carNumber, String entryTime) {
        this.status = "已占用";
        this.carNumber = carNumber;
        this.entryTime = entryTime;
    }

    // 释放车位
    public void release() {
        this.status = "空闲";
        this.carNumber = null;
        this.entryTime = null;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "spotId='" + spotId + '\'' +
                ", area='" + area + '\'' +
                ", floor=" + floor +
                ", status='" + status + '\'' +
                ", carNumber='" + carNumber + '\'' +
                ", entryTime='" + entryTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSpot that = (ParkingSpot) o;
        return spotId.equals(that.spotId);
    }

    @Override
    public int hashCode() {
        return spotId.hashCode();
    }
}