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

import net.objecthunter.exp4j.operator.Operator;

import java.util.Deque;
import java.util.Map;

/**
 * Represents an operator used in expressions
 */
public class OperatorToken extends Token {

    private final Operator operator;

    /**
     * Create a new instance
     * @param op the operator
     */
    public OperatorToken(Operator op) {
        super(Token.TOKEN_OPERATOR);
        if (op == null) {
            throw new IllegalArgumentException("Operator is unknown for token.");
        }
        this.operator = op;
    }

    /**
     * Get the operator for that token
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }

    @Override
    public void process(Deque<Double> output, Map<String, Double> variables) {
        if (output.size() < operator.getNumOperands()) {
            throw new IllegalArgumentException("Invalid number of operands available for '" + operator.getSymbol() + "' operator");
        }
        if (operator.getNumOperands() == 2) {
            /* pop the operands and push the result of the operation */
            double rightArg = output.pop();
            double leftArg = output.pop();
            output.push(operator.apply(leftArg, rightArg));
        } else if (operator.getNumOperands() == 1) {
            /* pop the operand and push the result of the operation */
            double arg = output.pop();
            output.push(operator.apply(arg));
        }
    }
}
