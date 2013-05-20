package util

import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.category.CategoryDataset
import org.jfree.chart.renderer.category.LineAndShapeRenderer
import org.jfree.chart.ChartFactory
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import java.awt.Color
import org.jfree.util.ShapeUtilities
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator
import org.jfree.chart.axis.NumberTickUnit

object LineChartUtil {

  def createCategoryDataset(dataset:Seq[(Double,String)], series:String) =
    {   
        val defaultcategorydataset = new DefaultCategoryDataset()
        dataset.foreach(v => defaultcategorydataset.addValue(v._1, series, v._2))
        defaultcategorydataset
    }   
  
  def createLineChart(categorydataset:CategoryDataset,title:String) =   
    {   
        val jfreechart = ChartFactory.createLineChart(title, "Date", "Total", categorydataset, PlotOrientation.VERTICAL, true, true, false);   
        jfreechart.setBackgroundPaint(Color.white)
        
        val categoryplot = jfreechart.getPlot().asInstanceOf[CategoryPlot]   
        categoryplot.setBackgroundPaint(Color.white);   
        categoryplot.setRangeGridlinePaint(Color.white);   
        val numberaxis = categoryplot.getRangeAxis().asInstanceOf[NumberAxis]   
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits())

        val lineandshaperenderer = categoryplot.getRenderer().asInstanceOf[LineAndShapeRenderer]   
        lineandshaperenderer.setSeriesShapesVisible(0, true);   
        lineandshaperenderer.setSeriesShapesVisible(1, false);   
        lineandshaperenderer.setSeriesShapesVisible(2, true);   
        lineandshaperenderer.setSeriesLinesVisible(2, true);   
        lineandshaperenderer.setBaseItemLabelsVisible(true)
        lineandshaperenderer.setSeriesShape(2, ShapeUtilities.createDiamond(4F));   
        lineandshaperenderer.setDrawOutlines(true);   
        lineandshaperenderer.setUseFillPaint(true);   
        lineandshaperenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator())
        
        jfreechart   
    }  
  
}