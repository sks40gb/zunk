/*
 * $Header: /home/common/cvsarea/ibase/dia/src/beans/JSpellCheckDlg.java,v 1.2 2004/08/20 11:49:47 nancy Exp $
 */
// Sentry Spelling Checker Engine
// JSpellCheckDlg
//
// Copyright (c) 1999 Wintertree Software Inc. All rights reserved.
// www.wintertree-software.com
//
// Use, duplication, and disclosure of this file is governed
// by a license agreement between Wintertree Software Inc. and
// the licensee.
package beans;

import java.util.Stack;
import java.util.Vector;
import com.lexpar.util.Log;
import com.wintertree.ssce.FileTextLexicon;
import com.wintertree.ssce.Lexicon;
import com.wintertree.ssce.MemTextLexicon;
import com.wintertree.ssce.SpellingSession;
import com.wintertree.ssce.SuggestionSet;
import com.wintertree.ssce.TypographicalComparator;
import com.wintertree.ssce.WordComparator;
import com.wintertree.ssce.WordParser;
import com.wintertree.util.MessageBox;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Check the spelling of some text and interact with the user to correct
 * any spelling errors encountered using a Swing dialog.
 *
 * <P>The basic steps involved in using the JSpellCheckDlg class to check
 * text are:
 * <OL>
 * <LI>Create a MemTextLexicon object to hold ignore-all and change-all
 *	words.
 * <LI>Create one or more FileTextLexicon objects representing user
 *	lexicons.
 * <LI>Create a FileTextLexicon and CompressedLexicon object for the
 *	main lexicon.
 * <LI>Create a SpellingSession object.
 * <LI>Add the lexicon objects to an array, and pass the array to the
 *	SpellingSession objects's setLexicons method.
 * <LI>Create a TypographicalComparator or EnglishPhoneticComparator object.
 * <LI>If your application can display help information about the spelling-
 *	checker dialog, create an object derived from the HelpViewer class.
 * <LI>If the text being checked is contained by a TextArea component,
 *	create a TextAreaWordParser object.
 * <LI>Create an array to hold the set of user lexicons.
 * <LI>Create a JSpellCheckDlg object.
 * <LI>Call JSpellCheckDlg's show method.
 * <LI>When the dialog closes, call the userCanceled method to determine
 *	if the changes made by the user should be kept.
 * <LI>If the JSpellCheckDlg object was constructed to check a String,
 *	call the getText method to obtain the corrected text.
 * </OL>
 *
 * @see com.wintertree.ssce.CompressedLexicon
 * @see com.wintertree.ssce.EnglishPhoneticComparator
 * @see com.wintertree.ssce.FileTextLexicon
 * @see com.wintertree.ssce.MemTextLexicon
 * @see com.wintertree.ssce.SpellingSession
 * @see com.wintertree.ssce.TypographicalComparator
 *
 * @version $Id: JSpellCheckDlg.java,v 1.2 2004/08/20 11:49:47 nancy Exp $
 */
public class JSpellCheckDlg extends JDialog {

    protected JSpellCheckDlg(Frame parent) {
        super(parent, true);
        //{{INIT_CONTROLS
        setTitle("Check Spelling");
        setResizable(false);
        getContentPane().setLayout(null);
        setSize(497, 320);
        setVisible(false);
        problemLabel.setText("Not in dictionary:");
        getContentPane().add(problemLabel);
        problemLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        problemLabel.setBounds(8, 8, 288, 12);
        problemTextFld.setToolTipText("The misspelled word");
        getContentPane().add(problemTextFld);
        problemTextFld.setBounds(8, 24, 372, 24);
        JLabel2.setText("Suggestions:");
        getContentPane().add(JLabel2);
        JLabel2.setFont(new Font("Dialog", Font.PLAIN, 12));
        JLabel2.setBounds(8, 96, 372, 16);
        JLabel3.setText("Add words to:");
        //getContentPane().add(JLabel3);
        JLabel3.setFont(new Font("Dialog", Font.PLAIN, 12));
        JLabel3.setBounds(8, 176, 372, 24);
        undoButton.setText("Undo");
        undoButton.setActionCommand("Undo");
        undoButton.setToolTipText("Undo the last change");
        getContentPane().add(undoButton);
        undoButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        undoButton.setBounds(392, 168, 96, 24);
        ignoreButton.setText("Ignore");
        ignoreButton.setActionCommand("Ignore");
        ignoreButton.setToolTipText("Ignore this occurrence of the word.");
        getContentPane().add(ignoreButton);
        ignoreButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        ignoreButton.setBounds(392, 8, 96, 24);
        ignoreAllButton.setText("Ignore All");
        ignoreAllButton.setActionCommand("Ignore All");
        ignoreAllButton.setToolTipText("Ignore this and all further occurrences of the word.");
        getContentPane().add(ignoreAllButton);
        ignoreAllButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        ignoreAllButton.setBounds(392, 40, 96, 24);
        changeButton.setText("Change");
        changeButton.setActionCommand("Change");
        changeButton.setToolTipText("Change this occurrence of the word.");
        getContentPane().add(changeButton);
        changeButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        changeButton.setBounds(392, 72, 96, 24);
        changeAllButton.setText("Change All");
        changeAllButton.setActionCommand("Change All");
        changeAllButton.setToolTipText("Change this and all further occurrences of the word.");
        getContentPane().add(changeAllButton);
        changeAllButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        changeAllButton.setBounds(392, 104, 96, 24);
        suggestButton.setText("Suggest");
        suggestButton.setActionCommand("Suggest");
        suggestButton.setToolTipText("Look for better suggestions.");
        //getContentPane().add(suggestButton);
        suggestButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        suggestButton.setBounds(392, 136, 96, 24);
        addButton.setText("Add");
        addButton.setActionCommand("Add");
        addButton.setToolTipText("Add this word to the selected user dictionary.");
        getContentPane().add(addButton);
        addButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        addButton.setBounds(392, 136, 96, 24);
        cancelButton.setText("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.setToolTipText("Stop the spelling check.");
        getContentPane().add(cancelButton);
        cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        cancelButton.setBounds(392, 200, 96, 24);
        JLabel1.setText("Change to:");
        getContentPane().add(JLabel1);
        JLabel1.setFont(new Font("Dialog", Font.PLAIN, 12));
        JLabel1.setBounds(8, 48, 368, 24);
        changeToTextFld.setToolTipText("The word to replace the misspelled word.");
        getContentPane().add(changeToTextFld);
        changeToTextFld.setBounds(8, 72, 369, 25);
        JScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane1.setOpaque(true);
        getContentPane().add(JScrollPane1);
        JScrollPane1.setBounds(8, 112, 370, 85);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setToolTipText("Suggested replacements for the misspelled word");
        JScrollPane1.getViewport().add(suggestionList);
        suggestionList.setBounds(0, 0, 367, 67);
        JScrollPane2.setOpaque(true);
        getContentPane().add(JScrollPane2);
        JScrollPane2.setBounds(8, 232, 483, 63);
        contextTextArea.setLineWrap(true);
        contextTextArea.setAutoscrolls(false);
        contextTextArea.setWrapStyleWord(true);
        contextTextArea.setEditable(false);
        JScrollPane2.getViewport().add(contextTextArea);
        contextTextArea.setBounds(0, 0, 480, 60);
        JScrollPane3.setOpaque(true);        
        JScrollPane3.setBounds(8, 200, 372, 49);
        userDictList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userDictList.setToolTipText("Select the user dictionary words will be added to when the Add button is pressed.");
        userDictList.setVisibleRowCount(1);
        JScrollPane3.getViewport().add(userDictList);
        userDictList.setBounds(0, 0, 369, 46);
        //}}

        //{{REGISTER_LISTENERS
        SymAction lSymAction = new SymAction();
        ignoreButton.addActionListener(lSymAction);
        ignoreAllButton.addActionListener(lSymAction);
        changeButton.addActionListener(lSymAction);
        changeAllButton.addActionListener(lSymAction);
        suggestButton.addActionListener(lSymAction);
        addButton.addActionListener(lSymAction);
        cancelButton.addActionListener(lSymAction);
        undoButton.addActionListener(lSymAction);
        SymListSelection lSymListSelection = new SymListSelection();
        suggestionList.addListSelectionListener(lSymListSelection);
        //}}

        suggestionListModel = new DefaultListModel();
        suggestionList.setModel(suggestionListModel);

        // Create a timer which will delay checking to give the dialog
        // a chance to draw itself. If this isn't done, an exception
        // is raised if any text is highlighted in the context area
        // before the dialog is painted.
        checkStartDelayTimer = new Timer(250, lSymAction);
        checkStartDelayTimer.setRepeats(false);	// 1-shot

        // Added by Nancy, 8/20/02, to avoid mouse-clicking the change button.
        getRootPane().setDefaultButton(changeButton);
    }

    public JSpellCheckDlg(Frame parent,
            SpellingSession speller, WordParser parser, WordComparator comparator,
            FileTextLexicon userLexicons[]) {
        this(parent);

        canceled = false;
        this.speller = speller;
        this.parser = parser;
        if (comparator == null) {
            comparator = new TypographicalComparator();
        }
        this.comparator = comparator;
        doubledWord = false;
        minSuggestDepth = 10;
        suggestSearchDepth = 0;
        userLexVec = new Vector();
        for (int i = 0; i < userLexicons.length; ++i) {
            userLexVec.addElement(userLexicons[i].getFileName());
        }
        userDictList.setListData(userLexVec);
        if (userLexicons.length > 0) {
            userDictList.setSelectedIndex(0);
        }
        undoStack = new Stack();

        // The context area isn't used.
        contextTextArea.setEnabled(false);
    }

    /**
     * Check the spelling of a String and interact with the user to correct
     * spelling errors found in it using a Swing dialog.
     * @param parent The parent frame
     * @param speller The spelling session used to check spelling
     * @param text The text to spell-check. When the dialog closes, call
     *	the getText method to get the updated text.
     * @param comparator Used to assess suggestions for misspelled words.
     *	If null, a TypographicalComparator will be used.
     * @param userLexicons Array of user lexicon files which appear in
     *	the "Add words to" choice. If null or empty, the "Add words to"
     *	choice will	not be displayed.
     * @see #getText
     */
    public JSpellCheckDlg(Frame parent,
            SpellingSession speller, String text, WordComparator comparator,
            FileTextLexicon userLexicons[]) {
        this(parent);

        //Log.print("enter JSpellChckDlg");

        canceled = false;
        this.speller = speller;
        if (comparator == null) {
            comparator = new TypographicalComparator();
        }
        this.comparator = comparator;
        doubledWord = false;
        minSuggestDepth = 10;
        suggestSearchDepth = 0;
        undoStack = new Stack();

        // Set up a parser to get words from the context area.
        contextTextArea.setText(text);
        this.parser = new LTextAreaWordParser(contextTextArea,
                speller.getOption(speller.SPLIT_HYPHENATED_WORDS_OPT), true);

        userLexVec = new Vector();
        for (int i = 0; i < userLexicons.length; ++i) {
            userLexVec.addElement(userLexicons[i].getFileName());
        }
        userDictList.setListData(userLexVec);

    }

    public void setVisible(boolean b) {
        if (b) {
            setLocation(50, 50);

            // Delay to give the dialog a chance to draw itself. When the
            // delay expires, we'll start checking.
            checkStartDelayTimer.start();
        }
        super.setVisible(b);
    }

    /**
     * Obtain the (possibly updated) text. This method should be called
     * only if the SpellingDialog was constructed to check a String.
     * @return The updated text
     */
    public String getText() {
        if (contextTextArea != null) {
            return contextTextArea.getText();
        }
        return null;
    }

    /**
     * Determine if the user canceled the spelling check.
     * @return true if the user canceled; false if the spelling check
     *	ran to completion.
     */
    public boolean userCanceled() {
        return canceled;
    }

    public void addNotify() {
        // Record the size of the window prior to calling parents addNotify.
        Dimension size = getSize();

        super.addNotify();

        if (frameSizeAdjusted) {
            return;
        }
        frameSizeAdjusted = true;

        // Adjust size of frame according to the insets
        Insets insets = getInsets();
        setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height);
    }

    /**
     * Indicate whether we're busy or not.
     */
    protected void busy(boolean b) {
        if (b) {
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        } else {
            setCursor(java.awt.Cursor.getDefaultCursor());
        }
    }

    /**
     * Replace the word in the problem field with the word in the change-to
     * field.
     */
    protected void changeWord() {
        // Replace function:  Substitute the word in the
        // Change To field for the current word.
        if (debug) {
            System.out.println("replacing " + parser.getWord() + " with " +
                    changeToTextFld.getText());
        }

        // Temporarily make the context area editable or this won't work.
        contextTextArea.setEditable(true);
        parser.replaceWord(changeToTextFld.getText());
        contextTextArea.setEditable(false);

        // Make the change undo-able.
        UndoRecord undoRec = new UndoRecord();
        undoRec.offset = parser.getCursor();
        undoRec.origText = problemTextFld.getText();
        undoRec.newText = changeToTextFld.getText();
        undoStack.push(undoRec);
    }

    /**
     * Delete the word in the problem field.
     */
    protected void deleteWord() {
        // Delete the current word. Any whitespace appearing before the word
        // is also deleted.
        if (debug) {
            System.out.println("deleting " + parser.getWord());
        }
        StringBuffer delText = new StringBuffer();
        // Temporarily make the context area editable or this won't work.
        contextTextArea.setEditable(true);
        int offset = parser.deleteWord(delText);
        contextTextArea.setEditable(false);

        // Make the change undo-able.
        UndoRecord undoRec = new UndoRecord();
        undoRec.offset = offset;
        undoRec.origText = delText.toString();
        undoRec.newText = "";
        undoStack.push(undoRec);
    }

    /**
     * Locate a user lexicon with a given name in the set of lexicons owned
     * by the spelling session.
     * @param fileName Name of the lexicon file to locate
     * @return The lexicon object associated with the file name, or null if
     *	the file could not be found.
     */
    protected FileTextLexicon findLex(String fileName) {
        Lexicon lexSet[] = speller.getLexicons();
        if (lexSet == null) {
            return null;
        }
        for (int i = 0; i < lexSet.length; ++i) {
            if (lexSet[i] instanceof FileTextLexicon) {
                FileTextLexicon lex = (FileTextLexicon) lexSet[i];
                if (lex.getFileName().equals(fileName)) {
                    return lex;
                }
            }
        }
        return null;
    }

    /**
     * Fill the suggestions list with suggestions for the word in the
     * problem field.
     * @return The number of suggestions obtained.
     */
    protected int getSuggestions() {
        SuggestionSet suggestions = new SuggestionSet(16);
        busy(true);
        speller.suggest(problemTextFld.getText(), suggestSearchDepth,
                comparator, suggestions);
        suggestionListModel.clear();
        int i;
        for (i = 0; i < suggestions.size(); ++i) {
            suggestionListModel.addElement(suggestions.wordAt(i));
        }
        busy(false);
        return i;
    }

    /**
     * Locate a temporary lexicon in the set of lexicons
     * owned by the spelling session.
     * @return The lexicon object, or null if a suitable lexicon could not
     *	be found.
     */
    protected MemTextLexicon getTempLex() {
        Lexicon lexSet[] = speller.getLexicons();

        if (lexSet == null) {
            return null;
        }
        for (int i = 0; i < lexSet.length; ++i) {
            if (lexSet[i] instanceof MemTextLexicon) {
                return (MemTextLexicon) lexSet[i];
            }
        }
        return null;
    }

    /**
     * Check the spelling of words until the user's attention is required.
     * @return true if there is more text to check; false if all text has
     *	been checked.
     */
    protected boolean runChecker() {
        Vector empty = new Vector();
        problemTextFld.setText("");
        changeToTextFld.setText("");
        changeButton.setText("Change");
        doubledWord = false;
        suggestWord = "";
        updateUI();

        // Check the text from the current position forward. The check method
        // keeps checking until all words have been checked or
        // a problem is encountered. Note that CHANGE_WORD_RSLT is
        // handled without any interaction with the user.
        StringBuffer replWord = new StringBuffer();
        int result;
        int numAutoChanges = 0;
        // increase maxAutoChanges to 5 to enable auto changes
        // int maxAutoChanges = 0;
        // 2007-06-05: self-test, Modified by V.Sathiyanarayanan
        // allows auto changes for Change All events, 
        // otherwise do nothing, if maxAutoChanges = 0
        int maxAutoChanges = 5;
        // ends here
        int lastAutoChangeOffset = -1;
        int lastAutoChangeLen = 0;
        do {
            busy(true);
            result = speller.check(parser, replWord);
            busy(false);

            if (debug) {
                System.out.println("runChecker: check result: " + result +
                        " word: " + (parser.hasMoreElements() ? parser.getWord() : "(none)") +
                        " replWord: " + replWord.toString());
            }

            // See if we should make an automatic change.
            if ((result & speller.AUTO_CHANGE_WORD_RSLT) != 0) {
                int changeOffset = parser.getCursor();

                // It's possible for automatic changes to recurse forever.
                // Example: xxx AUTO_CHANGE a xxx
                // To prevent this, limit the number of automatic changes made
                // within the replacement text.
                if (changeOffset >= lastAutoChangeOffset &&
                        changeOffset < lastAutoChangeOffset + lastAutoChangeLen) {
                    ++numAutoChanges;
                } else {
                    numAutoChanges = 0;
                }
                if (numAutoChanges >= maxAutoChanges) {
                    // Don't make the replacement. Just skip this word.
                    parser.nextWord();
                } else {
                    // Automatically replace the current word with the
                    // indicated replacement.
                    problemTextFld.setText(parser.getWord());
                    changeToTextFld.setText(replWord.toString());
                    changeWord();
                    lastAutoChangeOffset = changeOffset;
                    lastAutoChangeLen = replWord.length();
                }
            }
        } while ((result & speller.AUTO_CHANGE_WORD_RSLT) != 0);

        if ((result & speller.END_OF_TEXT_RSLT) != 0) {
            // close the dialog
            setVisible(false);
            dispose();
            return false;
        }

        // Highlight the current word so the user can see its context.
        parser.highlightWord();

        if ((result & speller.CONDITIONALLY_CHANGE_WORD_RSLT) != 0) {
            problemLabel.setText("Consider changing:");
            problemTextFld.setText(parser.getWord());
            changeToTextFld.setText(replWord.toString());

            // Display suggestions to go along with the suggested word.
            // Note that we don't copy the first suggestion to the
            // replacement field, because the repWord parameter
            // returned by check() is the best suggestion.
            suggestSearchDepth = minSuggestDepth;
            getSuggestions();
            updateUI();
        } else if ((result & speller.UNCAPPED_WORD_RSLT) != 0) {
            problemLabel.setText("Capitalization:");
            problemTextFld.setText(parser.getWord());
            changeToTextFld.setText(replWord.toString());
            updateUI();
        } else if ((result & speller.DOUBLED_WORD_RSLT) != 0) {
            problemLabel.setText("Doubled word:");
            problemTextFld.setText(parser.getWord());
            changeToTextFld.setText("");

            // The Change button serves double-duty as a Delete button when
            // a doubled word is encountered. Indicate we're in this state.
            doubledWord = true;
            updateUI();
        } else if ((result & speller.MIXED_CASE_WORD_RSLT) != 0) {
            problemLabel.setText("Mixed case:");
            problemTextFld.setText(parser.getWord());
            changeToTextFld.setText(replWord.toString());
            updateUI();
        } else if ((result & speller.MIXED_DIGITS_WORD_RSLT) != 0) {
            problemLabel.setText("Contains digits:");
            problemTextFld.setText(parser.getWord());
            changeToTextFld.setText(replWord.toString());
            updateUI();
        } else if ((result & speller.MISSPELLED_WORD_RSLT) != 0) {
            // Present the misspelled word to the user.
            problemLabel.setText("Not in dictionary:");
            problemTextFld.setText(parser.getWord());
            changeToTextFld.setText(replWord.toString());

            // Fill the Suggestions list with suggested replacements for the
            // misspelled word.
            suggestSearchDepth = minSuggestDepth;
            if (getSuggestions() > 0) {
                // Display the first suggestion in the Change To field.
                changeToTextFld.setText((String) suggestionListModel.getElementAt(0));
            }

            updateUI();
        }

        return true;
    }

    /**
     * Undo the last change made to the tex. If no changes can be undone,
     * this method does nothing. Subsequent calls to this method will undo
     * successive changes, starting with the most recent change.
     */
    public void undo() {
        // Pop the last change off the stack.
        UndoRecord undoRec = (UndoRecord) undoStack.pop();

        // Position to the location of the change.
        parser.setCursor(undoRec.offset);

        // Remove the text added by the change.
        if (undoRec.newText.length() > 0) {
            // Temporarily make the context area editable or this won't work.
            contextTextArea.setEditable(true);
            parser.deleteText(undoRec.newText.length());
            contextTextArea.setEditable(false);
        }

        // Insert the original text.
        if (undoRec.origText.length() > 0) {
            // Temporarily make the context area editable or this won't work.
            contextTextArea.setEditable(true);
            parser.insertText(undoRec.offset, undoRec.origText);
            contextTextArea.setEditable(false);
        }

        // If the undone change resulted from Change All, delete the word
        // from the temp lexicon. Otherwise, the undone change will be
        // restored automatically.
        MemTextLexicon tmpLex = getTempLex();
        if (tmpLex != null) {
            StringBuffer junk = new StringBuffer();
            if (tmpLex.findWord(undoRec.origText, true, junk) ==
                    MemTextLexicon.AUTO_CHANGE_PRESERVE_CASE_ACTION) {
                try {
                    tmpLex.deleteWord(undoRec.origText);
                } catch (Exception e) {
                    // Do nothing.
                }
            }
        }
    }

    /**
     * Update the user interface to reflect the current state. Various
     * buttons are enabled or disabled depending on whether their associated
     * actions are appropriate at this point.
     */
    protected void updateUI() {
        // If the user has changed the word in the problem field, reset the
        // suggestion search depth.
        if (!problemTextFld.getText().equals(suggestWord)) {
            suggestSearchDepth = minSuggestDepth;
        }

        // The change-to field is enabled unless a doubled word was detected.
        changeToTextFld.setEnabled(!doubledWord);

        // The Ignore button is always enabled.
        ignoreButton.setEnabled(true);

        // The Ignore All button is always enabled unless there is no temporary
        // lexicon to remember the ignore-all words, or we've detected a
        // doubled word.
        ignoreAllButton.setEnabled(!doubledWord && getTempLex() != null);

        // The Change button's label is set to Delete in the doubled-word
        // case.
        if (doubledWord) {
            changeButton.setText("Delete");
        } else {
            changeButton.setText("Change");
        }
        changeButton.setEnabled(true);

        // The Change All button is enabled if a doubled word hasn't been
        // detected and there's a temporary lexicon to remember
        // change-all words.
        changeAllButton.setEnabled(!doubledWord && getTempLex() != null);

        // The Add button is enabled only if there is a lexicon to add to
        // and we aren't processing doubled words.
        ListModel userLexLM = userDictList.getModel();
        addButton.setEnabled(!doubledWord && userLexLM.getSize() > 0);

        // The Suggest button is enabled only if further suggestions can
        // be obtained, there is a word to obtain suggestions for,
        // and we aren't processing doubled words.
        // nlm suggestButton.setEnabled(!doubledWord &&
        //  suggestSearchDepth < speller.MAX_SUGGEST_DEPTH &&
        //  problemTextFld.getText().length() > 0);

        // The Cancel button is always enabled.
        cancelButton.setEnabled(true);

        // The Undo button is enabled if there are actions to undo.
        undoButton.setEnabled(!undoStack.empty());
    }
    /**
     * Initial depth used to obtain suggestions.
     */
    public int minSuggestDepth;
    /**
     * true if the user canceled
     */
    protected boolean canceled;
    /**
     * WordComparator used to locate suggestions for misspelled words.
     */
    protected WordComparator comparator;
    /**
     * Delay starting until the dialog has been displayed. This
     * prevents an exception which occurs if text is highlighted in
     * the context area before the dialog is visible.
     */
    protected boolean firstTime = true;
    /**
     * WordParser used to obtain words to check and update corrections.
     */
    protected WordParser parser;
    /**
     * SpellingSession used to check spelling and look up suggestions
     */
    protected SpellingSession speller;
    /**
     * true if a doubled word was detected.
     */
    private boolean doubledWord;
    /**
     * Current depth used to locate suggestions.
     */
    private int suggestSearchDepth;
    /**
     * List model used to update the suggestions JList, which is dynamic.
     */
    DefaultListModel suggestionListModel;
    /** Contains the last word for which suggestions were located. The
     * suggestion search depth is reset if the user changes the word.
     */
    private String suggestWord;
    /**
     * 1-shot timer used to delay checking until the dialog is displayed.
     * This prevents an exception which occurs when text is highlighted in
     * the context area before the dialog has been painted.
     */
    private Timer checkStartDelayTimer;
    /**
     * Stack of changes which can be undone.
     */
    private Stack undoStack;
    /**
     * User dictionary names.
     */
    Vector userLexVec;

    // set to true to enable debugging messages
    public boolean debug;

    // Used by addNotify
    boolean frameSizeAdjusted = false;

    //{{DECLARE_CONTROLS
    JLabel problemLabel = new JLabel();
    JTextField problemTextFld = new JTextField();
    JLabel JLabel2 = new JLabel();
    JLabel JLabel3 = new JLabel();
    JButton undoButton = new JButton();
    JButton ignoreButton = new JButton();
    JButton ignoreAllButton = new JButton();
    JButton changeButton = new JButton();
    JButton changeAllButton = new JButton();
    JButton suggestButton = new JButton();
    JButton addButton = new JButton();
    JButton cancelButton = new JButton();
    JLabel JLabel1 = new JLabel();
    JTextField changeToTextFld = new JTextField();
    JScrollPane JScrollPane1 = new JScrollPane();
    JList suggestionList = new JList();
    JScrollPane JScrollPane2 = new JScrollPane();
    JTextArea contextTextArea = new JTextArea();
    JScrollPane JScrollPane3 = new JScrollPane();
    JList userDictList = new JList();
    //}}

    class SymAction implements java.awt.event.ActionListener {

        public void actionPerformed(java.awt.event.ActionEvent event) {
            try {
                Object object = event.getSource();
                if (object == ignoreButton) {
                    ignoreButton_actionPerformed(event);
                } else if (object == ignoreAllButton) {
                    ignoreAllButton_actionPerformed(event);
                } else if (object == changeButton) {
                    changeButton_actionPerformed(event);
                } else if (object == changeAllButton) {
                    changeAllButton_actionPerformed(event);
                } else if (object == suggestButton) {
                    suggestButton_actionPerformed(event);
                } else if (object == addButton) {
                    addButton_actionPerformed(event);
                } else if (object == cancelButton) {
                    cancelButton_actionPerformed(event);
                } else if (object == undoButton) {
                    undoButton_actionPerformed(event);
                }

                if (object == checkStartDelayTimer) {
                    checkStartDelayTimer_actionPerformed(event);
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    }

    void ignoreButton_actionPerformed(java.awt.event.ActionEvent event) {
        // Skip this word and advance to the next word.
        parser.nextWord();
        runChecker();
    }

    void ignoreAllButton_actionPerformed(java.awt.event.ActionEvent event) {
        // Save the problem word in the temporary lexicon, and advance to the
        // next word.
        MemTextLexicon tmpLex = getTempLex();
        String word = problemTextFld.getText();
        if (speller.getOption(speller.STRIP_POSSESSIVES_OPT)) {
            word = speller.stripPossessives(word);
        }
        try {
            tmpLex.addWord(word);
        } catch (Exception e) {
            // Do nothing.
        }

        // Keep checking. This and all further occurrences of the word will
        // be ignored.
        runChecker();
    }

    void changeButton_actionPerformed(java.awt.event.ActionEvent event) {
        // The Change button has two overloaded functions: Change and Delete.
        // Normally its function is to replace the problem word with another word.
        // If a doubled word is detected, its function changes to delete
        // the second occurrence.
        if (doubledWord) {
            // Delete function:	 Delete the current word.
            deleteWord();
        } else {
            // Replace function:  Substitute the word in the
            // Change To field for the current word.
            changeWord();
        }

        runChecker();
    }

    void changeAllButton_actionPerformed(java.awt.event.ActionEvent event) {
        // Add the word and replacement to the temporary lexicon,
        // so the word will automatically be replaced next time it occurs.
        if (!problemTextFld.getText().equals(changeToTextFld.getText())) {
            MemTextLexicon tmpLex = getTempLex();
            String word = problemTextFld.getText();
            String changeToWord = changeToTextFld.getText();
            if (speller.getOption(speller.STRIP_POSSESSIVES_OPT)) {
                word = speller.stripPossessives(word);
                changeToWord = speller.stripPossessives(changeToWord);
            }
            if (tmpLex != null) {
                try {
                    tmpLex.addWord(word,
                            tmpLex.AUTO_CHANGE_PRESERVE_CASE_ACTION, changeToWord);
                } catch (Exception e) {
                    MessageBox.createMessageBox(getTitle(),
                            "Error saving word: " + e);
                }
            }
        }

        // Keep checking. This and all further occurrences of the word will
        // be replaced.
        runChecker();
    }

    void suggestButton_actionPerformed(java.awt.event.ActionEvent event) {
        // Fill the Suggestions list with
        // suggested replacements for the current word. Note that we save the
        // word for which suggestions were obtained so we can later tell if
        // the user has edited the word. If the word is changed, the search
        // depth can start back at the beginning.
        suggestWord = problemTextFld.getText();
        if (getSuggestions() > 0) {
            // Display the first suggestion in the Change To field.
            changeToTextFld.setText((String) suggestionListModel.getElementAt(0));
        }

        // Advance to the next search depth in case the user
        // selects the Suggest button again.
        suggestSearchDepth += 10;
        updateUI();

    // Pressing the Suggest button doesn't properly dispose
    // of the current problem, so we're not ready to call runChecker.
    }

    void addButton_actionPerformed(java.awt.event.ActionEvent event) {
        if (userDictList.getModel().getSize() == 0) {
            // Nothing to do.
        }

        for (int i = 0; i < userDictList.getModel().getSize(); i++) {
            Log.print("dict=" + userDictList.getModel().getElementAt(i));
        }
        // Add the word in the problem field to the selected lexicon (or the
        // first lexicon if none is selected)
        //int pos = userDictList.getSelectedIndex();
        //if (pos < 0) {
        int pos = 0;
        //}
        FileTextLexicon lex =
                findLex((String) userDictList.getModel().getElementAt(pos));
        if (lex != null) {
            String word = problemTextFld.getText();
            if (speller.getOption(speller.STRIP_POSSESSIVES_OPT)) {
                word = speller.stripPossessives(word);
            }
            try {
                lex.addWord(word);
            } catch (Exception e) {
                MessageBox.createMessageBox(getTitle(),
                        "Error adding word to dictionary: " + e);
            }
        }
        parser.nextWord();
        runChecker();
    }

    void cancelButton_actionPerformed(java.awt.event.ActionEvent event) {
        canceled = true;
        setVisible(false);
        dispose();
    }

    void undoButton_actionPerformed(java.awt.event.ActionEvent event) {
        // Undo the last change
        undo();
        runChecker();
    }

    void checkStartDelayTimer_actionPerformed(java.awt.event.ActionEvent event) {
        // Start checking.
        runChecker();
    }

    class SymListSelection implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent event) {
            try {
                Object object = event.getSource();
                if (object == suggestionList) {
                    suggestionList_valueChanged(event);
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    }

    void suggestionList_valueChanged(ListSelectionEvent event) {
        int newSel = suggestionList.getSelectedIndex();
        if (newSel >= 0) {
            // The change-to word becomes the selected suggestion.
            changeToTextFld.setText((String) suggestionListModel.getElementAt(newSel));
        }
    }
}

/**
 * Information recorded about an undo-able change.
 */
class UndoRecord {

    /**
     * Location of the 1st changed character within the text.
     */
    int offset;
    /**
     * Original text (before the change).
     */
    String origText;
    /**
     * New text inserted by the change.
     */
    String newText;
}
