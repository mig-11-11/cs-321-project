package com.scanlinearcade.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class MenuPanel extends JPanel
{
    private static final String IMAGE_PATH =
        "/com/scanlinearcade/assets/arcade_menu_bg.png";

    // Makes the whole cabinet art larger on startup
    private static final int DEFAULT_SCALE = 2;

    // These are based on the original 600 x 600 image
    // They define where the cabinet monitor area is
    private static final int BASE_IMAGE_W = 600;
    private static final int BASE_IMAGE_H = 600;

    private static final int SCREEN_X = 65;
    private static final int SCREEN_Y = 55;
    private static final int SCREEN_W = 470;
    private static final int SCREEN_H = 355;

    private static final Color CYAN = new Color(0, 255, 200);
    private static final Color MAGENTA = new Color(255, 60, 180);
    private static final Color YELLOW = new Color(255, 220, 80);
    private static final Color PANEL_BG = new Color(0, 0, 0, 150);

    private BufferedImage backgroundImage;
    private final JPanel screenOverlay;

    public MenuPanel(Runnable snakeAction, Runnable breakoutAction, Runnable invadersAction)
    {
        loadBackgroundImage();

        setLayout(null);
        setBackground(Color.BLACK);
        setOpaque(true);

        // Make the whole panel larger based on the image size
        if (backgroundImage != null)
        {
            int scaledWidth = backgroundImage.getWidth() * DEFAULT_SCALE;
            int scaledHeight = backgroundImage.getHeight() * DEFAULT_SCALE;
            setPreferredSize(new Dimension(scaledWidth, scaledHeight));
        }
        else
        {
            setPreferredSize(new Dimension(1000, 1000));
        }

        screenOverlay = createScreenOverlay(snakeAction, breakoutAction, invadersAction);
        add(screenOverlay);
    }

    private void loadBackgroundImage()
    {
        try
        {
            backgroundImage = ImageIO.read(getClass().getResource(IMAGE_PATH));
        }
        catch (IOException | IllegalArgumentException e)
        {
            System.err.println("Could not load arcade menu background image: " + IMAGE_PATH);
            backgroundImage = null;
        }
    }

    private JPanel createScreenOverlay(Runnable snakeAction, Runnable breakoutAction, Runnable invadersAction)
    {
        JPanel panel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                // Dark transparent box so text is readable on the monitor
                g2.setColor(PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                // Neon border
                g2.setColor(CYAN);

                // Subtle scanlines
                g2.setColor(new Color(255, 255, 255, 18));
                for (int y = 0; y < getHeight(); y += 4)
                {
                    g2.drawLine(0, y, getWidth(), y);
                }

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("SCANLINE ARCADE");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(CYAN);
        title.setFont(new Font("Monospaced", Font.BOLD, 30));

        JLabel subtitle = new JLabel("SELECT A GAME");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Monospaced", Font.BOLD, 16));

        JButton snakeButton = createMenuButton("SNAKE", CYAN, snakeAction);
        JButton breakoutButton = createMenuButton("BREAKOUT", MAGENTA, breakoutAction);
        JButton invadersButton = createMenuButton("SPACE INVADERS", YELLOW, invadersAction);

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 28)));
        panel.add(snakeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(breakoutButton);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(invadersButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JButton createMenuButton(String text, Color accent, Runnable action)
    {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(270, 48));
        button.setPreferredSize(new Dimension(270, 48));
        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.setFont(new Font("Monospaced", Font.BOLD, 18));
        button.setForeground(accent);
        button.setBackground(new Color(10, 10, 10));
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(accent, 3));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> action.run());

        button.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                button.setBackground(accent);
                button.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                button.setBackground(new Color(10, 10, 10));
                button.setForeground(accent);
            }
        });

        return button;
    }

    @Override
    public void doLayout()
    {
        super.doLayout();

        Rectangle imageBounds = getScaledImageBounds();

        // Position the overlay inside the monitor area of the cabinet art
        int overlayX = imageBounds.x + (imageBounds.width * SCREEN_X) / BASE_IMAGE_W;
        int overlayY = imageBounds.y + (imageBounds.height * SCREEN_Y) / BASE_IMAGE_H;
        int overlayW = (imageBounds.width * SCREEN_W) / BASE_IMAGE_W;
        int overlayH = (imageBounds.height * SCREEN_H) / BASE_IMAGE_H;

        screenOverlay.setBounds(overlayX, overlayY, overlayW, overlayH);
    }

    private Rectangle getScaledImageBounds()
    {
        if (backgroundImage == null)
        {
            return new Rectangle(0, 0, getWidth(), getHeight());
        }

        int imageWidth = backgroundImage.getWidth();
        int imageHeight = backgroundImage.getHeight();

        double scaleX = (double) getWidth() / imageWidth;
        double scaleY = (double) getHeight() / imageHeight;
        double scale = Math.min(scaleX, scaleY);

        int drawWidth = (int) Math.round(imageWidth * scale);
        int drawHeight = (int) Math.round(imageHeight * scale);

        int x = (getWidth() - drawWidth) / 2;
        int y = (getHeight() - drawHeight) / 2;

        return new Rectangle(x, y, drawWidth, drawHeight);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        if (backgroundImage != null)
        {
            Rectangle bounds = getScaledImageBounds();
            g2.drawImage(
                backgroundImage,
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height,
                this
            );
        }

        g2.dispose();
    }
}