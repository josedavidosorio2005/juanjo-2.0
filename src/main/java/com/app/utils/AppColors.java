package com.app.utils;

import java.awt.Color;

public class AppColors {
    // Brand Colors
    public static final Color PRIMARY = new Color(52, 152, 219);      // #3498db
    public static final Color SECONDARY = new Color(44, 62, 80);     // #2c3e50
    public static final Color ACCENT = new Color(155, 89, 182);      // #9b59b6
    
    // Semantic Colors
    public static final Color SUCCESS = new Color(46, 204, 113);     // #2ecc71
    public static final Color WARNING = new Color(241, 196, 15);     // #f1c40f
    public static final Color DANGER = new Color(231, 76, 60);       // #e74c3c
    public static final Color INFO = new Color(52, 152, 219);        // #3498db
    
    // Neutral Colors
    public static final Color BG_MAIN = new Color(245, 246, 250);    // #f5f6fa
    public static final Color BG_SIDEBAR = new Color(28, 40, 51);    // #1c2833
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = new Color(230, 230, 230);
    
    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    public static final Color TEXT_LIGHT = new Color(189, 195, 199);
    
    // KPI Transparent variants for icons
    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}
