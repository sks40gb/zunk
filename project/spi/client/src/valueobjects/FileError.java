/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package valueobjects;

/**
 *
 * @author sunil
 */

public class FileError
{
    public FileError(String errorType, String error)
    {     
        this.errorType = errorType;
        this.error = error;
    }    
    private String errorType;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
