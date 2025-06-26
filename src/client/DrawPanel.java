

package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DrawPanel extends JPanel {
    private int lastX, lastY;
    private PrintWriter out;
    private Color currentColor = Color.BLACK;
    private boolean eraserMode = false;
    private int eraserSize = 20;

    // Store drawn lines
    private final List<DrawAction> drawActions = new ArrayList<>();

    public DrawPanel(PrintWriter out) {
        this.out = out;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Create a color palette (Left Side)
        JPanel colorPalette = new JPanel();
        colorPalette.setLayout(new GridLayout(11, 1));
        colorPalette.setPreferredSize(new Dimension(50, 0));

        // Define colors
        Color[] colors = {
                Color.BLACK, Color.RED, Color.BLUE, Color.GREEN,
                Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK,
                Color.YELLOW, Color.GRAY
        };

        // Add color buttons
        for (Color color : colors) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setOpaque(true);
            colorButton.setPreferredSize(new Dimension(40, 40));
            colorButton.addActionListener(e -> {
                currentColor = color;
                eraserMode = false;
            });

            colorPalette.add(colorButton);
        }

        // Eraser button
        JButton eraserButton = new JButton("E");
        eraserButton.addActionListener(e -> {
            currentColor = Color.WHITE;
            eraserMode = true;
        });

        colorPalette.add(eraserButton);
        add(colorPalette, BorderLayout.WEST);

        // Mouse events for drawing
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // Store the action
                drawActions.add(new DrawAction(lastX, lastY, x, y, currentColor, eraserMode, eraserSize));

                repaint(); // Redraw everything

                // Send data to server
                out.println("DRAW " + lastX + " " + lastY + " " + x + " " + y + " " + currentColor.getRGB());

                lastX = x;
                lastY = y;
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Redraw all stored actions
        for (DrawAction action : drawActions) {
            g.setColor(action.color);
            if (action.eraserMode) {
                g.fillOval(action.x1 - action.eraserSize / 2, action.y1 - action.eraserSize / 2, action.eraserSize, action.eraserSize);
            } else {
                g.drawLine(action.x1, action.y1, action.x2, action.y2);
            }
        }
    }

    public void processDrawMessage(String data) {
        String[] parts = data.split(" ");
        int x1 = Integer.parseInt(parts[0]);
        int y1 = Integer.parseInt(parts[1]);
        int x2 = Integer.parseInt(parts[2]);
        int y2 = Integer.parseInt(parts[3]);
        Color color = new Color(Integer.parseInt(parts[4]));

        // Store the received draw action
        drawActions.add(new DrawAction(x1, y1, x2, y2, color, false, 0));

        repaint(); // Refresh UI
    }

    // Inner class to store drawing actions
    private static class DrawAction {
        int x1, y1, x2, y2;
        Color color;
        boolean eraserMode;
        int eraserSize;

        public DrawAction(int x1, int y1, int x2, int y2, Color color, boolean eraserMode, int eraserSize) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
            this.eraserMode = eraserMode;
            this.eraserSize = eraserSize;
        }
    }
}

