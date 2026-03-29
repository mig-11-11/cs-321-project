package com.scanlinearcade.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MenuPanel extends JPanel
{
    private static final String BACKGROUND_PATH =
        "/com/scanlinearcade/assets/arcade_menu_bg.png";

    private static final String SNAKE_ICON_PATH =
        "/com/scanlinearcade/assets/Snake.png";

    private static final String BREAKOUT_ICON_PATH =
        "/com/scanlinearcade/assets/Breakout.png";

    private static final String INVADERS_ICON_PATH =
        "/com/scanlinearcade/assets/Space_Invaders.png";

    private static final String TROPHY_ICON_PATH =
        "/com/scanlinearcade/assets/Trophy.png";

    private static final String COG_ICON_PATH =
        "/com/scanlinearcade/assets/Cog.png";

    private static final int DEFAULT_SCALE = 2;

    // Base size of the cabinet image
    private static final int BASE_IMAGE_W = 600;
    private static final int BASE_IMAGE_H = 600;

    // Monitor area inside the cabinet image
    private static final int SCREEN_X = 65;
    private static final int SCREEN_Y = 55;
    private static final int SCREEN_W = 470;
    private static final int SCREEN_H = 355;

    private static final Color CYAN = new Color(0, 255, 200);
    private static final Color PANEL_BG = new Color(0, 0, 0, 70);
    private static final Color HOVER_TEXT = new Color(255, 220, 80);

    private BufferedImage backgroundImage;
    private final JPanel monitorOverlay;

    public MenuPanel(
        Runnable snakeAction,
        Runnable breakoutAction,
        Runnable invadersAction,
        Runnable scoresAction,
        Runnable settingsAction
    )
    {
        loadBackgroundImage();

        setLayout(null);
        setBackground(Color.BLACK);
        setOpaque(true);

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

        monitorOverlay = createMonitorOverlay(
            snakeAction,
            breakoutAction,
            invadersAction,
            scoresAction,
            settingsAction
        );

        add(monitorOverlay);
    }

    private void loadBackgroundImage()
    {
        backgroundImage = loadImage(
            BACKGROUND_PATH,
            "Could not load menu background image: "
        );
    }

    private BufferedImage loadImage(String resourcePath, String errorPrefix)
    {
        try
        {
            var resourceUrl = getClass().getResource(resourcePath);

            if (resourceUrl != null)
            {
                return ImageIO.read(resourceUrl);
            }

            String normalizedPath = resourcePath.startsWith("/")
                ? resourcePath.substring(1)
                : resourcePath;

            Path[] fallbackPaths = {
                Paths.get(normalizedPath),
                Paths.get("src").resolve(normalizedPath),
                Paths.get("build", "classes").resolve(normalizedPath)
            };

            for (Path candidate : fallbackPaths)
            {
                if (Files.exists(candidate))
                {
                    return ImageIO.read(candidate.toFile());
                }
            }
        }
        catch (IOException e)
        {
            // Falls through to a single consistent error message below.
        }

        System.err.println(errorPrefix + resourcePath);
        return null;
    }

    private JPanel createMonitorOverlay(
        Runnable snakeAction,
        Runnable breakoutAction,
        Runnable invadersAction,
        Runnable scoresAction,
        Runnable settingsAction
    )
    {
        JPanel panel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                // Very light dark tint for readability
                g2.setColor(PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                // Subtle scanlines
                g2.setColor(new Color(255, 255, 255, 10));
                for (int y = 0; y < getHeight(); y += 4)
                {
                    g2.drawLine(0, y, getWidth(), y);
                }

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 18, 18, 18));

        JPanel headerPanel = createHeaderPanel();

        JPanel gamesPanel = createGamesPanel(
            snakeAction,
            breakoutAction,
            invadersAction
        );

        JPanel utilityPanel = createUtilityPanel(
            scoresAction,
            settingsAction
        );

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);

        contentPanel.add(gamesPanel, BorderLayout.NORTH);
        contentPanel.add(spacerPanel, BorderLayout.CENTER);
        contentPanel.add(utilityPanel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHeaderPanel()
    {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("SCANLINE ARCADE");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(CYAN);
        title.setFont(new Font("Monospaced", Font.BOLD, 26));



        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 7)));

        return header;
    }

    private JPanel createGamesPanel(
        Runnable snakeAction,
        Runnable breakoutAction,
        Runnable invadersAction
    )
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        panel.setOpaque(false);

        panel.add(createIconEntry("Snake", SNAKE_ICON_PATH, snakeAction));
        panel.add(createIconEntry("Breakout", BREAKOUT_ICON_PATH, breakoutAction));
        panel.add(createIconEntry("Space Invaders", INVADERS_ICON_PATH, invadersAction));

        return panel;
    }

    private JPanel createUtilityPanel(
        Runnable scoresAction,
        Runnable settingsAction
    )
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 40, 10));
        panel.setOpaque(false);

        panel.add(createIconEntry("High Scores", TROPHY_ICON_PATH, scoresAction));
        panel.add(createIconEntry("Settings", COG_ICON_PATH, settingsAction));

        return panel;
    }

    private JPanel createIconEntry(String labelText, String imagePath, Runnable action)
    {
        JPanel entryPanel = new JPanel();
        entryPanel.setOpaque(false);
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
        entryPanel.setPreferredSize(new Dimension(110, 122));
        entryPanel.setMinimumSize(new Dimension(110, 122));
        entryPanel.setMaximumSize(new Dimension(110, 122));

        JButton iconButton = new JButton();
        iconButton.setPreferredSize(new Dimension(96, 96));
        iconButton.setMinimumSize(new Dimension(96, 96));
        iconButton.setMaximumSize(new Dimension(96, 96));
        iconButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        iconButton.setIcon(loadScaledIcon(imagePath, 96, 96));
        iconButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        iconButton.setFocusPainted(false);
        iconButton.setBorderPainted(false);
        iconButton.setContentAreaFilled(false);
        iconButton.setOpaque(false);

        JLabel hoverLabel = new JLabel(" ", SwingConstants.CENTER);
        hoverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hoverLabel.setForeground(HOVER_TEXT);
        hoverLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        hoverLabel.setPreferredSize(new Dimension(110, 16));
        hoverLabel.setMinimumSize(new Dimension(110, 16));
        hoverLabel.setMaximumSize(new Dimension(110, 16));

        iconButton.addActionListener(e -> action.run());

        iconButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                hoverLabel.setText(labelText);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                hoverLabel.setText(" ");
            }
        });

        entryPanel.add(iconButton);
        entryPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        entryPanel.add(hoverLabel);

        return entryPanel;
    }

    private ImageIcon loadScaledIcon(String path, int width, int height)
    {
        BufferedImage source = loadImage(path, "Could not load icon: ");

        if (source == null)
        {
            return null;
        }

        BufferedImage scaled = new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        g2.drawImage(source, 0, 0, width, height, null);
        g2.dispose();

        return new ImageIcon(scaled);
    }

    @Override
    public void doLayout()
    {
        super.doLayout();

        Rectangle imageBounds = getScaledImageBounds();

        int overlayX = imageBounds.x + (imageBounds.width * SCREEN_X) / BASE_IMAGE_W;
        int overlayY = imageBounds.y + (imageBounds.height * SCREEN_Y) / BASE_IMAGE_H;
        int overlayW = (imageBounds.width * SCREEN_W) / BASE_IMAGE_W;
        int overlayH = (imageBounds.height * SCREEN_H) / BASE_IMAGE_H;

        monitorOverlay.setBounds(overlayX, overlayY, overlayW, overlayH);
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