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

package com.tang.intellij.lua.editor.completion;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.ProjectAndLibrariesScope;
import com.intellij.util.ProcessingContext;
import com.tang.intellij.lua.lang.LuaIcons;
import com.tang.intellij.lua.lang.type.LuaString;
import com.tang.intellij.lua.psi.LuaFile;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Created by tangzx on 2016/12/25.
 */
public class RequirePathCompletionProvider extends CompletionProvider<CompletionParameters> {

    static final char PATH_SPLITTER = '.';

    @Override
    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        PsiFile file = completionParameters.getOriginalFile();
        PsiElement cur = file.findElementAt(completionParameters.getOffset() - 1);
        if (cur != null) {
            LuaString ls = LuaString.getContent(cur.getText());
            int textOffset = completionParameters.getOffset() - cur.getTextOffset() - ls.start;
            String content = ls.value.substring(0, textOffset).replace('/', '.'); //统一用.来处理，aaa.bbb.ccc

            int last = content.lastIndexOf(PATH_SPLITTER);
            String prefixPackage = "";
            if (last !=- 1)
                prefixPackage = content.substring(0, last);
            String postfix = content.substring(last + 1);

            completionResultSet = completionResultSet.withPrefixMatcher(postfix);

            // add packages / files
            JavaPsiFacade facade = JavaPsiFacade.getInstance(file.getProject());
            PsiPackage psiPackage = facade.findPackage(prefixPackage);
            if (psiPackage != null) {
                PsiPackage[] subPackages = psiPackage.getSubPackages();
                for (PsiPackage subPackage : subPackages) {
                    String packageName = subPackage.getName();
                    if (packageName != null) {
                        LookupElement lookupElement = LookupElementBuilder
                                .create(packageName)
                                .withIcon(AllIcons.Nodes.Package)
                                .withInsertHandler(new PackageInsertHandler());
                        completionResultSet.addElement(PrioritizedLookupElement.withPriority(lookupElement, 2));
                    }
                }
                PsiFile[] files = psiPackage.getFiles(new ProjectAndLibrariesScope(file.getProject()));
                for (PsiFile psiFile : files) {
                    if (psiFile instanceof LuaFile) {
                        String fileName = FileUtil.getNameWithoutExtension(psiFile.getName());
                        LookupElement lookupElement = LookupElementBuilder
                                .create(fileName)
                                .withIcon(LuaIcons.FILE);
                        completionResultSet.addElement(PrioritizedLookupElement.withPriority(lookupElement, 1));
                    }
                }
            }
        }
    }

    static class PackageInsertHandler implements InsertHandler<LookupElement> {

        @Override
        public void handleInsert(InsertionContext insertionContext, LookupElement lookupElement) {
            int tail = insertionContext.getTailOffset();
            insertionContext.getDocument().insertString(tail, String.valueOf(PATH_SPLITTER));
            insertionContext.getEditor().getCaretModel().moveToOffset(tail + 1);
            AutoPopupController.getInstance(insertionContext.getProject()).autoPopupMemberLookup(insertionContext.getEditor(), null);
        }
    }
}
