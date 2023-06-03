/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

package com.sylvanaar.idea.Lua.intentions.utils;

import consulo.language.psi.PsiElement;
import consulo.util.collection.HashingStrategy;
import consulo.util.collection.Maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author ilyas
 */
public class DuplicatesUtil {
//  public static void collectMethodDuplicates(Map<GrMethod, List<GrMethod>> map, HashSet<GrMethod> duplicateMethodsWarning, HashSet<GrMethod> duplicateMethodsErrors) {
//    for (GrMethod method : map.keySet()) {
//      List<GrMethod> duplicateMethods = map.get(method);
//
//      if (duplicateMethods != null && duplicateMethods.size() > 1) {
//        HashMap<PsiType, GrMethod> duplicateMethodsToReturnTypeMap = new HashMap<PsiType, GrMethod>();
//
//        for (GrMethod duplicateMethod : duplicateMethods) {
//          GrTypeElement typeElement = duplicateMethod.getReturnTypeElementLua();
//
//          PsiType methodReturnType;
//          if (typeElement != null) {
//            methodReturnType = typeElement.getType();
//          } else {
//            methodReturnType = PsiType.NULL;
//          }
//
//          duplicateMethodsWarning.add(duplicateMethod);
//
//          GrMethod grMethodWithType = duplicateMethodsToReturnTypeMap.get(methodReturnType);
//          if (grMethodWithType != null) {
//            duplicateMethodsErrors.add(duplicateMethod);
//            duplicateMethodsErrors.add(grMethodWithType);
//            duplicateMethodsWarning.remove(duplicateMethod);
//            duplicateMethodsWarning.remove(grMethodWithType);
//          }
//
//          duplicateMethodsToReturnTypeMap.put(methodReturnType, duplicateMethod);
//        }
//      }
//    }
//  }

  public static <D extends PsiElement> Map<D, List<D>> factorDuplicates(D[] elements, HashingStrategy<D> strategy) {
    if (elements == null || elements.length == 0) return Collections.emptyMap();

    Map<D, List<D>> map = Maps.newHashMap(strategy);

    for (D element : elements) {
      List<D> list = map.get(element);
      if (list == null) {
        list = new ArrayList<D>();
      }
      list.add(element);
      map.put(element, list);
    }

    return map;
  }
}
