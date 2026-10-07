package view;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private Image originalImage;
    private String placeholderText = "Select a song to begin";

    public ImagePanel() {
        setBackground(new Color(38, 38, 44));
        setBorder(BorderFactory.createLineBorder(new Color(70, 70, 80), 1, true));
    }

    public void setImage(Image img) {
        this.originalImage = img;
        this.placeholderText = null;
        repaint();
    }

    public void setPlaceholder(String text) {
        this.originalImage = null;
        this.placeholderText = text;
        repaint();
    }

    public void clearImage() {
        this.originalImage = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (originalImage != null) {
            int imgWidth = originalImage.getWidth(null);
            int imgHeight = originalImage.getHeight(null);

            // ==== CROP-TO-FILL ====
            // Use Math.max instead of Math.min so the image fills the panel
            // and overflows on the shorter side. The overflow is then cropped.
            double scale = Math.max(
                (double) panelWidth / imgWidth,
                (double) panelHeight / imgHeight
            );

            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);

            // Center the scaled image (negative x/y will crop it)
            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;

            // Clip to the panel bounds so the overflow is cropped
            g2.setClip(0, 0, panelWidth, panelHeight);
            g2.drawImage(originalImage, x, y, scaledWidth, scaledHeight, null);

        } else if (placeholderText != null) {
            g2.setColor(new Color(150, 150, 160));
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(placeholderText);
            int textHeight = fm.getAscent();
            g2.drawString(
                placeholderText,
                (panelWidth - textWidth) / 2,
                (panelHeight + textHeight) / 2
            );
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        // Always request a large size; BorderLayout.CENTER will stretch it anyway.
        return new Dimension(500, 500);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(200, 200);
    }
}