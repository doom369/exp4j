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

package net.objecthunter.exp4j.function;

import net.objecthunter.exp4j.exceptions.ParseExpressionException;

/**
 * A class representing a Function which can be used in an expression
 */
public abstract class Function {

    protected final String name;
    protected final int numberOfArguments;

    /**
     * Create a new Function with a given name and number of arguments
     * 
     * @param name the name of the Function
     */
    public Function(String name, int numberOfArguments) {
        if (!isValidFunctionName(name)) {
            throw new IllegalArgumentException("The function name '" + name + "' is invalid");
        }
        this.name = name;
        this.numberOfArguments = numberOfArguments;
    }

    /**
     * Get the name of the Function
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the number of arguments for this function
     * @return the number of arguments
     */
    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    public void validateArguments(int count) {
        if (this.numberOfArguments > count) {
            throw new ParseExpressionException("Not enough arguments for '" + this.name + "'");
        }
    }

    public static boolean isValidFunctionName(final String name) {
        if (name == null) {
            return false;
        }

        final int size = name.length();

        if (size == 0) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            final char c = name.charAt(i);
            if (Character.isLetter(c) || c == '_') {
                continue;
            } else if (Character.isDigit(c) && i > 0) {
                continue;
            }
            return false;
        }
        return true;
    }
}
