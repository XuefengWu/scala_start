import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
  
//JFreeChart Bar Chart（柱状图）   
public class CreateJFreeChartBar {   
  
    /**  
     * 创建JFreeChart Bar Chart（柱状图）  
     */  
    public static void main(String[] args) {   
        //步骤1：创建CategoryDataset对象（准备数据）   
        CategoryDataset dataset = createDataset();   
        //步骤2：根据Dataset 生成JFreeChart对象，以及做相应的设置   
        JFreeChart freeChart = createChart(dataset);   
        //步骤3：将JFreeChart对象输出到文件，Servlet输出流等   
        saveAsFile(freeChart, "d:\\bar.png", 500, 400);   
    }   
       
    //保存为文件   
    public static void saveAsFile(JFreeChart chart, String outputPath, int weight, int height) {   
        FileOutputStream out = null;   
        try {   
            File outFile = new File(outputPath);   
            if (!outFile.getParentFile().exists()) {   
                outFile.getParentFile().mkdirs();   
            }   
            out = new FileOutputStream(outputPath);   
            //保存为PNG文件   
            ChartUtilities.writeChartAsPNG(out, chart, 300, 200);   
            //保存为JPEG文件   
            //ChartUtilities.writeChartAsJPEG(out, chart, 500, 400);   
            out.flush();   
        } catch (FileNotFoundException e) {   
            e.printStackTrace();   
        } catch (IOException e) {   
            e.printStackTrace();   
        } finally {   
            if (out != null) {   
                try {   
                    out.close();   
                } catch (IOException e) {   
                    //do nothing   
                }   
            }   
        }   
    }   
  
    //根据CategoryDataset生成JFreeChart对象   
    public static JFreeChart createChart(CategoryDataset categoryDataset) { 
        JFreeChart jfreechart = ChartFactory.createLineChart("Line Chart Demo",    //标题   
                "产品",    //categoryAxisLabel （category轴，横轴，X轴的标签）   
                "数量",    //valueAxisLabel（value轴，纵轴，Y轴的标签）   
                categoryDataset, // dataset   
                PlotOrientation.VERTICAL,   
                true, // legend   
                false, // tooltips   
                false); // URLs   
           
        //以下的设置可以由用户定制，也可以省略   
        CategoryPlot  plot = (CategoryPlot) jfreechart.getPlot();   
        //背景色　透明度   
        plot.setBackgroundAlpha(0.2f);   
        //前景色　透明度   
        plot.setForegroundAlpha(1f);   
        //其它设置可以参考 CategoryPlot   
           
        return jfreechart;   
    }   
  
    /**  
     * 创建CategoryDataset对象  
     *   
     */  
    public static CategoryDataset createDataset() {   
           
        String []rowKeys = {"One", "Two", "Three"};   
        String []colKeys = {"1987", "1997", "2007"};   
           
        double [][] data = {   
                {50, 20, 30},   
                {20, 10D, 40D},   
                {40, 30.0008D, 38.24D},   
        };   
           
        //也可以使用以下代码   
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();   
        categoryDataset.addValue(10D, "colKey", "1");
        categoryDataset.addValue(11D, "colKey", "2");  
        categoryDataset.addValue(12D, "colKey", "3");  
        return categoryDataset;
        
        /*
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
        */
        //return DatasetUtilities.createCategoryDataset(rowKeys, colKeys, data);           
    }   
  
}  
