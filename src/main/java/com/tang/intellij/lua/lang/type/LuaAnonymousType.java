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

package com.tang.intellij.lua.lang.type;

import com.tang.intellij.lua.psi.LuaNameDef;
import com.tang.intellij.lua.psi.LuaPsiResolveUtil;

/**
 *
 * Created by tangzx on 2017/1/12.
 */
public class LuaAnonymousType extends LuaType {

    private final String clazzName;

    private LuaAnonymousType(String typeName) {
        super();
        this.clazzName = typeName;
    }

    @Override
    public String getClassName() {
        return clazzName;
    }

    public static LuaAnonymousType create(LuaNameDef localDef) {
        return new LuaAnonymousType(LuaPsiResolveUtil.getAnonymousType(localDef));
    }
}
