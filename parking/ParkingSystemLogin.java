import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ParkingSystemLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton resetButton;

    public ParkingSystemLogin() {
        // 设置窗口基本属性
        setTitle("停车场管理系统 - 登录");
        setSize(600, 700);  // 增加窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 创建主面板（保持不变）
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(66, 133, 244),
                        0, getHeight(), new Color(219, 238, 244));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // 调整图标位置和大小
        ImageIcon icon = new ImageIcon("parking_icon.png");
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBounds(250, 50, 100, 100);  // 调整位置
        mainPanel.add(iconLabel);

        // 调整标题位置和大小
        JLabel titleLabel = new JLabel("停车场管理系统", JLabel.CENTER);
        titleLabel.setBounds(0, 180, 600, 40);  // 调整位置和宽度
        titleLabel.setFont(new Font("微软雅黑 ", Font.BOLD, 30));  // 增大字体
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel);

        // 调整登录面板大小和位置
        JPanel loginPanel = new JPanel();
        loginPanel.setBounds(100, 250, 400, 300);  // 调整大小和位置
        loginPanel.setLayout(null);
        loginPanel.setBackground(new Color(255, 255, 255, 200));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 调整用户名标签和输入框
        JLabel userLabel = new JLabel("用户名：");
        userLabel.setBounds(70, 70, 80, 30);  // 调整位置和大小
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));  // 增大字体
        loginPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 70, 200, 30);  // 调整位置和大小
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));  // 增大字体
        loginPanel.add(usernameField);

        // 调整密码标签和输入框
        JLabel passLabel = new JLabel("密  码：");
        passLabel.setBounds(70, 130, 80, 30);  // 调整位置和大小
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));  // 增大字体
        loginPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 130, 200, 30);  // 调整位置和大小
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));  // 增大字体
        loginPanel.add(passwordField);

        // 调整按钮大小和位置
        loginButton = new JButton("登录");
        loginButton.setBounds(70, 200, 120, 40);  // 调整位置和大小
        loginButton.setBackground(new Color(66, 133, 244));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));  // 增大字体
        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton);

        resetButton = new JButton("重置");
        resetButton.setBounds(230, 200, 120, 40);  // 调整位置和大小
        resetButton.setBackground(new Color(158, 158, 158));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBorderPainted(false);
        resetButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));  // 增大字体
        resetButton.addActionListener(e -> handleReset());
        loginPanel.add(resetButton);

        // 调整版权信息位置
        JLabel copyrightLabel = new JLabel("©反客为组 版权所有", JLabel.CENTER);
        copyrightLabel.setBounds(0, 620, 600, 20);  // 调整位置和宽度
        copyrightLabel.setForeground(new Color(158, 158, 158));
        mainPanel.add(copyrightLabel);

        mainPanel.add(loginPanel);
        add(mainPanel);

        // 添加按键监听（保持不变）
        addKeyListener();
    }


    private void addKeyListener() {
        // 为密码框添加回车键监听
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("用户名和密码不能为空！");
            return;
        }

        if (username.equals("admin") && password.equals("123456")) {
            // 关闭登录窗口
            this.dispose();

            // 创建并显示主界面
            SwingUtilities.invokeLater(() -> {
                ParkingSystemMain mainFrame = new ParkingSystemMain();
                mainFrame.setVisible(true);
            });
        } else {
            showMessage("用户名或密码错误！");
        }
    }

    private void handleReset() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        try {
            // 设置观感
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // 设置按钮和组件的默认字体
            UIManager.put("Button.font", new Font("微软雅黑", Font.PLAIN, 12));
            UIManager.put("Label.font", new Font("微软雅黑", Font.PLAIN, 12));
            UIManager.put("TextField.font", new Font("微软雅黑", Font.PLAIN, 12));
            UIManager.put("PasswordField.font", new Font("微软雅黑", Font.PLAIN, 12));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ParkingSystemLogin login = new ParkingSystemLogin();
            login.setVisible(true);
        });
    }
}