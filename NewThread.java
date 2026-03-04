import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class NewThread implements Runnable {
    public Thread t;
    public static int z = 0;
    public static StopWatch Abhey;
    
    private volatile boolean running = true;
    private volatile boolean paused = false;

    public NewThread(String str) {
        t = new Thread(this, str);
        t.start();
    }

    @Override 
    public void run() {
        int Hour = 0, Minute = 0, Second = 0, Milli = 0;
        try {
            if (z == 0) {
                z = 1;
                while (running) {
                    Thread.sleep(1000);
                    Abhey.setDate();
                }
            } else {
                while (running) {
                    if (!paused) {
                        Thread.sleep(10); 
                        Milli++;
                        if (Milli == 100) { Second++; Milli = 0; }
                        if (Second == 60) { Minute++; Second = 0; }
                        if (Minute == 60) { Hour++; Minute = 0; }
                        Abhey.setTime(Hour, Minute, Second, Milli);
                    } else {
                        Thread.sleep(100);
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Thread finalizada.");
        } catch (Exception e) {
            System.out.println("Erro: " + e);
        }
    }

    public void stop() { running = false; }
    public void Suspend() { paused = true; }
    public void Resume() { paused = false; }

    public static void main(String args[]) {
        // Look and Feel do Sistema para parecer um app nativo
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
        catch (Exception e) {}
        
        Abhey = new StopWatch();
        Abhey.setVisible(true);
    }
}

class StopWatch extends JFrame {
    static int x = 0;
    public static NewThread Gary;

    // Componentes de Texto
    private JLabel L_Time, L_Date, L_Clock;
    private JTextArea CB1;
    private JButton btnStart, btnLap, btnReset;

    public StopWatch() {
        setTitle("Java Pro StopWatch");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 550);
        setLocationRelativeTo(null); // Centraliza na tela
        
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Título
        JLabel title = new JLabel("STOPWATCH", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        mainPanel.add(title, gbc);

        // 2. Data e Hora Atual (Relógio do Topo)
        L_Date = new JLabel("Data: --/--/----", SwingConstants.LEFT);
        L_Clock = new JLabel("Hora: 00:00:00", SwingConstants.RIGHT);
        JPanel topInfo = new JPanel(new GridLayout(1, 2));
        topInfo.add(L_Date);
        topInfo.add(L_Clock);
        gbc.gridy = 1;
        mainPanel.add(topInfo, gbc);

        // 3. Display do Cronômetro (Grande)
        L_Time = new JLabel("00:00:00:00", SwingConstants.CENTER);
        L_Time.setFont(new Font("Consolas", Font.BOLD, 48));
        L_Time.setOpaque(true);
        L_Time.setBackground(Color.BLACK);
        L_Time.setForeground(new Color(0, 255, 0)); // Verde neon
        L_Time.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
        gbc.gridy = 2;
        mainPanel.add(L_Time, gbc);

        // 4. Botões
        btnStart = new JButton("Start");
        btnLap = new JButton("Lap");
        btnReset = new JButton("Reset");
        
        // Estilo dos botões
        Dimension btnSize = new Dimension(100, 40);
        btnStart.setPreferredSize(btnSize);
        btnLap.setPreferredSize(btnSize);
        btnReset.setPreferredSize(btnSize);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnStart);
        buttonPanel.add(btnLap);
        buttonPanel.add(btnReset);
        gbc.gridy = 3;
        mainPanel.add(buttonPanel, gbc);

        // 5. Área de Laps (Histórico)
        CB1 = new JTextArea(8, 20);
        CB1.setEditable(false);
        CB1.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(CB1);
        scroll.setBorder(BorderFactory.createTitledBorder("Laps / Voltas"));
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scroll, gbc);

        add(mainPanel);

        // Listeners
        btnStart.addActionListener(e -> startAction());
        btnLap.addActionListener(e -> lapAction());
        btnReset.addActionListener(e -> resetAction());
    }

    private void startAction() {
        if (NewThread.z == 0) new NewThread("Relógio");
        if (x == 0) { 
            Gary = new NewThread("Cronômetro"); 
            x = 1; btnStart.setText("Pause"); 
            btnStart.setBackground(Color.ORANGE);
        } else if (x == 1) { 
            Gary.Suspend(); 
            x = 2; btnStart.setText("Resume"); 
            btnStart.setBackground(Color.GREEN);
        } else { 
            Gary.Resume(); 
            x = 1; btnStart.setText("Pause"); 
            btnStart.setBackground(Color.ORANGE);
        }
    }

    private void lapAction() {
        if (x != 0) {
            CB1.append(" Lap " + (CB1.getLineCount()) + ":  " + L_Time.getText() + "\n");
        }
    }

    private void resetAction() {
        if (Gary != null) Gary.stop();
        x = 0;
        btnStart.setText("Start");
        btnStart.setBackground(null);
        L_Time.setText("00:00:00:00");
        CB1.setText("");
    }

    public void setTime(int h, int m, int s, int ms) {
        L_Time.setText(String.format("%02d:%02d:%02d:%02d", h, m, s, ms));
    }

    public void setDate() {
        GregorianCalendar cal = new GregorianCalendar();
        L_Date.setText(String.format("Data: %02d/%02d/%d", 
            cal.get(Calendar.DAY_OF_MONTH), (cal.get(Calendar.MONTH)+1), cal.get(Calendar.YEAR)));
        L_Clock.setText(String.format("Hora: %02d:%02d:%02d", 
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)));
    }
}
