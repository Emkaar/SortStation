package com.emkaar;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    /**
     * Evaluate statement represented as string.
     *
     * @param statement mathematical statement containing digits, '.' (dot) as decimal mark,
     *                  parentheses, operations signs '+', '-', '*', '/'<br>
     *                  Example: <code>(1 + 38) * 4.5 - 1 / 2.</code>
     * @return string value containing result of evaluation or null if statement is invalid
     */
    private Deque<String> statementDeque = new LinkedList<>();
    private Deque<String> numsDeque = new LinkedList<>();
    private Deque<String> operationDeque = new LinkedList<>();

    public Deque<String> getStatementDeque() {
        return statementDeque;
    }

    public Deque<String> getNumsDeque() {
        return numsDeque;
    }

    public Deque<String> getOperationDeque() {
        return operationDeque;
    }

    public String evaluate(String statement) {
        statementDeque.clear();
        numsDeque.clear();
        operationDeque.clear();

        if(statement == null){
            return null;
        }
        if(statement.isEmpty()){
            return null;
        }

        createStatementDeque(statement);


        while(!statementDeque.isEmpty()){
            String token = statementDeque.pollFirst();
            if(isNumber(token)){
                numsDeque.addLast(token);
            }
            if(!isNumber(token) && !isOpenBracket(token) && !isCloseBracket(token)){
                if (!operationDeque.isEmpty() && getPriority(operationDeque.peekLast()) >= getPriority(token)) {
                    while (getPriority(operationDeque.peekLast()) >= getPriority(token)) {
                        if(numsDeque.size() < 2){return null;}
                        String arg1 = numsDeque.pollLast();
                        String arg2 = numsDeque.pollLast();
                        String oper = operationDeque.pollLast();
                        numsDeque.addLast(makeOperation(arg1, arg2, oper));
                        if(operationDeque.isEmpty()){break;}
                    }
                }
                operationDeque.addLast(token);
            }
            if(isOpenBracket(token)){
                operationDeque.addLast(token);
            }
            if(isCloseBracket(token)){
                while (!isOpenBracket(operationDeque.peekLast())){
                    if(numsDeque.size() < 2){return null;}//проверка на (5)
                    String arg1 = numsDeque.pollLast();
                    String arg2 = numsDeque.pollLast();
                    String oper = operationDeque.pollLast();
                    numsDeque.addLast(makeOperation(arg1, arg2, oper));
                    if(operationDeque.isEmpty()){ return null;} // проверка на 5+6)
                }
                operationDeque.pollLast();//удаляем открывающую скобку если нашли
            }
        }

        while(!operationDeque.isEmpty()){
            if(numsDeque.size() < 2){return null;}
            String arg1 = numsDeque.pollLast();
            String arg2 = numsDeque.pollLast();
            String oper = operationDeque.pollLast();
            numsDeque.addLast(makeOperation(arg1, arg2, oper));
        }

        return createRightAnswer(numsDeque.pollLast());
    }

    public String createRightAnswer(String answer){
        String correctAnswer;
        if(answer == null){
            return null;
        }
        if(answer.matches("-?[0-9]+[.][0]*")){
            correctAnswer = Integer.toString((int) Double.parseDouble(answer));
            return correctAnswer;
        }
        else {
            if(BigDecimal.valueOf(Double.parseDouble(answer)).scale() > 4) {
                correctAnswer = String.format("%.4f", Double.parseDouble(answer)).replace(",", ".");
                return correctAnswer;
            }
            return answer;
        }
    }

    public void createStatementDeque(String statement){
        Pattern pToken = Pattern.compile("([0-9]+[.]?[0-9]*)|([^0-9])");
        Matcher mToken = pToken.matcher(statement);
        while(mToken.find()){
            statementDeque.addLast(mToken.group());
        }
    }

    public boolean isNumber(String s){
        return s.matches("[0-9]+[.]?[0-9]*");
    }

    public String makeOperation(String a, String b, String symbol){
        double numA = Double.parseDouble(b);
        double numB = Double.parseDouble(a);
        switch (symbol){
            case "+": return Double.toString(numA+numB);

            case "-": return Double.toString(numA-numB);

            case "/": {
                if(numB == 0){
                return null;
                } else{
                    return Double.toString(numA/numB);
                }
            }

            case "*": return Double.toString(numA*numB);

        }
        return null;
    }

    public boolean isOpenBracket(String s){
        return s.equals("(");
    }

    public boolean isCloseBracket(String s){
        return s.equals(")");
    }

    public int getPriority(String s){
        if(s.equals("+") || s.equals("-")){
            return 1;
        } else if(s.equals("/") || s.equals("*")) {
            return 2;
        }
        return 0;
    }


}
