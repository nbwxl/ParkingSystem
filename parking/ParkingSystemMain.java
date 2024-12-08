import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.util.List;

public class ParkingSystemMain extends JFrame {
    private JPanel mainPanel;
    private JTable parkingTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    // 数据存储
    private ArrayList<ParkingSpot> parkingSpots = new ArrayList<>();
    private ArrayList<Vehicle> vehicles = new ArrayList<>();

    // 车位入场面板组件
    private JTextField carNumberField;
    private JComboBox<String> vehicleTypeCombo;
    private JComboBox<String> spotCombo;

    // 车位出场面板组件
    private JTextField searchCarField;
    private JLabel feeLabel;
    private JTextArea vehicleInfoArea;
    private ParkingDAO parkingDAO;

    public ParkingSystemMain() {
        parkingDAO = new ParkingDAO();
        initializeFrame();
        initializeComponents();
        createComponents();
        setupLayout();
        addListeners();
        loadInitialData();
    }



    private void initializeFrame() {
        setTitle("停车场管理系统");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // 初始化所有成员变量
        mainPanel = new JPanel(new BorderLayout());
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        statusLabel = new JLabel(" 系统状态: 正常运行中");
        tableModel = new DefaultTableModel(
                new String[]{"车位编号", "区域", "层数", "状态", "车牌号", "入场时间"},
                0
        );
        parkingTable = new JTable(tableModel);
        carNumberField = new JTextField(15);
        vehicleTypeCombo = new JComboBox<>(new String[]{"小型车", "中型车", "大型车"});
        spotCombo = new JComboBox<>();
    }



    private void createComponents() {
        // 设置主面板
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 创建顶部面板
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 创建左侧菜单
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // 设置内容面板
        contentPanel.setLayout(cardLayout);

        // 添加各个功能面板
        contentPanel.add(createParkingInfoPanel(), "parkingInfo");
        contentPanel.add(createVehicleEntryPanel(), "vehicleEntry");
        contentPanel.add(createVehicleExitPanel(), "vehicleExit");
        contentPanel.add(createBackupPanel(), "backup");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 设置状态栏
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel.setBackground(new Color(240, 240, 240));
        statusLabel.setOpaque(true);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }




    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(51, 122, 183));
        topPanel.setPreferredSize(new Dimension(1200, 50));

        JLabel titleLabel = new JLabel("停车场管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("当前用户: admin  ");
        userLabel.setForeground(Color.WHITE);
        JButton logoutButton = new JButton("退出");

        // 添加退出按钮的事件监听器
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "确定要退出系统吗？",
                    "退出确认",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                // 保存数据
                backupData();
                // 退出系统
                System.exit(0);
            }
        });

        userPanel.add(userLabel);
        userPanel.add(logoutButton);

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(userPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(200, 0));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(247, 247, 247));

        String[] menuItems = {
                "车位信息管理",
                "车辆入场管理",
                "车辆出场管理",
                "数据备份与恢复"
        };

        for (String item : menuItems) {
            JButton menuButton = new JButton(item);
            menuButton.setMaximumSize(new Dimension(200, 40));
            menuButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            menuButton.addActionListener(e -> handleMenuClick(item));
            menuPanel.add(menuButton);
            menuPanel.add(Box.createVerticalStrut(5));
        }

        return menuPanel;
    }

    private void handleMenuClick(String menuItem) {
        switch (menuItem) {
            case "车位信息管理":
                cardLayout.show(contentPanel, "parkingInfo");
                break;
            case "车辆入场管理":
                cardLayout.show(contentPanel, "vehicleEntry");
                break;
            case "车辆出场管理":
                cardLayout.show(contentPanel, "vehicleExit");
                break;
            case "数据备份与恢复":
                cardLayout.show(contentPanel, "backup");
                break;
        }
    }

    private void setupLayout() {
        // 设置主面板的边距
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 设置状态栏的边框和背景
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel.setBackground(new Color(240, 240, 240));
        statusLabel.setOpaque(true);

        // 设置表格的一些属性
        if (parkingTable != null) {
            parkingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            parkingTable.getTableHeader().setReorderingAllowed(false);
            parkingTable.setRowHeight(25);

            // 设置表格列宽
            parkingTable.getColumnModel().getColumn(0).setPreferredWidth(100); // 车位编号
            parkingTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // 区域
            parkingTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // 层数
            parkingTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // 状态
            parkingTable.getColumnModel().getColumn(4).setPreferredWidth(100); // 车牌号
            parkingTable.getColumnModel().getColumn(5).setPreferredWidth(150); // 入场时间
        }
    }

    private void addListeners() {
        // 添加窗口监听器
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 退出前保存数据
                backupData();
                System.exit(0);
            }
        });

        // 添加表格选择监听器
        if (parkingTable != null) {
            parkingTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    updateButtonStates();
                }
            });
        }

    }

    private void updateButtonStates() {
        // 根据表格选择状态更新按钮状态
        int selectedRow = parkingTable.getSelectedRow();
        boolean hasSelection = selectedRow != -1;

    }




    private JPanel createParkingInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addButton = new JButton("添加车位");
        JButton editButton = new JButton("修改车位");
        JButton deleteButton = new JButton("删除车位");
        JButton refreshButton = new JButton("刷新");
        JComboBox<String> areaFilter = new JComboBox<>(new String[]{"全部区域", "A区", "B区", "C区"});
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"全部状态", "空闲", "已占用"});

        // 添加按钮事件
        addButton.addActionListener(e -> showAddParkingSpotDialog());
        editButton.addActionListener(e -> showEditParkingSpotDialog());
        deleteButton.addActionListener(e -> deleteParkingSpot());
        // 添加筛选监听器
        areaFilter.addActionListener(e -> filterParkingSpots(
                (String)areaFilter.getSelectedItem(),
                (String)statusFilter.getSelectedItem()
        ));

        statusFilter.addActionListener(e -> filterParkingSpots(
                (String)areaFilter.getSelectedItem(),
                (String)statusFilter.getSelectedItem()
        ));

        // 添加刷新按钮的事件监听器
        refreshButton.addActionListener(e -> loadInitialData());

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.add(refreshButton);
        toolBar.addSeparator();
        toolBar.add(new JLabel("区域: "));
        toolBar.add(areaFilter);
        toolBar.add(new JLabel("状态: "));
        toolBar.add(statusFilter);

        // 创建表格
        String[] columns = {"车位编号", "区域", "层数", "状态", "车牌号", "入场时间"};
        tableModel = new DefaultTableModel(columns, 0);
        parkingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(parkingTable);

        panel.add(toolBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // 添加筛选方法
    private void filterParkingSpots(String area, String status) {
        ParkingDAO parkingDAO = new ParkingDAO();
        List<ParkingSpot> allSpots = parkingDAO.getAllSpots();

        // 清空表格
        tableModel.setRowCount(0);

        // 筛选并添加数据
        for (ParkingSpot spot : allSpots) {
            boolean areaMatch = "全部区域".equals(area) || area.equals(spot.getArea());
            boolean statusMatch = "全部状态".equals(status) || status.equals(spot.getStatus());

            if (areaMatch && statusMatch) {
                tableModel.addRow(new Object[]{
                        spot.getSpotId(),
                        spot.getArea(),
                        spot.getFloor(),
                        spot.getStatus(),
                        spot.getCarNumber(),
                        spot.getEntryTime()
                });
            }
        }

        // 更新状态栏
        statusLabel.setText(" 系统状态: 显示 " + tableModel.getRowCount() + " 个车位信息");
    }



    private JPanel createVehicleEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 车牌号输入
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("车牌号:"), gbc);

        gbc.gridx = 1;
        carNumberField = new JTextField(15);
        formPanel.add(carNumberField, gbc);

        // 车辆类型选择
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("车辆类型:"), gbc);

        gbc.gridx = 1;
        vehicleTypeCombo = new JComboBox<>(new String[]{"小型车", "中型车", "大型车"});
        formPanel.add(vehicleTypeCombo, gbc);

        // 车位选择
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("选择车位:"), gbc);

        gbc.gridx = 1;
        spotCombo = new JComboBox<>();
        updateAvailableSpots();
        formPanel.add(spotCombo, gbc);

        // 确认按钮
        gbc.gridx = 1; gbc.gridy = 3;
        JButton confirmButton = new JButton("确认入场");
        confirmButton.addActionListener(e -> handleVehicleEntry());
        formPanel.add(confirmButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }
    private JPanel createVehicleExitPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchLabel = new JLabel("车牌号：");
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("查询");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 创建信息显示面板
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建车辆信息显示区域
        JTextArea infoArea = new JTextArea(10, 40);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(infoArea);

        // 创建费用和操作面板
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JLabel feeLabel = new JLabel("停车费用：¥0.00");
        feeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        JButton checkoutButton = new JButton("结算出场");
        checkoutButton.setEnabled(false);

        bottomPanel.add(feeLabel);
        bottomPanel.add(checkoutButton);

        // 添加查询按钮事件
        searchButton.addActionListener(e -> {
            String carNumber = searchField.getText().trim();
            if (carNumber.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "请输入车牌号！");
                return;
            }

            // 查询车辆信息
            ParkingDAO dao = new ParkingDAO();
            Vehicle vehicle = dao.getVehicle(carNumber);

            if (vehicle == null) {
                JOptionPane.showMessageDialog(panel, "未找到该车辆信息！");
                infoArea.setText("");
                feeLabel.setText("停车费用：¥0.00");
                checkoutButton.setEnabled(false);
                return;
            }

            // 计算停车费用
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date entryDate = sdf.parse(vehicle.getEntryTime());
                Date now = new Date();
                long parkingMinutes = (now.getTime() - entryDate.getTime()) / (1000 * 60);
                double fee = calculateParkingFee(parkingMinutes, vehicle.getType());

                // 显示车辆信息
                StringBuilder info = new StringBuilder();
                info.append("车辆信息：\n\n");
                info.append("车牌号：").append(vehicle.getCarNumber()).append("\n");
                info.append("车辆类型：").append(vehicle.getType()).append("\n");
                info.append("入场时间：").append(vehicle.getEntryTime()).append("\n");
                info.append("停车时长：").append(parkingMinutes).append(" 分钟\n");
                info.append("车位编号：").append(vehicle.getSpotId()).append("\n");

                infoArea.setText(info.toString());
                feeLabel.setText(String.format("停车费用：¥%.2f", fee));
                checkoutButton.setEnabled(true);

                // 保存费用信息
                vehicle.setFee(fee);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "计算费用时出错：" + ex.getMessage());
            }
        });

        // 添加结算按钮事件
        checkoutButton.addActionListener(e -> {
            String carNumber = searchField.getText().trim();
            ParkingDAO dao = new ParkingDAO();
            Vehicle vehicle = dao.getVehicle(carNumber);

            if (vehicle != null) {
                int confirm = JOptionPane.showConfirmDialog(panel,
                        String.format("确认收费 ¥%.2f 并办理出场？", vehicle.getFee()),
                        "确认出场",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 更新车位状态
                    boolean success = dao.updateSpotStatus(vehicle.getSpotId(), "空闲", null, null) &&
                            dao.removeVehicle(carNumber);

                    if (success) {
                        JOptionPane.showMessageDialog(panel, "出场成功！");
                        // 清空显示
                        searchField.setText("");
                        infoArea.setText("");
                        feeLabel.setText("停车费用：¥0.00");
                        checkoutButton.setEnabled(false);
                        // 刷新车位信息
                        loadInitialData();
                    } else {
                        JOptionPane.showMessageDialog(panel, "出场处理失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        infoPanel.add(scrollPane, BorderLayout.CENTER);
        infoPanel.add(bottomPanel, BorderLayout.SOUTH);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    // 计算停车费用
    private double calculateParkingFee(long minutes, String vehicleType) {
        // 基础费率（每小时）
        double baseRate;
        switch (vehicleType) {
            case "小型车":
                baseRate = 5.0;
                break;
            case "中型车":
                baseRate = 8.0;
                break;
            case "大型车":
                baseRate = 12.0;
                break;
            default:
                baseRate = 5.0;
        }

        // 计算费用（按小时计费，不足一小时按一小时计算）
        double hours = Math.ceil(minutes / 60.0);
        return baseRate * hours;
    }




    private JPanel createBackupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 备份按钮
        gbc.gridx = 0; gbc.gridy = 0;
        JButton backupButton = new JButton("备份数据");
        backupButton.addActionListener(e -> backupData());
        panel.add(backupButton, gbc);

        // 恢复按钮
        gbc.gridx = 1;
        JButton restoreButton = new JButton("恢复数据");
        restoreButton.addActionListener(e -> restoreData());
        panel.add(restoreButton, gbc);

        return panel;
    }


    // 添加车位对话框
    private void showAddParkingSpotDialog() {
        JDialog dialog = new JDialog(this, "添加车位", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 车位编号输入
        JTextField spotIdField = new JTextField(10);
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("车位编号："), gbc);
        gbc.gridx = 1;
        dialog.add(spotIdField, gbc);

        // 区域选择
        JComboBox<String> areaCombo = new JComboBox<>(new String[]{"A区", "B区", "C区"});
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("区域："), gbc);
        gbc.gridx = 1;
        dialog.add(areaCombo, gbc);

        // 层数输入
        JSpinner floorSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("层数："), gbc);
        gbc.gridx = 1;
        dialog.add(floorSpinner, gbc);

        // 确认按钮
        JButton confirmButton = new JButton("确认");
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(confirmButton, gbc);

        confirmButton.addActionListener(e -> {
            String spotId = spotIdField.getText().trim();
            String area = (String) areaCombo.getSelectedItem();
            int floor = (Integer) floorSpinner.getValue();

            if (spotId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入车位编号！");
                return;
            }

            ParkingSpot spot = new ParkingSpot(spotId, area, floor);
            ParkingDAO dao = new ParkingDAO();

            if (dao.addParkingSpot(spot)) {
                JOptionPane.showMessageDialog(dialog, "添加成功！");
                loadInitialData();  // 刷新表格
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 修改车位对话框
    private void showEditParkingSpotDialog() {
        int selectedRow = parkingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要修改的车位！");
            return;
        }

        String spotId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentArea = (String) tableModel.getValueAt(selectedRow, 1);
        int currentFloor = (Integer) tableModel.getValueAt(selectedRow, 2);

        JDialog dialog = new JDialog(this, "修改车位", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 显示车位编号（不可修改）
        JLabel spotIdLabel = new JLabel(spotId);
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("车位编号："), gbc);
        gbc.gridx = 1;
        dialog.add(spotIdLabel, gbc);

        // 区域选择
        JComboBox<String> areaCombo = new JComboBox<>(new String[]{"A区", "B区", "C区"});
        areaCombo.setSelectedItem(currentArea);
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("区域："), gbc);
        gbc.gridx = 1;
        dialog.add(areaCombo, gbc);

        // 层数输入
        JSpinner floorSpinner = new JSpinner(new SpinnerNumberModel(currentFloor, 1, 5, 1));
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("层数："), gbc);
        gbc.gridx = 1;
        dialog.add(floorSpinner, gbc);

        // 确认按钮
        JButton confirmButton = new JButton("确认");
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(confirmButton, gbc);

        confirmButton.addActionListener(e -> {
            String area = (String) areaCombo.getSelectedItem();
            int floor = (Integer) floorSpinner.getValue();

            ParkingSpot spot = new ParkingSpot(spotId, area, floor);
            ParkingDAO dao = new ParkingDAO();

            if (dao.updateParkingSpot(spot)) {
                JOptionPane.showMessageDialog(dialog, "修改成功！");
                loadInitialData();  // 刷新表格
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 删除车位
    private void deleteParkingSpot() {
        int selectedRow = parkingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的车位！");
            return;
        }

        String spotId = (String) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 3);

        if ("已占用".equals(status)) {
            JOptionPane.showMessageDialog(this, "该车位已被占用，无法删除！");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除车位 " + spotId + " 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ParkingDAO dao = new ParkingDAO();
            if (dao.deleteParkingSpot(spotId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadInitialData();  // 刷新表格
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }




    private void backupData() {
        // 实现数据备份逻辑
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("parking_system_backup.dat"))) {
            oos.writeObject(parkingSpots);
            oos.writeObject(vehicles);
            JOptionPane.showMessageDialog(this, "数据备份成功");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "数据备份失败: " + e.getMessage());
        }
    }

    private void restoreData() {
        // 实现数据恢复逻辑
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("parking_system_backup.dat"))) {
            parkingSpots = (ArrayList<ParkingSpot>)ois.readObject();
            vehicles = (ArrayList<Vehicle>)ois.readObject();
            updateParkingTable();
            updateAvailableSpots();
            JOptionPane.showMessageDialog(this, "数据恢复成功");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "数据恢复失败: " + e.getMessage());
        }
    }

    private void updateParkingTable() {
        tableModel.setRowCount(0);
        for (ParkingSpot spot : parkingSpots) {
            tableModel.addRow(new Object[]{
                    spot.getSpotId(),
                    spot.getArea(),
                    spot.getFloor(),
                    spot.getStatus(),
                    spot.getCarNumber(),
                    spot.getEntryTime()
            });
        }
    }



    private void updateAvailableSpots() {
        if (spotCombo == null || statusLabel == null) {
            return;  // 如果组件未初始化，直接返回
        }

        try {
            ParkingDAO dao = new ParkingDAO();
            List<ParkingSpot> spots = dao.getFilteredSpots("全部区域", "空闲");

            spotCombo.removeAllItems();
            for (ParkingSpot spot : spots) {
                spotCombo.addItem(spot.getSpotId());
            }

            // 更新状态栏
            statusLabel.setText(" 系统状态: 当前可用车位 " + spots.size() + " 个");
        } catch (Exception e) {
            e.printStackTrace();
            if (statusLabel != null) {
                statusLabel.setText(" 系统状态: 更新车位信息失败");
            }
        }
    }

    // 同时修改车辆入场处理方法
    private void handleVehicleEntry() {
        String carNumber = carNumberField.getText().trim();
        String vehicleType = (String) vehicleTypeCombo.getSelectedItem();
        String spotIdFull = (String) spotCombo.getSelectedItem();

        // 从完整显示文本中提取车位编号
        String spotId = spotIdFull != null ? spotIdFull.split(" ")[0] : null;

        if (carNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入车牌号！");
            return;
        }

        if (spotId == null) {
            JOptionPane.showMessageDialog(this, "请选择车位！");
            return;
        }

        // 记录入场时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String entryTime = sdf.format(new Date());

        // 创建车辆记录
        Vehicle vehicle = new Vehicle(carNumber, vehicleType, entryTime, spotId);

        // 更新数据库
        ParkingDAO dao = new ParkingDAO();
        boolean success = dao.addVehicle(vehicle) &&
                dao.updateSpotStatus(spotId, "已占用", carNumber, entryTime);

        if (success) {
            JOptionPane.showMessageDialog(this, "车辆入场成功！");
            // 清空输入
            carNumberField.setText("");
            vehicleTypeCombo.setSelectedIndex(0);
            // 刷新可用车位
            updateAvailableSpots();
            // 刷新车位信息表格
            loadInitialData();
        } else {
            JOptionPane.showMessageDialog(this, "车辆入场失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void loadInitialData() {
        // 清空表格现有数据
        tableModel.setRowCount(0);

        // 创建DAO对象
        ParkingDAO parkingDAO = new ParkingDAO();

        try {
            // 从数据库获取所有车位信息
            List<ParkingSpot> spots = parkingDAO.getAllSpots();

            // 将数据添加到表格中
            for (ParkingSpot spot : spots) {
                tableModel.addRow(new Object[]{
                        spot.getSpotId(),
                        spot.getArea(),
                        spot.getFloor(),
                        spot.getStatus(),
                        spot.getCarNumber(),
                        spot.getEntryTime()
                });
            }

            // 更新状态栏
            statusLabel.setText(" 系统状态: 已加载 " + spots.size() + " 个车位信息");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "加载数据失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


}

