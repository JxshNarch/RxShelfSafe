package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

// A thin scroll bar with a rounded gray thumb, a light track and small arrows.
public class ScrollBarStyle extends BasicScrollBarUI {

    private Color trackColor = new Color(250, 250, 250);     // the light bar behind
    private Color thumbColor = new Color(150, 150, 150);     // the gray part you drag

    // Swing calls this by itself when the class name is registered in UIManager
    public static ComponentUI createUI(JComponent c) {
        return new ScrollBarStyle();
    }

    public Dimension getPreferredSize(JComponent c) {
        JScrollBar bar = (JScrollBar) c;
        if (bar.getOrientation() == JScrollBar.VERTICAL) {
            return new Dimension(14, 48);
        }
        return new Dimension(48, 14);
    }

    protected JButton createDecreaseButton(int direction) {
        return makeArrow(direction);
    }

    protected JButton createIncreaseButton(int direction) {
        return makeArrow(direction);
    }

    // the small arrow at each end
    private JButton makeArrow(final int direction) {
        JButton button = new JButton() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(trackColor);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(thumbColor);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                Polygon p = new Polygon();
                if (direction == SwingConstants.NORTH) {
                    p.addPoint(cx - 4, cy + 2);
                    p.addPoint(cx + 4, cy + 2);
                    p.addPoint(cx, cy - 2);
                } else if (direction == SwingConstants.SOUTH) {
                    p.addPoint(cx - 4, cy - 2);
                    p.addPoint(cx + 4, cy - 2);
                    p.addPoint(cx, cy + 2);
                } else if (direction == SwingConstants.WEST) {
                    p.addPoint(cx + 2, cy - 4);
                    p.addPoint(cx + 2, cy + 4);
                    p.addPoint(cx - 2, cy);
                } else {
                    p.addPoint(cx - 2, cy - 4);
                    p.addPoint(cx - 2, cy + 4);
                    p.addPoint(cx + 2, cy);
                }
                g2.fillPolygon(p);
                g2.dispose();
            }
        };
        button.setPreferredSize(new Dimension(14, 16));
        button.setBorder(null);
        button.setFocusable(false);
        button.setContentAreaFilled(false);
        return button;
    }

    protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
        g.setColor(trackColor);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
        if (bounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(thumbColor);
        int gap = 3;                                          // the space around the thumb (makes it thinner)
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            int w = bounds.width - gap * 2;
            g2.fillRoundRect(bounds.x + gap, bounds.y, w, bounds.height, w, w);
        } else {
            int h = bounds.height - gap * 2;
            g2.fillRoundRect(bounds.x, bounds.y + gap, bounds.width, h, h, h);
        }
        g2.dispose();
    }
}