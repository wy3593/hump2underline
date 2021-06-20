package com.wy.hump2underline.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.wy.hump2underline.Utils;

public class ToggleAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        // 获取当前操作的类文件
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) return;
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        // 获取选中的文本
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null) {
            // 如果没有选中，选中当前光标所在的文本
            final EditorActionManager actionManager = EditorActionManager.getInstance();
            final EditorActionHandler actionHandler =
                    actionManager.getActionHandler(IdeActions.ACTION_EDITOR_SELECT_WORD_AT_CARET);
            actionHandler.execute(editor, editor.getCaretModel().getPrimaryCaret(), e.getDataContext());
        }
        // 切换驼峰和下划线
        String result = toggleText(editor.getSelectionModel().getSelectedText());
        // 替换选中的文本
        final Document document = editor.getDocument();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, result)
        );
    }

    private String toggleText(String text) {
        if (text.contains("_")) {
            return Utils.lineToHump(text);
        } else {
            return Utils.humpToLine(text);
        }
    }
}
