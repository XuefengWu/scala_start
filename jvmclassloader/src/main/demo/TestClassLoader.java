package demo;

/**
 * Created with IntelliJ IDEA.
 * User: 19002850
 * Date: 13-5-20
 * Time: 下午4:44
 * To change this template use File | Settings | File Templates.
 */
public class TestClassLoader {
    public static void main(String[] args) throws Exception {
        MyClassLoader loader1 = new MyClassLoader();

        // load demo.Base64
        Class clsB64 = Class.forName("demo.Base64", true, loader1);
        System.out.println("Base64 class: " + clsB64);

        // call the main method in Base64
        java.lang.reflect.Method main = clsB64.getMethod("main",
                new Class[] {String[].class});
        main.invoke(null, new Object[]{ new String[]{} });
    }
}
