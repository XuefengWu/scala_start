import java.awt.*;   
import javax.swing.JPanel;   
import org.jfree.chart.*;   
import org.jfree.chart.axis.NumberAxis;   
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;   
import org.jfree.chart.plot.*;   
import org.jfree.chart.renderer.category.LineAndShapeRenderer;   
import org.jfree.data.category.CategoryDataset;   
import org.jfree.data.category.DefaultCategoryDataset;   
import org.jfree.ui.ApplicationFrame;   
import org.jfree.ui.RefineryUtilities;   
   
public class LineChartDemo5 extends ApplicationFrame   
{   
   
    /**  
     *   
     */   
    private static final long serialVersionUID = 1L;   
   
    public LineChartDemo5(String s)   
    {   
        super(s);   
        CategoryDataset categorydataset = createDataset();   
        JFreeChart jfreechart = createChart(categorydataset);   
        ChartPanel chartpanel = new ChartPanel(jfreechart);   
        chartpanel.setPreferredSize(new Dimension(500, 270));   
        setContentPane(chartpanel);   
    }   
   
    private static CategoryDataset createDataset()   
    {   
        String s = "First";   
        String s1 = "Second";   
        String s2 = "Third";   
        String s3 = "Type 1";   
        String s4 = "Type 2";   
        String s5 = "Type 3";   
        String s6 = "Type 4";   
        String s7 = "Type 5";   
        String s8 = "Type 6";   
        String s9 = "Type 7";   
        String s10 = "Type 8";   
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();   
        defaultcategorydataset.addValue(1.0D, s, s3);   
        defaultcategorydataset.addValue(4D, s, s4);   
        defaultcategorydataset.addValue(3D, s, s5);   
        defaultcategorydataset.addValue(5D, s, s6);   
        defaultcategorydataset.addValue(5D, s, s7);   
        defaultcategorydataset.addValue(7D, s, s8);   
        defaultcategorydataset.addValue(7D, s, s9);   
        defaultcategorydataset.addValue(8D, s, s10);   
        defaultcategorydataset.addValue(5D, s1, s3);   
        defaultcategorydataset.addValue(7D, s1, s4);   
        defaultcategorydataset.addValue(6D, s1, s5);   
        defaultcategorydataset.addValue(8D, s1, s6);   
        defaultcategorydataset.addValue(4D, s1, s7);   
        defaultcategorydataset.addValue(4D, s1, s8);   
        defaultcategorydataset.addValue(2D, s1, s9);   
        defaultcategorydataset.addValue(1.0D, s1, s10);   
        defaultcategorydataset.addValue(4D, s2, s3);   
        defaultcategorydataset.addValue(3D, s2, s4);   
        defaultcategorydataset.addValue(2D, s2, s5);   
        defaultcategorydataset.addValue(3D, s2, s6);   
        defaultcategorydataset.addValue(6D, s2, s7);   
        defaultcategorydataset.addValue(3D, s2, s8);   
        defaultcategorydataset.addValue(4D, s2, s9);   
        defaultcategorydataset.addValue(3D, s2, s10);   
        return defaultcategorydataset;   
    }   
   
    private static JFreeChart createChart(CategoryDataset categorydataset)   
    {   
        JFreeChart jfreechart = ChartFactory.createLineChart("Line Chart Demo 5", "Type", "Value", categorydataset, PlotOrientation.VERTICAL, true, true, false);   
        jfreechart.setBackgroundPaint(Color.white);   
        Shape ashape[] = new Shape[3];   
        int ai[] = {   
            -3, 3, -3   
        };   
        int ai1[] = {   
            -3, 0, 3   
        };   
        ashape[0] = new Polygon(ai, ai1, 3);   
        ashape[1] = new java.awt.geom.Rectangle2D.Double(-2D, -3D, 3D, 6D);   
        ai = (new int[] {   
            -3, 3, 3   
        });   
        ai1 = (new int[] {   
            0, -3, 3   
        });   
        ashape[2] = new Polygon(ai, ai1, 3);   
        DefaultDrawingSupplier defaultdrawingsupplier = new DefaultDrawingSupplier(DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, ashape);   
        CategoryPlot categoryplot = jfreechart.getCategoryPlot();   
        categoryplot.setOrientation(PlotOrientation.HORIZONTAL);   
        categoryplot.setBackgroundPaint(Color.lightGray);   
        categoryplot.setDomainGridlinePaint(Color.white);   
        categoryplot.setRangeGridlinePaint(Color.white);   
        categoryplot.setDrawingSupplier(defaultdrawingsupplier);   
        categoryplot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] {   
            10F, 6F   
        }, 0.0F));   
        categoryplot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] {   
            6F, 6F   
        }, 0.0F));   
        categoryplot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] {   
            2.0F, 6F   
        }, 0.0F));   
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer)categoryplot.getRenderer();   
        lineandshaperenderer.setShapesVisible(true);   
        lineandshaperenderer.setBaseItemLabelsVisible(true);   
        lineandshaperenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());   
        NumberAxis numberaxis = (NumberAxis)categoryplot.getRangeAxis();   
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());   
        numberaxis.setAutoRangeIncludesZero(false);   
        numberaxis.setUpperMargin(0.12D);   
        return jfreechart;   
    }   
   
    public static JPanel createDemoPanel()   
    {   
        JFreeChart jfreechart = createChart(createDataset());   
        return new ChartPanel(jfreechart);   
    }   
   
    public static void main(String args[])   
    {   
        LineChartDemo5 linechartdemo5 = new LineChartDemo5("Line Chart Demo 5");   
        linechartdemo5.pack();   
        RefineryUtilities.centerFrameOnScreen(linechartdemo5);   
        linechartdemo5.setVisible(true);   
    }   
}   