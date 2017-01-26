/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hibernate.constant;

/**
 *
 * @author shanmugam
 */
public interface  CommonConstant {
    String USER = "user";
    String USER_TYPE = "userType";
    String USER_ID = "userId";
    String STATUS = "status";
    String FAILURE = "failure";
    String SUCCESS = "success";
    String MESSAGE = "message";
    String UPDATE = "Update";
    String UPDATED = "Updated";
    String IMMEDIATE = "Immediate";
    String APPROVAL = "Approval";
    String PENDING = "pending";
    String CONTACTUS = "contactus";
    String PURCHASED = "purchased";
    String FILE_NAME = "fileName";
    String CONTRACT = "contract";
    String ALL = "All";
    String SEARCH_VALUE = "searchValue";
    String EMPTY = "";
    String NULL = null;
    Double NONE = null;

    //For request
    String USER_DETAILS = "userDetails";
    String USER_DETAIL = "userDetail";
    String WELCOME_NAME = "welcomeName";
    String RECEIVABLE_DETAILS = "receivableDetails";
    String RECEIVABLE_LIST = "receivableList";
    String REGISTERED_USERSLIST = "registeredUsersList";
    String AVAILABLE_ACCOUNTS_LIST = "availableAccountsList";
    String PUCHASED_ACCOUNTS_LIST = "purchasedAccountsList";
    String PUCHASED_DETAIL_LIST = "purchasedDtailList";
    String DIVISION_LIST = "divisionList";
    String AGENCY_LIST = "agencyList";
    

    //Percentage for receivable transaction
    int ADMIN_PERCENTAGE = 2;
    int PROVIDER_PERCENTAGE_APPROVAL = 90;
    int PROVIDER_PERCENTAGE_IMMEDIATE = 75;
    int BALANCE_PERCENTAGE_IMMEDIATE = 25;
    int BALANCE_PERCENTAGE_APPROVAL = 10;
    int ZERO = 0;

    // For Transcation PDF Constants
    String TRANSACTION_ID = "Transaction ID";
    String COMPANY_NAME = "Company Name";
    String REFERENCE_NUMBER = "ReferenceNumber";
    String TRANSACTION_AMT = "Transaction Amt";
    String TRANSACTION_DATE = "Transaction Date";

    //For genericcode
    Integer SALUTATION = 4;
    Integer AGENCY = 2;
    Integer STATE = 1;
    Integer DIVISION = 3;
    Integer TIER = 5;
    Integer PURCHASE_SELECT_OPTION = 6;

    //For Pagination
    String TOTAL_SIZE = "totalSize";
    String TOTAL_PAGES = "totalPages";
    String CURRENT_PAGE_NO = "currentPageNo";
    String CURRENT_PAGE_SIZE = "currentPageSize";
    Integer PAGE_SIZE = 15;
}
