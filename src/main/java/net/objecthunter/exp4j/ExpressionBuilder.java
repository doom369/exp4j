/* 
 * Copyright 2014 Frank Asseg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package net.objecthunter.exp4j;

import java.util.*;

import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.function.Functions;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.shuntingyard.ShuntingYard;

/**
 * Factory class for {@link Expression} instances. This class is the main API entrypoint. Users should create new
 * {@link Expression} instances using this factory class.
 */
public class ExpressionBuilder {

    private final String expression;

    private Map<String, Function> allowedFunctions;

    private final Map<String, Function> userFunctions;

    private final Map<String, Operator> userOperators;

    private final Set<String> variableNames;

    private boolean implicitMultiplication = true;

    /**
     * Create a new ExpressionBuilder instance and initialize it with a given expression string.
     * @param expression the expression to be parsed
     */
    public ExpressionBuilder(String expression) {
        if (expression == null || expression.trim().length() == 0) {
            throw new IllegalArgumentException("Expression can not be empty");
        }
        this.expression = expression;
        this.userOperators = new HashMap<>();
        this.userFunctions = new HashMap<>();
        this.variableNames = new HashSet<>();
    }

    /**
     * Add a {@link net.objecthunter.exp4j.function.Function} implementation available for use in the expression
     * @param function the custom {@link net.objecthunter.exp4j.function.Function} implementation that should be available for use in the expression.
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder function(Function function) {
        this.userFunctions.put(function.getName(), function);
        return this;
    }

    /**
     * Add multiple {@link net.objecthunter.exp4j.function.Function} implementations available for use in the expression
     * @param functions the custom {@link net.objecthunter.exp4j.function.Function} implementations
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder functions(Function... functions) {
        for (Function f : functions) {
            this.userFunctions.put(f.getName(), f);
        }
        return this;
    }

    /**
     * Add multiple {@link net.objecthunter.exp4j.function.Function} implementations available for use in the expression
     * @param functions A {@link java.util.List} of custom {@link net.objecthunter.exp4j.function.Function} implementations
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder functions(List<Function> functions) {
        for (Function f : functions) {
            this.userFunctions.put(f.getName(), f);
        }
        return this;
    }

    /**
     * Declare variable names used in the expression
     * @param variableNames the variables used in the expression
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder variables(Set<String> variableNames) {
        this.variableNames.addAll(variableNames);
        return this;
    }

    /**
     * Declare variable names used in the expression
     * @param variableNames the variables used in the expression
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder variables(String... variableNames) {
        Collections.addAll(this.variableNames, variableNames);
        return this;
    }

    public ExpressionBuilder withPhi() {
        return variable(Constants.PHI);
    }

    public ExpressionBuilder withE() {
        return variable(Constants.E);
    }

    public ExpressionBuilder withPi() {
        this.variableNames.add(Constants.PI);
        this.variableNames.add(Constants.PI_2);
        return this;
    }

    public ExpressionBuilder withAllConstants() {
        this.variableNames.add(Constants.PI);
        this.variableNames.add(Constants.PI_2);
        this.variableNames.add(Constants.PHI);
        this.variableNames.add(Constants.E);
        return this;
    }

    public ExpressionBuilder allowOnly(Functions allowedFunction) {
        if (this.allowedFunctions == null) {
            this.allowedFunctions = new HashMap<>();
        }
        addFunction(allowedFunction);
        return this;
    }

    public ExpressionBuilder allowOnly(Functions... allowedFunctions) {
        if (this.allowedFunctions == null) {
            this.allowedFunctions = new HashMap<>();
        }
        for (Functions functions : allowedFunctions) {
            addFunction(functions);
        }
        return this;
    }

    private void addFunction(Functions functions) {
        this.allowedFunctions.put(functions.name().toLowerCase(), functions.function);
    }

    /**
     * Declare a variable used in the expression
     * @param variableName the variable used in the expression
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder variable(String variableName) {
        this.variableNames.add(variableName);
        return this;
    }

    public ExpressionBuilder implicitMultiplication(boolean enabled) {
        this.implicitMultiplication = enabled;
        return this;
    }

    /**
     * Add an {@link net.objecthunter.exp4j.operator.Operator} which should be available for use in the expression
     * @param operator the custom {@link net.objecthunter.exp4j.operator.Operator} to add
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder operator(Operator operator) {
        this.checkOperatorSymbol(operator);
        this.userOperators.put(operator.getSymbol(), operator);
        return this;
    }

    private void checkOperatorSymbol(Operator op) {
        String name = op.getSymbol();
        for (char ch : name.toCharArray()) {
            if (!Operator.isAllowedOperatorChar(ch)) {
                throw new IllegalArgumentException("The operator symbol '" + name + "' is invalid");
            }
        }
    }

    /**
     * Add multiple {@link net.objecthunter.exp4j.operator.Operator} implementations which should be available for use in the expression
     * @param operators the set of custom {@link net.objecthunter.exp4j.operator.Operator} implementations to add
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder operator(Operator... operators) {
        for (Operator o : operators) {
            this.operator(o);
        }
        return this;
    }

    /**
     * Add multiple {@link net.objecthunter.exp4j.operator.Operator} implementations which should be available for use in the expression
     * @param operators the {@link java.util.List} of custom {@link net.objecthunter.exp4j.operator.Operator} implementations to add
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder operator(List<Operator> operators) {
        for (Operator o : operators) {
            this.operator(o);
        }
        return this;
    }

    /**
     * Build the {@link Expression} instance using the custom operators and functions set.
     * @return an {@link Expression} instance which can be used to evaluate the result of the expression
     */
    public Expression build() {
        if (expression.length() == 0) {
            throw new IllegalArgumentException("The expression can not be empty");
        }

        //if user didn't specified specific functions, we use all built ins
        userFunctions.putAll(allowedFunctions == null ? Functions.ALL : allowedFunctions);

        /* Check if there are duplicate vars/functions */
        Map<String, Double> consts = new HashMap<>();
        for (String variable : variableNames) {
            if (Functions.isBuiltinFunction(variable) || userFunctions.containsKey(variable)) {
                throw new IllegalArgumentException(
                        "A variable can not have the same name as a function [" + variable + "]");
            }
            Double constantValue = Constants.ALL.get(variable);
            if (constantValue != null) {
                consts.put(variable, constantValue);
            }
        }

        return new Expression(ShuntingYard.convertToRPN(this.expression, this.userFunctions, this.userOperators,
                this.variableNames, this.implicitMultiplication), this.userFunctions.keySet(), consts);
    }

}
