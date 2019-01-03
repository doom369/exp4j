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
package net.objecthunter.exp4j.tokenizer;

import net.objecthunter.exp4j.ArrayStack;
import net.objecthunter.exp4j.function.DynamicArgumentFunction;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.function.OneArgumentFunction;
import net.objecthunter.exp4j.function.PredefinedArgumentFunction;
import net.objecthunter.exp4j.function.TwoArgumentFunction;

import java.util.Map;

public class FunctionToken extends Token {

    private final Function function;
    private int dynamicNumberOfArguments;

    public FunctionToken(Function function) {
        super(Token.TOKEN_FUNCTION);
        this.function = function;
        this.dynamicNumberOfArguments = 1;
    }

    public Function getFunction() {
        return function;
    }

    @Override
    public void process(ArrayStack output, Map<String, Double> variables) {
        double result = apply(output);
        output.push(result);
    }

    private double apply(ArrayStack output) {
        if (this.function instanceof OneArgumentFunction) {
            OneArgumentFunction oneArgumentFunction = (OneArgumentFunction) this.function;
            int functionArguments = oneArgumentFunction.getNumberOfArguments();
            verify(output.size(), functionArguments);
            return oneArgumentFunction.apply(output.pop());
        } else if (this.function instanceof TwoArgumentFunction) {
            TwoArgumentFunction twoArgumentFunction = (TwoArgumentFunction) this.function;
            int functionArguments = twoArgumentFunction.getNumberOfArguments();
            verify(output.size(), functionArguments);
            double right = output.pop();
            double left = output.pop();
            return twoArgumentFunction.apply(left, right);
        } else if (this.function instanceof PredefinedArgumentFunction) {
            PredefinedArgumentFunction predefinedArgumentFunction = (PredefinedArgumentFunction) this.function;
            return predefinedArgumentFunction.apply(output);
        } else {
            DynamicArgumentFunction dynamicArgumentFunction = (DynamicArgumentFunction) this.function;
            /* collect the arguments from the stack */
            int functionArguments = dynamicNumberOfArguments;
            dynamicArgumentFunction.verify(functionArguments);
            double[] args = new double[functionArguments];
            for (int j = functionArguments - 1; j >= 0; j--) {
                args[j] = output.pop();
            }
            return dynamicArgumentFunction.apply(args);
        }
    }

    private void verify(int stackSize, int numberOfArguments) {
        if (stackSize < numberOfArguments) {
            throw new IllegalArgumentException("Invalid number of arguments available for '"
                    + function.getName() + "' function");
        }
    }

    public int getDynamicNumberOfArguments() {
        if (function instanceof DynamicArgumentFunction) {
            return dynamicNumberOfArguments;
        }
        return function.getNumberOfArguments();
    }

    public void incrArgumentsCounter() {
        this.dynamicNumberOfArguments++;
    }
}
