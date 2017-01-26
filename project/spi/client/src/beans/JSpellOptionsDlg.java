/*
 * $Header: /home/common/cvsarea/ibase/dia/src/beans/JSpellOptionsDlg.java,v 1.1 2004/08/06 16:46:29 nancy Exp $
 */
// Sentry Spelling Checker Engine
// SwingDemo.JSpellOptionsDlg
//
// Copyright (c) 1999 Wintertree Software Inc. All rights reserved.
// www.wintertree-software.com
//
// Use, duplication, and disclosure of this file is governed
// by a license agreement between Wintertree Software Inc. and
// the licensee.
package beans;

import com.lexpar.util.Log;
import com.wintertree.ssce.SpellingSession;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

/**
 * Interact with the user to set SpellingSession options.
 *
 * @version $Id: JSpellOptionsDlg.java,v 1.1 2004/08/06 16:46:29 nancy Exp $
 * @see SpellingSession
 */
public class JSpellOptionsDlg extends JDialog {

    /**
     * Interact with the user to set SpellingSession options.
     * @param parent The parent frame
     * @param session The spelling session whose options are to be set
     */
    public JSpellOptionsDlg(Frame parent, SpellingSession session) {
        super(parent, true);

        //{{INIT_CONTROLS
        setTitle("Spelling preferences");
        setResizable(false);
        getContentPane().setLayout(null);
        setSize(405, 273);
        setVisible(false);
        ignoreCapitalizedWordsCkb.setText("Ignore capitalized words (e.g., Canada)");
        ignoreCapitalizedWordsCkb.setActionCommand("Ignore capitalized words (e.g., Canada)");
        ignoreCapitalizedWordsCkb.setMnemonic((int) 'C');
        getContentPane().add(ignoreCapitalizedWordsCkb);
        ignoreCapitalizedWordsCkb.setFont(new Font("Dialog", Font.PLAIN, 12));
        ignoreCapitalizedWordsCkb.setBounds(8, 32, 368, 24);
        ignoreAllCapsWordsCkb.setText("Ignore all-caps words (e.g., ASAP)");
        ignoreAllCapsWordsCkb.setActionCommand("Ignore all-caps words (e.g., ASAP)");
        ignoreAllCapsWordsCkb.setMnemonic((int) 'A');
        getContentPane().add(ignoreAllCapsWordsCkb);
        ignoreAllCapsWordsCkb.setFont(new Font("Dialog", Font.PLAIN, 12));
        ignoreAllCapsWordsCkb.setBounds(8, 8, 368, 24);
        ignoreWordsWithNumbersCkb.setText("Ignore words with numbers (e.g., Y2K)");
        ignoreWordsWithNumbersCkb.setActionCommand("Ignore words with numbers (e.g., Y2K)");
        ignoreWordsWithNumbersCkb.setMnemonic((int) 'N');
        getContentPane().add(ignoreWordsWithNumbersCkb);
        ignoreWordsWithNumbersCkb.setFont(new Font("Dialog", Font.PLAIN, 12));
        ignoreWordsWithNumbersCkb.setBounds(8, 56, 368, 24);
        ignoreMixedCaseWordsCkb.setText("Ignore words with mixed case (e.g., SuperBase)");
        ignoreMixedCaseWordsCkb.setActionCommand("Ignore words with mixed case (e.g., SuperBase)");
        ignoreMixedCaseWordsCkb.setMnemonic((int) 'M');
        getContentPane().add(ignoreMixedCaseWordsCkb);
        ignoreMixedCaseWordsCkb.setFont(new Font("Dialog", Font.PLAIN, 12));
        ignoreMixedCaseWordsCkb.setBounds(8, 80, 376, 24);
        ignoreDomainNamesCkb.setText("Ignore domain names (e.g., wintertreesoftware.com)");
        ignoreDomainNamesCkb.setActionCommand("Ignore domain names (e.g., wintertreesoftware.com)");
        ignoreDomainNamesCkb.setMnemonic((int) 'I');
        getContentPane().add(ignoreDomainNamesCkb);
        ignoreDomainNamesCkb.setFont(new Font("Dialog", Font.PLAIN, 12));
        ignoreDomainNamesCkb.setBounds(8, 104, 384, 24);
        reportDoubledWordsCkb.setText("Report doubled words (e.g., the the)");
        reportDoubledWordsCkb.setActionCommand("Report doubled words (e.g., the the)");
        reportDoubledWordsCkb.setMnemonic((int) 'D');
        getContentPane().add(reportDoubledWordsCkb);
        reportDoubledWordsCkb.setFont(new Font("Dialog", Font.PLAIN, 12));
        reportDoubledWordsCkb.setBounds(8, 128, 384, 24);
        caseSensitiveCkb.setText("Case sensitive");
        caseSensitiveCkb.setActionCommand("Case sensitive");
        caseSensitiveCkb.setMnemonic((int) 'E');
        getContentPane().add(caseSensitiveCkb);
        caseSensitiveCkb.setFont(new Font("Dialog", Font.PLAIN, 12));
        caseSensitiveCkb.setBounds(8, 152, 384, 24);
        okBtn.setText("OK");
        okBtn.setActionCommand("OK");
        okBtn.setMnemonic((int) 'O');
        getContentPane().add(okBtn);
        okBtn.setFont(new Font("Dialog", Font.PLAIN, 12));
        okBtn.setBounds(104, 216, 88, 40);
        cancelBtn.setText("Cancel");
        cancelBtn.setActionCommand("Cancel");
        getContentPane().add(cancelBtn);
        cancelBtn.setFont(new Font("Dialog", Font.PLAIN, 12));
        cancelBtn.setBounds(216, 216, 88, 38);
        suggestSplitWordsCkb.setText("Suggest split words");
        suggestSplitWordsCkb.setActionCommand("Suggest split words");
        getContentPane().add(suggestSplitWordsCkb);
        suggestSplitWordsCkb.setFont(new Font("Dialog", Font.PLAIN, 12));
        suggestSplitWordsCkb.setBounds(8, 176, 384, 24);
        //}}

        //{{REGISTER_LISTENERS
        SymAction lSymAction = new SymAction();
        okBtn.addActionListener(lSymAction);
        cancelBtn.addActionListener(lSymAction);
        //}}

        ssce = session;

        caseSensitiveCkb.setSelected(ssce.getOption(ssce.CASE_SENSITIVE_OPT));
        ignoreAllCapsWordsCkb.setSelected(ssce.getOption(ssce.IGNORE_ALL_CAPS_WORD_OPT));
        ignoreCapitalizedWordsCkb.setSelected(ssce.getOption(ssce.IGNORE_CAPPED_WORD_OPT));
        ignoreMixedCaseWordsCkb.setSelected(ssce.getOption(ssce.IGNORE_MIXED_CASE_OPT));
        ignoreWordsWithNumbersCkb.setSelected(ssce.getOption(ssce.IGNORE_MIXED_DIGITS_OPT));
        ignoreDomainNamesCkb.setSelected(ssce.getOption(ssce.IGNORE_DOMAIN_NAMES_OPT));
        reportDoubledWordsCkb.setSelected(ssce.getOption(ssce.REPORT_DOUBLED_WORD_OPT));
        suggestSplitWordsCkb.setSelected(ssce.getOption(ssce.SUGGEST_SPLIT_WORDS_OPT));
    }

    public void setVisible(boolean b) {
        if (b) {
            setLocation(50, 50);
        }
        super.setVisible(b);
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

    // Used by addNotify
    boolean frameSizeAdjusted = false;

    //{{DECLARE_CONTROLS
    JCheckBox ignoreCapitalizedWordsCkb = new JCheckBox();
    JCheckBox ignoreAllCapsWordsCkb = new JCheckBox();
    JCheckBox ignoreWordsWithNumbersCkb = new JCheckBox();
    JCheckBox ignoreMixedCaseWordsCkb = new JCheckBox();
    JCheckBox ignoreDomainNamesCkb = new JCheckBox();
    JCheckBox reportDoubledWordsCkb = new JCheckBox();
    JCheckBox caseSensitiveCkb = new JCheckBox();
    JButton okBtn = new JButton();
    JButton cancelBtn = new JButton();
    JCheckBox suggestSplitWordsCkb = new JCheckBox();
    //}}
    /**
     * Spelling session being edited
     */
    protected SpellingSession ssce;

    class SymAction implements java.awt.event.ActionListener {

        public void actionPerformed(java.awt.event.ActionEvent event) {
            try {
                Object object = event.getSource();
                if (object == okBtn) {
                    okBtn_actionPerformed(event);
                } else if (object == cancelBtn) {
                    cancelBtn_actionPerformed(event);
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    }

    void okBtn_actionPerformed(java.awt.event.ActionEvent event) {
        // Apply the option settings, then close.
        ssce.setOption(SpellingSession.CASE_SENSITIVE_OPT, caseSensitiveCkb.isSelected());
        ssce.setOption(SpellingSession.IGNORE_ALL_CAPS_WORD_OPT, ignoreAllCapsWordsCkb.isSelected());
        ssce.setOption(SpellingSession.IGNORE_CAPPED_WORD_OPT, ignoreCapitalizedWordsCkb.isSelected());
        ssce.setOption(SpellingSession.IGNORE_MIXED_CASE_OPT, ignoreMixedCaseWordsCkb.isSelected());
        ssce.setOption(SpellingSession.IGNORE_MIXED_DIGITS_OPT, ignoreWordsWithNumbersCkb.isSelected());
        ssce.setOption(SpellingSession.IGNORE_DOMAIN_NAMES_OPT, ignoreDomainNamesCkb.isSelected());
        ssce.setOption(SpellingSession.REPORT_DOUBLED_WORD_OPT, reportDoubledWordsCkb.isSelected());
        ssce.setOption(SpellingSession.SUGGEST_SPLIT_WORDS_OPT, suggestSplitWordsCkb.isSelected());
        setVisible(false);
        dispose();
    }

    void cancelBtn_actionPerformed(java.awt.event.ActionEvent event) {
        // Close without saving.
        setVisible(false);
        dispose();
    }
}
