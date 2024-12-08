import java.io.Serializable;

public class Vehicle implements Serializable {
    private String carNumber;     // 车牌号
    private String type;          // 车辆类型
    private String entryTime;     // 入场时间
    private String spotId;        // 停车位编号
    private double fee;           // 停车费用

    public Vehicle(String carNumber, String type, String entryTime, String spotId) {
        this.carNumber = carNumber;
        this.type = type;
        this.entryTime = entryTime;
        this.spotId = spotId;
        this.fee = 0.0;
    }

    // Getters
    public String getCarNumber() {
        return carNumber;
    }

    public String getType() {
        return type;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public String getSpotId() {
        return spotId;
    }

    public double getFee() {
        return fee;
    }

    // Setters
    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "carNumber='" + carNumber + '\'' +
                ", type='" + type + '\'' +
                ", entryTime='" + entryTime + '\'' +
                ", spotId='" + spotId + '\'' +
                ", fee=" + fee +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return carNumber.equals(vehicle.carNumber);
    }

    @Override
    public int hashCode() {
        return carNumber.hashCode();
    }
}