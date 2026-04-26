package com.app.utils;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;

import java.awt.*;

public class ChartUtils {

    public static final Color[] PALETTE = {
        AppColors.PRIMARY,
        AppColors.SUCCESS,
        AppColors.ACCENT,
        AppColors.WARNING,
        AppColors.DANGER,
        new Color(26, 188, 156), // Teal
        AppColors.SECONDARY
    };

    public static void applyPremiumStyle(JFreeChart chart) {
        chart.setBackgroundPaint(AppColors.SURFACE);
        chart.getPlot().setBackgroundPaint(AppColors.SURFACE);
        chart.getPlot().setOutlineVisible(false);
        chart.setPadding(new RectangleInsets(10, 10, 10, 10));
        
        chart.setTextAntiAlias(true);
        chart.setAntiAlias(true);

        if (chart.getLegend() != null) {
            chart.getLegend().setFrame(org.jfree.chart.block.BlockBorder.NONE);
            chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));
            chart.getLegend().setItemPaint(AppColors.TEXT_SECONDARY);
        }

        Plot plot = chart.getPlot();
        if (plot instanceof PiePlot) {
            PiePlot pie = (PiePlot) plot;
            pie.setLabelGenerator(null);
            pie.setShadowPaint(null);
            pie.setSectionOutlinesVisible(false);
            
            int i = 0;
            for (Object key : pie.getDataset().getKeys()) {
                pie.setSectionPaint((Comparable) key, PALETTE[i % PALETTE.length]);
                i++;
            }

            if (plot instanceof RingPlot) {
                RingPlot ring = (RingPlot) plot;
                ring.setSectionDepth(0.35);
                ring.setSeparatorPaint(AppColors.SURFACE);
                ring.setSeparatorStroke(new BasicStroke(3.0f));
            }
        } else if (plot instanceof CategoryPlot) {
            CategoryPlot cp = (CategoryPlot) plot;
            cp.setRangeGridlinePaint(new Color(240, 240, 240));
            cp.setDomainGridlinePaint(new Color(240, 240, 240));
            
            cp.getRangeAxis().setAxisLineVisible(false);
            cp.getRangeAxis().setTickMarksVisible(false);
            cp.getRangeAxis().setTickLabelPaint(AppColors.TEXT_SECONDARY);
            cp.getDomainAxis().setAxisLineVisible(false);
            cp.getDomainAxis().setTickMarksVisible(false);
            cp.getDomainAxis().setTickLabelPaint(AppColors.TEXT_SECONDARY);

            if (cp.getRenderer() instanceof LineAndShapeRenderer) {
                LineAndShapeRenderer renderer = (LineAndShapeRenderer) cp.getRenderer();
                renderer.setSeriesPaint(0, AppColors.PRIMARY);
                renderer.setSeriesStroke(0, new BasicStroke(3.5f));
                renderer.setSeriesShapesVisible(0, true);
                renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8));
                renderer.setDrawOutlines(true);
                renderer.setUseFillPaint(true);
                renderer.setSeriesFillPaint(0, AppColors.SURFACE);
            }
        }
    }
}
