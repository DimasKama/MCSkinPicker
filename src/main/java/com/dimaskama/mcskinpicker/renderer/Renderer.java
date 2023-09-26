package com.dimaskama.mcskinpicker.renderer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class Renderer {

    public abstract BufferedImage renderHead(BufferedImage src, int widthAndHeight, boolean overlay);

    protected BufferedImage getPart(final BufferedImage src, final int x, final int y, final int w, final int h, final double scale) {
        final BufferedImage subImage = src.getSubimage(x, y, w, h);
        return this.resize(subImage, scale);
    }

    protected BufferedImage resize(final BufferedImage src, final double scale) {
        final BufferedImage scaled = new BufferedImage((int) (src.getWidth() * scale), (int) (src.getHeight() * scale), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = scaled.createGraphics();
        graphics.drawImage(src, AffineTransform.getScaleInstance(scale, scale), null);
        graphics.dispose();
        return scaled;
    }

}
