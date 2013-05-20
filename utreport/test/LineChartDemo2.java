import java.awt.Color;   
import java.awt.Dimension;   
import javax.swing.JPanel;   
import org.jfree.chart.*;   
import org.jfree.chart.axis.NumberAxis;   
import org.jfree.chart.plot.PlotOrientation;   
import org.jfree.chart.plot.XYPlot;   
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;   
import org.jfree.data.xy.*;   
import org.jfree.ui.*;   
   
public class LineChartDemo2 extends ApplicationFrame   
{   
   
    /**  
     *   
     */   
    private static final long serialVersionUID = 1L;   
   
    public LineChartDemo2(String s)   
    {   
        super(s);   
        XYDataset xydataset = createDataset();   
        JFreeChart jfreechart = createChart(xydataset);   
        ChartPanel chartpanel = new ChartPanel(jfreechart);   
        chartpanel.setPreferredSize(new Dimension(500, 270));   
        setContentPane(chartpanel);   
    }   
   
    private static XYDataset createDataset()   
    {   
        XYSeries xyseries = new XYSeries("First");   
        xyseries.add(1.0D, 1.0D);   
        xyseries.add(2D, 4D);   
        xyseries.add(3D, 3D);   
        xyseries.add(4D, 5D);   
        xyseries.add(5D, 5D);   
        xyseries.add(6D, 7D);   
        xyseries.add(7D, 7D);   
        xyseries.add(8D, 8D);   
        XYSeries xyseries1 = new XYSeries("Second");   
        xyseries1.add(1.0D, 5D);   
        xyseries1.add(2D, 7D);   
        xyseries1.add(3D, 6D);   
        xyseries1.add(4D, 8D);   
        xyseries1.add(5D, 4D);   
        xyseries1.add(6D, 4D);   
        xyseries1.add(7D, 2D);   
        xyseries1.add(8D, 1.0D);   
        XYSeries xyseries2 = new XYSeries("Third");   
        xyseries2.add(3D, 4D);   
        xyseries2.add(4D, 3D);   
        xyseries2.add(5D, 2D);   
        xyseries2.add(6D, 3D);   
        xyseries2.add(7D, 6D);   
        xyseries2.add(8D, 3D);   
        xyseries2.add(9D, 4D);   
        xyseries2.add(10D, 3D);   
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();   
        xyseriescollection.addSeries(xyseries);   
        xyseriescollection.addSeries(xyseries1);   
        xyseriescollection.addSeries(xyseries2);   
        return xyseriescollection;   
    }   
   
    private static JFreeChart createChart(XYDataset xydataset)   
    {   
        JFreeChart jfreechart = ChartFactory.createXYLineChart("Line Chart Demo 2", "X", "Y", xydataset, PlotOrientation.VERTICAL, true, true, false);   
        jfreechart.setBackgroundPaint(Color.white);   
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();   
        xyplot.setBackgroundPaint(Color.white);   
        xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));   
        xyplot.setDomainGridlinePaint(Color.white);   
        xyplot.setRangeGridlinePaint(Color.white);   
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();   
        xylineandshaperenderer.setShapesVisible(true);   
        xylineandshaperenderer.setShapesFilled(true);   
        NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();   
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());   
        return jfreechart;   
    }   
   
    public static JPanel createDemoPanel()   
    {   
        JFreeChart jfreechart = createChart(createDataset());   
        return new ChartPanel(jfreechart);   
    }   
   
    public static void main(String args[])   
    {   
        LineChartDemo2 linechartdemo2 = new LineChartDemo2("Line Chart Demo 2");   
        linechartdemo2.pack();   
        RefineryUtilities.centerFrameOnScreen(linechartdemo2);   
        linechartdemo2.setVisible(true);   
    }   
}   