package com.carestream.demo.hash;

public class GREP {

    public static void main(String[] args){
        String regexp = "(.*(A*B|AC)D.*)";
        NFA nfa = new NFA(regexp);
        
        String txt = "ABdBDC";
        if(nfa.recognizes(txt)){
            System.out.println("match");
        } else {
            System.out.println("Not match");
        }
    }
}
