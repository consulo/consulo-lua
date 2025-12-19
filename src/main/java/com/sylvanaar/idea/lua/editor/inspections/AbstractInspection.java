/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.sylvanaar.idea.lua.editor.inspections;

import com.sylvanaar.idea.lua.lang.psi.statements.LuaStatementElement;
import consulo.application.ApplicationManager;
import consulo.application.util.function.Computable;
import consulo.language.editor.inspection.CustomSuppressableInspectionTool;
import consulo.language.editor.inspection.LocalInspectionTool;
import consulo.language.editor.inspection.SuppressionUtil;
import consulo.language.editor.intention.SuppressIntentionAction;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElement;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Jun 12, 2010
 * Time: 7:28:23 AM
 */
public abstract class AbstractInspection extends LocalInspectionTool implements CustomSuppressableInspectionTool
{
    private static final SuppressIntentionAction[] EMPTY_ARRAY = new SuppressIntentionAction[0];


    protected static final LocalizeValue ASSIGNMENT_ISSUES = LocalizeValue.localizeTODO("Assignment issues");
    protected static final LocalizeValue CONFUSING_CODE_CONSTRUCTS = LocalizeValue.localizeTODO("Potentially confusing code constructs");
    protected static final LocalizeValue CONTROL_FLOW = LocalizeValue.localizeTODO("Control Flow");
    protected static final LocalizeValue PROBABLE_BUGS = LocalizeValue.localizeTODO("Probable bugs");
    protected static final LocalizeValue ERROR_HANDLING = LocalizeValue.localizeTODO("Error handling");
    protected static final LocalizeValue GPATH = LocalizeValue.localizeTODO("GPath inspections");
    protected static final LocalizeValue METHOD_METRICS = LocalizeValue.localizeTODO("Method Metrics");
    protected static final LocalizeValue PERFORMANCE_ISSUES = LocalizeValue.localizeTODO("Performance issues");
    protected static final LocalizeValue VALIDITY_ISSUES = LocalizeValue.localizeTODO("Validity issues");
    protected static final LocalizeValue DATA_FLOW = LocalizeValue.localizeTODO("Data Flow Issues");
    protected static final LocalizeValue ANNOTATIONS_ISSUES = LocalizeValue.localizeTODO("Annotations verifying");
    
    private static Pattern SUPPRESS_IN_LINE_COMMENT_PATTERN = Pattern.compile("--" + SuppressionUtil.COMMON_SUPPRESS_REGEXP);;

    @Nonnull
    @Override
    public LocalizeValue[] getGroupPath() {
        return new LocalizeValue[]{LocalizeValue.localizeTODO("Lua"), getGroupDisplayName()};
    }

    private final String m_shortName = null;

    @Nonnull
    public String getShortName() {
        if (m_shortName == null) {
            final Class<? extends AbstractInspection> aClass = getClass();
            @NonNls final String name = aClass.getName();
            return name.substring(name.lastIndexOf((int) '.') + 1,
                    name.length() - "Inspection".length());
        }
        return m_shortName;
    }


//  @Nullable BaseInspectionVisitor buildLuaVisitor(@NotNull ProblemsHolder problemsHolder, boolean onTheFly) {
//    final BaseInspectionVisitor visitor = buildVisitor();
//    visitor.setProblemsHolder(problemsHolder);
//    visitor.setOnTheFly(onTheFly);
//    visitor.setInspection(this);
//    return visitor;
//  }
//  protected abstract BaseInspectionVisitor buildVisitor();
    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Nonnull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WEAK_WARNING;
    }

    public boolean isSuppressedFor(PsiElement element) {
        return  getElementToolSuppressedIn(element, getShortName()) != null;
    }

    public SuppressIntentionAction[] getSuppressActions(@Nullable PsiElement element) {
       return new SuppressIntentionAction[]
       {
          //TODO [VISTALL] new SuppressByCommentFix(HighlightDisplayKey.find(getShortName()), LuaStatementElement.class)
       };
    }

  @Nullable
  public static PsiElement getStatementToolSuppressedIn(final PsiElement place,
                                                        final String toolId,
                                                        final Class<? extends PsiElement> statementClass) {
    return SuppressionUtil.getStatementToolSuppressedIn(place, toolId, statementClass,
            SUPPRESS_IN_LINE_COMMENT_PATTERN);
  }

  @Nullable
  public PsiElement getElementToolSuppressedIn(@Nonnull final PsiElement place, final String toolId) {
    return ApplicationManager.getApplication().runReadAction(new Computable<PsiElement>() {
      @Nullable
      public PsiElement compute() {
        final PsiElement statement = getStatementToolSuppressedIn(place, toolId, LuaStatementElement.class);
        if (statement != null) {
          return statement;
        }

        return null;
      }
    });
  }


    @Nls
    @Nonnull
    public LocalizeValue getGroupDisplayName() {
        return LocalizeValue.localizeTODO("Lua");
    }

//    @Nullable
//    protected String buildErrorString(Object... args) {
//        return null;
//    }
//
//    protected boolean buildQuickFixesOnlyForOnTheFlyErrors() {
//        return false;
//    }
//
    @Nullable
    protected LuaFix buildFix(PsiElement location) {
        return null;
    }
//
//    @Nullable
//    protected LuaFix[] buildFixes(PsiElement location) {
//        return null;
//    }
    
}