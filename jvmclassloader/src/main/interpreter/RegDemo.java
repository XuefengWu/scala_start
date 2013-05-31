package interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: 19002850
 * Date: 13-5-21
 * Time: 下午5:44
 * To change this template use File | Settings | File Templates.
 */
public class RegDemo {
    public static void main(String[] args){
        Pattern pattern = Pattern.compile("(\\w*)\\s+is\\s+(\\w)\\s*");
        Matcher matcher = pattern.matcher("prok is V");
        if(matcher.find()){
            System.out.println(matcher.group(1)+"="+matcher.group(2));
        }

    }
}
