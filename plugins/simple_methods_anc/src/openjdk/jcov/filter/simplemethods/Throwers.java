/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package openjdk.jcov.filter.simplemethods;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.BiPredicate;

public class Throwers implements BiPredicate<ClassNode, MethodNode> {
    @Override
    public boolean test(ClassNode cnode, MethodNode m) {
        int index = 0;
        int opCode = -1;
        //find first instruction
        for(; index < m.instructions.size(); index++) {
            opCode = m.instructions.get(index).getOpcode();
            if(opCode >=0) {
                    break;
            }
        }
        //should be NEW
        if(opCode != Opcodes.NEW) {
            return false;
        }
        //next is DUP
        index++;
        opCode = m.instructions.get(index).getOpcode();
        if(opCode != Opcodes.DUP) {
            return false;
        }
        //some more simple code
        for(index++; index < m.instructions.size(); index++) {
            opCode = m.instructions.get(index).getOpcode();
            if(opCode >=0) {
                if (!Utils.isSimpleInstruction(opCode)) {
                    break;
                }
            }
        }
        //should be a constructor
        if(opCode != Opcodes.INVOKESPECIAL) {
            return false;
        }
        AbstractInsnNode node = m.instructions.get(index);
        if(!(node instanceof MethodInsnNode)) {
            return false;
        }
        if(!((MethodInsnNode)node).name.equals("<init>")) {
            return false;
        }
        index++;
        opCode = m.instructions.get(index).getOpcode();
        return opCode == Opcodes.ATHROW;
    }
}
