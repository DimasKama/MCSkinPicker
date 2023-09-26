package com.dimaskama.mcskinpicker.renderer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class IsometricRenderer extends Renderer {

    private static final double SKEW_A = 26d / 45d;    // 0.57777777
    private static final double SKEW_B = SKEW_A * 2d;  // 1.15555555

    @Override
    public BufferedImage renderHead(final BufferedImage src, final int widthAndHeight, final boolean overlay) {
        final double scale = (widthAndHeight / 8d) / 2.5;
        final BufferedImage output = new BufferedImage(widthAndHeight, widthAndHeight, BufferedImage.TYPE_INT_ARGB);

        // Get all the required head parts
        final BufferedImage headTop = this.resize(this.getPart(src, 8, 0, 8, 8, 1), scale);
        final BufferedImage headFront = this.resize(this.getPart(src, 8, 8, 8, 8, 1), scale);
        final BufferedImage headRight = this.resize(this.getPart(src, 0, 8, 8, 8, 1), scale);

        if (overlay) {

            // Top overlay
            Graphics2D g = headTop.createGraphics();
            g.drawImage(this.getPart(src, 40, 0, 8, 8, scale), 0, 0, null);
            g.dispose();

            // Front overlay
            g = headFront.createGraphics();
            g.drawImage(this.getPart(src, 40, 8, 8, 8, scale), 0, 0, null);
            g.dispose();

            // Right side overlay
            g = headRight.createGraphics();
            g.drawImage(this.getPart(src, 32, 8, 8, 8, scale), 0, 0, null);
            g.dispose();
        }

        double x;
        double y;
        double z;

        final double z_offset = scale * 3.5d;
        final double x_offset = scale * 2d;
        final Graphics2D outGraphics = output.createGraphics();

        // head top
        x = x_offset;
        y = -0.5;
        z = z_offset;
        outGraphics.setTransform(new AffineTransform(1d, -SKEW_A, 1, SKEW_A, 0, 0));
        outGraphics.drawImage(headTop, (int) (y - z), (int) (x + z), headTop.getWidth(), headTop.getHeight() + 1, null);

        // head front
        x = x_offset + 8 * scale;
        y = 0;
        z = z_offset - 0.5;
        outGraphics.setTransform(new AffineTransform(1d, -SKEW_A, 0d, SKEW_B, 0d, SKEW_A));
        outGraphics.drawImage(headFront, (int) (y + x), (int) (x + z), headFront.getWidth(), headFront.getHeight(), null);

        // head right
        x = x_offset;
        y = 0;
        z = z_offset;
        outGraphics.setTransform(new AffineTransform(1d, SKEW_A, 0d, SKEW_B, 0d, 0d));
        outGraphics.drawImage(headRight, (int) (x + y + 1), (int) (z - y - 0.5), (int) (headRight.getWidth()), headRight.getHeight() + 1, null);

        outGraphics.dispose();
        return output;
    }

}
