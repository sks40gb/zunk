/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LUndoController.java,v 1.12 2004/10/11 15:23:47 nancy Exp $ */
package beans;

import com.lexpar.util.Log;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * An undo manager shared by multiple components and that allows
 * all undos and redos to set focus to the affected JComponent.
 */
public class LUndoController {

    /** control debugging for undo */
    final public static boolean DEBUG = false;

    // TODO -- What should the undo limit be????  Maybe get the value from properties.
    final private static int UNDO_LIMIT = 1000;
    private UndoManager manager = new UndoManager();
    public UndoAction undoAction = new UndoAction();
    public RedoAction redoAction = new RedoAction();
    private UndoableEditListener editListener = null;

    /**
     * Create a new LUndoController.
     */
    public LUndoController() {
        manager.setLimit(UNDO_LIMIT);
    }

    /**
     * Register a document with the controller so that all
     * undoable edit events fired by the document will be handled
     * automatically.
     * @param document The document on which to listen for undoable edits.
     * @param component The component to focus on undo action.
     */
    public void registerDocument(Document document,
            final JComponent component) {
        editListener = new UndoableEditListener() {

            public void undoableEditHappened(UndoableEditEvent e) {
                try {
                    //Log.print("(LUndoController).undoableEditHappened " + e.toString());
                    if (((JTextField) component).getDocument() instanceof IbaseTextField.CheckedDocument) {
                        if (DEBUG) {
                            Log.print("(LUndoController.undoableEditHappened) " + ((IbaseTextField.CheckedDocument) ((JTextField) component).getDocument()).isSignificant());
                        }
                        if (((IbaseTextField.CheckedDocument) ((JTextField) component).getDocument()).isSignificant()) {
                            manager.addEdit(new DocumentEdit(e.getEdit(),
                                    new FocusInfo(component)));
                            undoAction.updateUndoState();
                        }
                    } else {
                        if (DEBUG) {
                            Log.print("(LUndoController.undoableEditHappened) not ibaseTextField " + true);
                        }
                        manager.addEdit(new DocumentEdit(e.getEdit(),
                                new FocusInfo(component)));
                        undoAction.updateUndoState();
                    }
                    redoAction.updateRedoState();
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        };
        document.addUndoableEditListener(editListener);
    }

    public UndoableEditListener getUndoableEditListener() {
        //Log.print("(LUndoCotnroller.getUndoableEditListener) " + editListener);
        return editListener;
    }

    class UndoAction extends AbstractAction {

        public UndoAction() {
            super("Undo");
            //Log.print("(LUndoController).UndoAction");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                //Log.print("(LUndoController).undoAction.actionPerformed "
                //          + manager.getUndoPresentationName());
                manager.undo();
            } catch (CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();
        }

        protected void updateUndoState() {
            if (manager.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, manager.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    class RedoAction extends AbstractAction {

        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                manager.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }

        protected void updateRedoState() {
            if (manager.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, manager.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }

    public AbstractAction getUndoAction() {
        return undoAction;
    }

    public AbstractAction getRedoAction() {
        return redoAction;
    }

    /**
     * Clear the undo buffer of edits.
     */
    public void discardAllEdits() {
        //Log.print("(LUndoController).discardAllEdits");
        manager.discardAllEdits();
        undoAction.setEnabled(false);
        redoAction.setEnabled(false);
    }

    /**
     * Remove the last edit from the undo queue.
     */
    public void die() {
        //Log.print("(LUndoController).die " + manager.getUndoPresentationName());
        manager.die();
    }

    /**
     * An inner class for collecting information necessary to focus a
     * component.
     */
    public static class FocusInfo {

        private JComponent component;

        public FocusInfo(JComponent c) {
            this.component = c;
        }

        public void doFocus() {
            if (component != null) {
                component.requestFocus();
            }
        }

        public boolean isSignificant() {
            if (DEBUG) {
                Log.print("(LUC.FocusInfo.isSignificant) component is " + component.getClass());
            }
            return true;
        }
    }
}

/**
 * An edit object that stores a component to which focus can be returned.
 * Make subclasses of this class and arrange for their undo
 * and redo methods to call super.undo() and super.redo().
 */
class ComponentAwareEdit extends AbstractUndoableEdit {

    private LUndoController.FocusInfo focusInfo;

    public ComponentAwareEdit(LUndoController.FocusInfo focusInfo) {
        this.focusInfo = focusInfo;
    }

    public void undo() throws CannotUndoException {
        focusInfo.doFocus();
    }

    public void redo() throws CannotRedoException {
        focusInfo.doFocus();
    }

    public boolean canUndo() {
        return true;
    }

    public boolean canRedo() {
        return true;
    }
}

/**
 * A wrapper edit object that encapsulates another edit.
 * Whenever this edit is undone or redone, it first sets focus
 * as directed by a FocusInfo object then undoes or redoes by
 * delegating to the internal edit object.
 */
class DocumentEdit extends ComponentAwareEdit {

    private UndoableEdit edit;
    private LUndoController.FocusInfo focusInfo;

    public DocumentEdit(UndoableEdit edit,
            LUndoController.FocusInfo focusInfo) {
        super(focusInfo);
        this.edit = edit;
        this.focusInfo = focusInfo;
    }

    public void undo() throws CannotUndoException {
        super.undo();
        if (edit.canUndo()) {
            edit.undo();
        }
    }

    public void redo() throws CannotRedoException {
        super.redo();
        if (edit.canRedo()) {
            edit.redo();
        }
    }

    public boolean isSignificant() {
        if (LUndoController.DEBUG) {
            Log.print("(LUndoController.DocumentEdit.isSignificant) " + focusInfo.isSignificant());
        }
        return true;
    }
}

