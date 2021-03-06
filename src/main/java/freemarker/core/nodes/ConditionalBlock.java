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

import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * An element that represents a conditionally executed block: #if, #elseif or #elseif. Note that when an #if has
 * related #elseif-s or #else, an {@link IfBlock} parent must be used. For a lonely #if, no such parent is needed. 
 * 
 * @author <A HREF="mailto:jon@revusky.com">Jonathan Revusky</A>
 */

public final class ConditionalBlock extends TemplateElement {

    static final int TYPE_IF = 0;
    static final int TYPE_ELSE = 1;
    static final int TYPE_ELSE_IF = 2;
    
    final Expression condition;
    private final int type;
    boolean isLonelyIf;

    ConditionalBlock(Expression condition, TemplateElement nestedBlock, int type)
    {
        this.condition = condition;
        this.nestedBlock = nestedBlock;
        this.type = type;
    }

    public void accept(Environment env) throws TemplateException, IOException {
        if (condition == null || condition.evalToBoolean(env)) {
            if (nestedBlock != null) {
                env.visitByHiddingParent(nestedBlock);
            }
        }
    }
    
    protected String dump(boolean canonical) {
        StringBuffer buf = new StringBuffer();
        if (canonical) buf.append('<');
        buf.append(getNodeTypeSymbol());
        if (condition != null) {
            buf.append(' ');
            buf.append(condition.getCanonicalForm());
        }
        if (canonical) {
            buf.append(">");
            if (nestedBlock != null) {
                buf.append(nestedBlock.getCanonicalForm());
            }
            if (isLonelyIf) {
                buf.append("</#if>");
            }
        }
        return buf.toString();
    }
    
    public String getNodeTypeSymbol() {
        if (type == TYPE_ELSE) {
            return "#else";
        } else if (type == TYPE_IF) {
            return "#if";
        } else if (type == TYPE_ELSE_IF) {
            return "#elseif";
        } else {
            throw new RuntimeException("Unknown type");
        }
    }
    
    public int getParameterCount() {
        return 2;
    }

    public Object getParameterValue(int idx) {
        switch (idx) {
        case 0: return condition;
        case 1: return new Integer(type);
        default: throw new IndexOutOfBoundsException();
        }
    }

    public ParameterRole getParameterRole(int idx) {
        switch (idx) {
        case 0: return ParameterRole.CONDITION;
        case 1: return ParameterRole.AST_NODE_SUBTYPE;
        default: throw new IndexOutOfBoundsException();
        }
    }
    
}
