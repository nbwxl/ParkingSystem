import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingDAO {
    // 添加车位
    public boolean addParkingSpot(ParkingSpot spot) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO parking_spots(spot_id, area, floor, status) VALUES(?,?,?,?)";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, spot.getSpotId());
            pstmt.setString(2, spot.getArea());
            pstmt.setInt(3, spot.getFloor());
            pstmt.setString(4, spot.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    // 查询所有车位
    public List<ParkingSpot> getAllSpots() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spots";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                ParkingSpot spot = new ParkingSpot(
                        rs.getString("spot_id"),
                        rs.getString("area"),
                        rs.getInt("floor")
                );
                spot.setStatus(rs.getString("status"));
                spot.setCarNumber(rs.getString("car_number"));
                spot.setEntryTime(rs.getString("entry_time"));
                spots.add(spot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        return spots;
    }

    // 更新车位状态
    public boolean updateSpotStatus(String spotId, String status, String carNumber, String entryTime) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE parking_spots SET status=?, car_number=?, entry_time=? WHERE spot_id=?";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2, carNumber);
            pstmt.setString(3, entryTime);
            pstmt.setString(4, spotId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    // 添加车辆记录
    public boolean addVehicle(Vehicle vehicle) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO vehicles(car_number, type, entry_time, spot_id) VALUES(?,?,?,?)";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vehicle.getCarNumber());
            pstmt.setString(2, vehicle.getType());
            pstmt.setString(3, vehicle.getEntryTime());
            pstmt.setString(4, vehicle.getSpotId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    // 查询车辆信息
    public Vehicle getVehicle(String carNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM vehicles WHERE car_number=?";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, carNumber);
            rs = pstmt.executeQuery();

            if(rs.next()) {
                return new Vehicle(
                        rs.getString("car_number"),
                        rs.getString("type"),
                        rs.getString("entry_time"),
                        rs.getString("spot_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        return null;
    }



    // 更新车位信息
    public boolean updateParkingSpot(ParkingSpot spot) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "UPDATE parking_spots SET area=?, floor=? WHERE spot_id=?";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, spot.getArea());
            pstmt.setInt(2, spot.getFloor());
            pstmt.setString(3, spot.getSpotId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    // 删除车位
    public boolean deleteParkingSpot(String spotId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM parking_spots WHERE spot_id=?";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, spotId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    public List<ParkingSpot> getFilteredSpots(String area, String status) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ParkingSpot> spots = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM parking_spots WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (!"全部区域".equals(area)) {
            sql.append(" AND area = ?");
            params.add(area);
        }

        if (!"全部状态".equals(status)) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        sql.append(" ORDER BY spot_id");

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            while(rs.next()) {
                ParkingSpot spot = new ParkingSpot(
                        rs.getString("spot_id"),
                        rs.getString("area"),
                        rs.getInt("floor")
                );
                spot.setStatus(rs.getString("status"));
                spot.setCarNumber(rs.getString("car_number"));
                spot.setEntryTime(rs.getString("entry_time"));
                spots.add(spot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        return spots;
    }

    // 删除车辆记录
    public boolean removeVehicle(String carNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "DELETE FROM vehicles WHERE car_number=?";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, carNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }




}