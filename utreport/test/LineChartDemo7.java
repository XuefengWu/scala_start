import java.awt.Color;   
import java.awt.Dimension;   
import javax.swing.JPanel;   
import org.jfree.chart.*;   
import org.jfree.chart.axis.NumberAxis;   
import org.jfree.chart.plot.CategoryPlot;   
import org.jfree.chart.plot.PlotOrientation;   
import org.jfree.chart.renderer.category.LineAndShapeRenderer;   
import org.jfree.data.category.CategoryDataset;   
import org.jfree.data.category.DefaultCategoryDataset;   
import org.jfree.ui.ApplicationFrame;   
import org.jfree.ui.RefineryUtilities;   
import org.jfree.util.ShapeUtilities;   
  
//http://read.pudn.com/downloads92/sourcecode/graph/359672/JFreeChart/src/com/rjsoft/linecharts/LineChartDemo7.java__.htm

public class LineChartDemo7 extends ApplicationFrame   
{   
   
    /**  
     *   
     */   
    private static final long serialVersionUID = 1L;   
   
    public LineChartDemo7(String s)   
    {   
        super(s);   
        JPanel jpanel = createDemoPanel();   
        jpanel.setPreferredSize(new Dimension(500, 270));   
        setContentPane(jpanel);   
    }   
   
    private static CategoryDataset createDataset()   
    {   
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();   
        defaultcategorydataset.addValue(21D, "Series 1", "Category 1");   
        defaultcategorydataset.addValue(50D, "Series 1", "Category 2");   
        defaultcategorydataset.addValue(152D, "Series 1", "Category 3");   
        defaultcategorydataset.addValue(184D, "Series 1", "Category 4");   
        defaultcategorydataset.addValue(299D, "Series 1", "Category 5");   
        defaultcategorydataset.addValue(275D, "Series 2", "Category 1");   
        defaultcategorydataset.addValue(121D, "Series 2", "Category 2");   
        defaultcategorydataset.addValue(98D, "Series 2", "Category 3");   
        defaultcategorydataset.addValue(103D, "Series 2", "Category 4");   
        defaultcategorydataset.addValue(210D, "Series 2", "Category 5");   
        defaultcategorydataset.addValue(198D, "Series 3", "Category 1");   
        defaultcategorydataset.addValue(165D, "Series 3", "Category 2");   
        defaultcategorydataset.addValue(55D, "Series 3", "Category 3");   
        defaultcategorydataset.addValue(34D, "Series 3", "Category 4");   
        defaultcategorydataset.addValue(77D, "Series 3", "Category 5");   
        return defaultcategorydataset;   
    }   
   
    private static JFreeChart createChart(CategoryDataset categorydataset)   
    {   
        JFreeChart jfreechart = ChartFactory.createLineChart("Line Chart Demo 7", "Category", "Count", categorydataset, PlotOrientation.VERTICAL, true, true, false);   
        jfreechart.setBackgroundPaint(Color.white);   
        CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();   
        categoryplot.setBackgroundPaint(Color.lightGray);   
        categoryplot.setRangeGridlinePaint(Color.white);   
        NumberAxis numberaxis = (NumberAxis)categoryplot.getRangeAxis();   
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());   
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer)categoryplot.getRenderer();   
        lineandshaperenderer.setSeriesShapesVisible(0, true);   
        lineandshaperenderer.setSeriesShapesVisible(1, false);   
        lineandshaperenderer.setSeriesShapesVisible(2, true);   
        lineandshaperenderer.setSeriesLinesVisible(2, false);   
        lineandshaperenderer.setSeriesShape(2, ShapeUtilities.createDiamond(4F));   
        lineandshaperenderer.setDrawOutlines(true);   
        lineandshaperenderer.setUseFillPaint(true);   
        lineandshaperenderer.setFillPaint(Color.white);   
        return jfreechart;   
    }   
   
    public static JPanel createDemoPanel()   
    {   
        JFreeChart jfreechart = createChart(createDataset());   
        return new ChartPanel(jfreechart);   
    }   
   
    public static void main(String args[])   
    {   
        LineChartDemo7 linechartdemo7 = new LineChartDemo7("Line Chart Demo 7");   
        linechartdemo7.pack();   
        RefineryUtilities.centerFrameOnScreen(linechartdemo7);   
        linechartdemo7.setVisible(true);   
    }   
}   