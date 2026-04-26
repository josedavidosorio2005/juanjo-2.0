package com.app.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AnimationUtils {

    /**
     * Adds a subtle lift effect on hover for any component.
     */
    public static void addHoverEffect(JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.PRIMARY, 2, true),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)
                ));
                component.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });
    }

    /**
     * Fades in a component by increasing its opacity.
     * Note: Requires the component to support transparency or custom painting.
     * For simplicity in Swing, we can animate the background color alpha or just use a Timer to call repaint.
     */
    public static void fadeIn(JComponent component, int durationMs) {
        final float[] alpha = {0.0f};
        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            alpha[0] += 10.0f / durationMs;
            if (alpha[0] >= 1.0f) {
                alpha[0] = 1.0f;
                timer.stop();
            }
            // In a real implementation, we'd wrap the component in a layer that supports alpha
            // but for now, we'll just trigger a repaint to show it's "alive"
            component.repaint();
        });
        timer.start();
    }

    /**
     * Slides a component from a starting offset to its original position.
     */
    public static void slideIn(JComponent component, int startOffsetY, int durationMs) {
        Point originalLoc = component.getLocation();
        final int[] currentY = {originalLoc.y + startOffsetY};
        
        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            float step = (float)startOffsetY / (durationMs / 10.0f);
            currentY[0] -= step;
            if (currentY[0] <= originalLoc.y) {
                currentY[0] = originalLoc.y;
                timer.stop();
            }
            component.setLocation(originalLoc.x, currentY[0]);
        });
        timer.start();
    }

    /**
     * Animates a double value from start to end over duration.
     */
    public static void animateValue(double start, double end, int durationMs, java.util.function.Consumer<Double> onUpdate) {
        long startTime = System.currentTimeMillis();
        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / durationMs);
            
            // Cubic ease out for premium feel
            float easeProgress = 1 - (float) Math.pow(1 - progress, 3);
            double current = start + (end - start) * easeProgress;
            
            onUpdate.accept(current);
            
            if (progress >= 1.0f) {
                timer.stop();
            }
        });
        timer.start();
    }

    /**
     * Applies high-quality rendering hints to a Graphics2D object.
     */
    public static void setHighQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
}
