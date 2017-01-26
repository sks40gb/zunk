/* $Header: /home/common/cvsarea/ibase/dia/src/report/DummyReport.java,v 1.1 2004/05/06 14:27:53 weaston Exp $ */
package report;

/**
 * A dummy report specification, with all controls disabled,
 * used for initial currentReport when no report has been
 * selected yet.
 */
final public class DummyReport extends AbstractReport {

    public DummyReport(ReportParameters param) {
        super(param);
        enableControls();
    }
    
    /**
     * Disable all controls.
     */
    public void enableControls() {
        disableControls();
    }

    /** Never called */
    public void generate() {}
}
