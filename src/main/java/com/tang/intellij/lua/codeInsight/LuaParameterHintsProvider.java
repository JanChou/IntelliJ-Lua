/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tang.intellij.lua.codeInsight;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import com.intellij.codeInsight.hints.MethodInfo;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.tang.intellij.lua.psi.*;
import com.tang.intellij.lua.search.SearchContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by TangZX on 2016/12/14.
 */
public class LuaParameterHintsProvider implements InlayParameterHintsProvider {
    @NotNull
    @Override
    public List<InlayInfo> getParameterHints(PsiElement psiElement) {
        List<InlayInfo> list = new ArrayList<>();
        if (psiElement instanceof LuaCallExpr) {
            LuaCallExpr callExpr = (LuaCallExpr) psiElement;
            List<LuaParamNameDef> parameters = null;
            LuaFuncBodyOwner methodDef = callExpr.resolveFuncBodyOwner(new SearchContext(psiElement.getProject()));
            if (methodDef != null) {
                parameters = methodDef.getParamNameDefList();
            }

            LuaArgs args = callExpr.getArgs();
            if (args != null && parameters != null) {
                LuaExprList luaExprList = args.getExprList();
                if (luaExprList != null) {
                    List<LuaExpr> exprList = luaExprList.getExprList();
                    for (int i = 0; i < exprList.size() && i < parameters.size(); i++) {
                        LuaExpr expr = exprList.get(i);
                        list.add(new InlayInfo(parameters.get(i).getName(), expr.getTextOffset()));
                    }
                }
            }
        }

        return list;
    }

    @Nullable
    @Override
    public MethodInfo getMethodInfo(PsiElement psiElement) {
        if (psiElement instanceof LuaCallExpr) {
            List<String> list = new ArrayList<>();
            return new MethodInfo("MethodInfo", list);
        }
        return null;
    }

    @NotNull
    @Override
    public Set<String> getDefaultBlackList() {
        Set<String> stringSet = new HashSet<>();
        stringSet.add("test");
        return stringSet;
    }

    @Nullable
    @Override
    public Language getBlackListDependencyLanguage() {
        return null;
    }
}
