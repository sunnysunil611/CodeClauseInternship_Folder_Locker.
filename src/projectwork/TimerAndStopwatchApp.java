package projectwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerAndStopwatchApp extends JFrame {
    private JLabel timerLabel;
    private JLabel stopwatchLabel;
    private Timer timer;
    private long startTime;
    private long stopwatchStartTime;
    private boolean running;
    private boolean stopwatchRunning;
    private long setDuration;

    public TimerAndStopwatchApp() {
        setTitle("Timer and Stopwatch App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(216, 247, 197)); // Set the light green background color

        // Use GridBagLayout to center the components
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        timerLabel = new JLabel("00:00:00");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(timerLabel, constraints);

        stopwatchLabel = new JLabel("00:00:00");
        stopwatchLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        constraints.gridx = 1;
        constraints.gridy = 0;
        add(stopwatchLabel, constraints);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    startTime = System.currentTimeMillis();
                    timer.start();
                    running = true;
                    startButton.setBackground(Color.RED); // Set the Start button color to red
                    startButton.setText("Stop");
                } else {
                    timer.stop();
                    running = false;
                    startButton.setBackground(Color.GREEN); // Set the Start button color to green
                    startButton.setText("Start");
                }
            }
        });
        startButton.setBackground(Color.GREEN); // Set the Start button color to green
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2; // Set the button to span both columns
        add(startButton, constraints);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                running = false;
                timerLabel.setText("00:00:00");
                stopwatchLabel.setText("00:00:00");
                stopwatchStartTime = 0;
                stopwatchRunning = false;
                startButton.setBackground(Color.GREEN); // Set the Start button color to green
                startButton.setText("Start");
            }
        });
        resetButton.setBackground(Color.BLUE); // Set the Reset button color to blue
        constraints.gridy = 2;
        constraints.gridwidth = 1; // Reset gridwidth to default value
        add(resetButton, constraints);

        JLabel setTimerLabel = new JLabel("Set Timer (in seconds):");
        constraints.gridx = 0;
        constraints.gridy = 3;
        add(setTimerLabel, constraints);

        JTextField timerField = new JTextField(10);
        constraints.gridx = 1;
        add(timerField, constraints);

        JButton setTimerButton = new JButton("Set Timer");
        setTimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    try {
                        long seconds = Long.parseLong(timerField.getText());
                        setDuration = seconds * 1000;
                        timerLabel.setText(String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
                    }
                }
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        add(setTimerButton, constraints);

        // Timer with 1-second delay
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();
                if (running) {
                    updateTimerLabel(currentTime - startTime, timerLabel);
                    if (currentTime - startTime >= setDuration) {
                        timer.stop();
                        running = false;
                        startButton.setBackground(Color.GREEN); // Set the Start button color to green
                        startButton.setText("Start");
                        JOptionPane.showMessageDialog(null, "Timer finished!");
                    }
                }
            }
        });

        // Timer with 1-second delay for the stopwatch
        Timer stopwatchTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();
                if (stopwatchRunning) {
                    updateTimerLabel(currentTime - stopwatchStartTime, stopwatchLabel);
                }
            }
        });

        JButton startStopwatchButton = new JButton("Start Stopwatch");
        startStopwatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!stopwatchRunning) {
                    stopwatchStartTime = System.currentTimeMillis();
                    stopwatchTimer.start();
                    stopwatchRunning = true;
                    startStopwatchButton.setBackground(Color.RED); // Set the Start Stopwatch button color to red
                    startStopwatchButton.setText("Stop Stopwatch");
                } else {
                    stopwatchTimer.stop();
                    stopwatchRunning = false;
                    startStopwatchButton.setBackground(Color.GREEN); // Set the Start Stopwatch button color to green
                    startStopwatchButton.setText("Start Stopwatch");
                }
            }
        });
        startStopwatchButton.setBackground(Color.GREEN); // Set the Start Stopwatch button color to green
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        add(startStopwatchButton, constraints);

        pack();
        setLocationRelativeTo(null); // Center the window
    }

    private void updateTimerLabel(long elapsedTime, JLabel label) {
        long hours = (elapsedTime / (1000 * 60 * 60)) % 24;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long seconds = (elapsedTime / 1000) % 60;
        label.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TimerAndStopwatchApp().setVisible(true);
            }
        });
    }
}
