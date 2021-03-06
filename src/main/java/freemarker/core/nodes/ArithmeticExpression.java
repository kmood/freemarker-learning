/*
 * Copyright (c) 2003 The Visigoth Software Society. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Visigoth Software Society (http://www.visigoths.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. Neither the name "FreeMarker", "Visigoth", nor any of the names of the 
 *    project contributors may be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact visigoths@visigoths.org.
 *
 * 5. Products derived from this software may not be called "FreeMarker" or "Visigoth"
 *    nor may "FreeMarker" or "Visigoth" appear in their names
 *    without prior written permission of the Visigoth Software Society.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE VISIGOTH SOFTWARE SOCIETY OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Visigoth Software Society. For more
 * information on the Visigoth Software Society, please see
 * http://www.visigoths.org/
 */

package freemarker.core.nodes;

import freemarker.core.ArithmeticEngine;
import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.exception._MiscTemplateException;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateException;
import freemarker.template.template_model.TemplateModel;

/**
 * An operator for arithmetic operations. Note that the + operator
 * also does string concatenation for backward compatibility.
 * @author <a href="mailto:jon@revusky.com">Jonathan Revusky</a>
 */
public final class ArithmeticExpression extends Expression {

    static final int TYPE_SUBSTRACTION = 0;
    static final int TYPE_MULTIPLICATION = 1;
    static final int TYPE_DIVISION = 2;
    static final int TYPE_MODULO = 3;

    private static final char[] OPERATOR_IMAGES = new char[] { '-', '*', '/', '%' };

    private final Expression lho;
    private final Expression rho;
    private final int operator;

    ArithmeticExpression(Expression lho, Expression rho, int operator) {
        this.lho = lho;
        this.rho = rho;
        this.operator = operator;
    }

   public TemplateModel _eval(Environment env) throws TemplateException
    {
        Number lhoNumber = lho.evalToNumber(env);
        Number rhoNumber = rho.evalToNumber(env);
        
        ArithmeticEngine ae =
            env != null 
                ? env.getArithmeticEngine()
                : getTemplate().getArithmeticEngine();
        switch (operator) {
            case TYPE_SUBSTRACTION : 
                return new SimpleNumber(ae.subtract(lhoNumber, rhoNumber));
            case TYPE_MULTIPLICATION :
                return new SimpleNumber(ae.multiply(lhoNumber, rhoNumber));
            case TYPE_DIVISION :
                return new SimpleNumber(ae.divide(lhoNumber, rhoNumber));
            case TYPE_MODULO :
                return new SimpleNumber(ae.modulus(lhoNumber, rhoNumber));
            default:
                throw new _MiscTemplateException(this, new Object[] {
                        "Unknown operation: ", new Integer(operator) });
        }
    }

    public String getCanonicalForm() {
        return lho.getCanonicalForm() + ' ' + OPERATOR_IMAGES[operator] + ' ' + rho.getCanonicalForm();
    }
    
    public String getNodeTypeSymbol() {
        return String.valueOf(OPERATOR_IMAGES[operator]);
    }
    
    public boolean isLiteral() {
        return constantValue != null || (lho.isLiteral() && rho.isLiteral());
    }

    protected Expression deepCloneWithIdentifierReplaced_inner(
            String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
    	return new ArithmeticExpression(
    	        lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState),
    	        rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState),
    	        operator);
    }
    
    public int getParameterCount() {
        return 3;
    }

    public Object getParameterValue(int idx) {
        switch (idx) {
        case 0: return lho;
        case 1: return rho;
        case 2: return new Integer(operator);
        default: throw new IndexOutOfBoundsException();
        }
    }

    public ParameterRole getParameterRole(int idx) {
        switch (idx) {
        case 0: return ParameterRole.LEFT_HAND_OPERAND;
        case 1: return ParameterRole.RIGHT_HAND_OPERAND;
        case 2: return ParameterRole.AST_NODE_SUBTYPE;
        default: throw new IndexOutOfBoundsException();
        }
    }
    
}
