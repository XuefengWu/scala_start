package interpreter;

/**
 * Created with IntelliJ IDEA.
 * User: 19002850
 * Date: 13-5-21
 * Time: 下午5:29
 * To change this template use File | Settings | File Templates.
 */
import java.util.Map;
import java.util.HashMap;

public class InterpreterExample {
    public static void main(String[] args) {
        String expression = "w x z - +";
        Evaluator sentence = new Evaluator(expression);
        Map<String,Expression> variables = new HashMap<String,Expression>();
        variables.put("w", new Number(5));
        variables.put("x", new Number(10));
        variables.put("z", new Number(42));
        int result = sentence.interpret(variables);
        System.out.println(result);
    }
}
