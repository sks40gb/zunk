/*
 * $Header: /home/common/cvsarea/ibase/dia/src/beans/LTextAreaWordParser.java,v 1.1 2004/08/06 16:46:29 nancy Exp $
 */
// Sentry Spelling Checker Engine
// SwingDemo.JTextAreaWordParser
//
// Copyright (c) 1999 Wintertree Software Inc. All rights reserved.
// www.wintertree-software.com
//
// Use, duplication, and disclosure of this file is governed
// by a license agreement between Wintertree Software Inc. and
// the licensee.
package beans;

import com.wintertree.ssce.StringWordParser;
import java.util.NoSuchElementException;
import javax.swing.JTextArea;

/**
 * Parse words from a Swing JTextArea component. In addition to parsing,
 * this class can also delete words, replace words, and detect doubled words.
 * The TextAreaWordParser maintains a cursor which points to the start of the
 * current word. The current word is always selected in the TextArea to show
 * context.
 *
 * <P>The text in the TextArea component is read when the TextAreaWordParser
 * is constructed. If the contents of the TextArea change, the updateText
 * method must be called.
 *
 * <P>If the JTextArea can contain text with HTML markups, extend this class
 * from HTMLStringWordParser instead of StringWordParser.
 *
 * @version $Id: LTextAreaWordParser.java,v 1.1 2004/08/06 16:46:29 nancy Exp $
 */
public class LTextAreaWordParser extends StringWordParser {

    /**
     * Construct a JTextAreaWordParser to parse words from the contents of a
     * JTextArea component.
     * @param textArea The JTextArea component to parse.
     * @param isHyphenDelim true if a hyphen is to be considered a word
     *	delimiter, false if it's part of a word
     * @param selectCurWord true if each word should be selected in the
     *	TextArea to show context. Enabling this option degrades performance.
     */
    public LTextAreaWordParser(JTextArea textArea, boolean isHyphenDelim,
            boolean selectCurWord) {
        super(textArea.getText(), isHyphenDelim);
        this.selectCurWord = selectCurWord;
        this.textArea = textArea;
    }

    /**
     * Delete a specified number of characters from the text starting at
     * the current cursor position.
     * @param numChars The number of characters to delete.
     * @exception NoSuchElementException Attempt to delete beyond the end of
     * the string.
     */
    public void deleteText(int numChars) throws NoSuchElementException {
        // Delete the text in the text area. Because both selectionStart and
        // selectionEnd are constrained so selectionStart <= selectionEnd,
        // we have to set selectionStart 2x to ensure it takes.
        textArea.setSelectionStart(cursor);
        textArea.setSelectionEnd(cursor + numChars);
        textArea.setSelectionStart(cursor);
        textArea.replaceSelection("");

        super.deleteText(numChars);
    }

    /**
     * Highlight the current word in the text area.
     */
    public void highlightWord() {
        if (hasMoreElements()) {
            String word = getWord();
            // following commented because it causes
            // double cursor (caret) on screen - nlm
            textArea.requestFocus();

            // selectionStart and selectionEnd are constrained so
            // selectionStart <= selectionEnd, so selectionStart
            // must be set 2x to ensure it gets set to what we want.
            textArea.setSelectionStart(cursor);
            textArea.setSelectionEnd(cursor + word.length());
            textArea.setSelectionStart(cursor);
        }
    }

    /**
     * Insert text at a specified position.
     * @param pos The position at which new text is to be inserted.
     * @param newText The text to insert.
     */
    public void insertText(int pos, String newText) {
        super.insertText(pos, newText);
        textArea.insert(newText, pos);
    }

    /**
     * Replace the word at the current position with a new word.
     * @param newWord The word to replace the word at the current position.
     * @exception NoSuchElementException The cursor is positioned at the end
     *	of the string.
     */
    public void replaceWord(String newWord) throws NoSuchElementException {
        // Update the text area first.
        String oldWord = getWord();

        // Replace the old with the new. Because selectionStart and
        // selectionEnd are constrained so selectionStart <= selectionEnd,
        // we have to set selectionStart 2x to ensure it takes.
        textArea.setSelectionStart(cursor);
        textArea.setSelectionEnd(cursor + oldWord.length());
        textArea.setSelectionStart(cursor);
        textArea.replaceSelection(newWord);
        super.replaceWord(newWord);
    }

    /**
     * Report that the contents of the TextArea have changed. This method
     * must be called if the TextArea is edited by the user or updated
     * by other software. Currently, it causes checking of the TextArea
     * to restart. In future, it will call the 1.1 TextArea's getCaret
     * method and check from that point.
     */
    public void updateText() {
        theString.setLength(0);
        theString.append(textArea.getText());
        cursor = 0;
        subWordLength = 0;
    }
    /**
     * true if the current word should always be selected in the TextArea
     * to show context.
     */
    private boolean selectCurWord;
    /**
     * The JTextArea being parsed
     */
    private JTextArea textArea;
}
