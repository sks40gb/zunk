/*
 * CommandFactory.java
 *
 * Created on 13 November, 2007, 3:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.MessageConstants;

/**
 * This class invokes the respective class based on the action name.
 * @author prakash
 */
public class CommandFactory implements MessageConstants {

    private static CommandFactory instance = null;

    public static CommandFactory getInstance() {
        if (null == instance) {
            instance = new CommandFactory();
        }
        return instance;
    }

    /**
     * This method returns the particular Command file based on action
     * @param actionName : is the action requests made by the client
     * @return corresponding Command object
     */
    public Command getCommand(String actionName) {
        if (actionName.equalsIgnoreCase(T_HELLO)) {
            return new Command_hello();
        } else if (actionName.equalsIgnoreCase(T_SQL_QUERY)) {
            return new Command_sql_query();
        } else if (actionName.equalsIgnoreCase(T_BATCH_BOUNDARY)) {
            return new Command_batch_boundary();
        } else if (actionName.equalsIgnoreCase(T_BATCH_QUEUE)) {
            return new Command_batch_queue();
        } else if (actionName.equalsIgnoreCase(T_BINDER_UPDATE)) {
            return new Command_binder_update();
        } else if (actionName.equalsIgnoreCase(T_CLOSE_BATCH)) {
            return new Command_close_batch();
        } else if (actionName.equalsIgnoreCase(T_CLOSE_EVENT)) {
            return new Command_close_event();
        } else if (actionName.equalsIgnoreCase(T_CLOSE_QA)) {
            return new Command_close_qa();
        } else if (actionName.equalsIgnoreCase(T_CREATE_CODING_BATCHES)) {
            return new Command_create_coding_batches();
        } else if (actionName.equalsIgnoreCase(T_CUSTOMERPRICE)) {
            return new Command_customerprice();
        } else if (actionName.equalsIgnoreCase(T_DAILY_TOTAL_REPORT)) {
            return new Command_daily_total_report();
        } else if (actionName.equalsIgnoreCase(T_DELETE_MAILSENT)) {
            return new Command_delete_mailsent();
        } else if (actionName.equalsIgnoreCase(T_DELETE_PROJECT)) {
            return new Command_delete_project();
        } else if (actionName.equalsIgnoreCase(T_DELETE_VOLUME)) {
            return new Command_delete_volume();
        } else if (actionName.equalsIgnoreCase(T_DELIMITER_DATA)) {
            return new Command_delimiter_data();
        } else if (actionName.equalsIgnoreCase(T_GOODBYE)) {
            return new Command_goodbye();
        } else if (actionName.equalsIgnoreCase(T_IMPORT_DATA)) {
            return new Command_import_data();
        } else if (actionName.equalsIgnoreCase(T_MAILRECEIVED_DATA)) {
            return new Command_mailreceived_data();
        } else if (actionName.equalsIgnoreCase(T_MAILSENT_DATA)) {
            return new Command_mailsent_data();
        } else if (actionName.equalsIgnoreCase(T_OPEN_BATCH)) {
            return new Command_open_batch();
        } else if (actionName.equalsIgnoreCase(T_OPEN_MANAGED_MODEL)) {
            return new Command_open_managed_model();
        } else if (actionName.equalsIgnoreCase(T_OPEN_QA_VOLUME)) {
            return new Command_open_qa_volume();
        } else if (actionName.equalsIgnoreCase(T_PAGE_BOUNDARY)) {
            return new Command_page_boundary();
        } else if (actionName.equalsIgnoreCase(T_PAGE_SPLIT)) {
            return new Command_page_split();
        } else if (actionName.equalsIgnoreCase(T_PAGE_VALUES)) {
            return new Command_page_values();
        } else if (actionName.equalsIgnoreCase(T_PAGEISSUE)) {
            return new Command_pageissue();
        } else if (actionName.equalsIgnoreCase(T_PING)) {
            return new Command_ping();
        } else if (actionName.equalsIgnoreCase(T_POPULATE_DATA)) {
            return new Command_populate_data();
        } else if (actionName.equalsIgnoreCase(T_PROJECTFIELDS_DATA)) {
            return new Command_projectfields_data();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_CODING)) {
            return new Command_request_coding();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_CODING_VALUES)) {
            return new Command_request_coding_values();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_DIRECTORY)) {
            return new Command_request_directory();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_FILE)) {
            return new Command_request_file();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_MAIL)) {
            return new Command_request_mail();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_PAGE)) {
            return new Command_request_page();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_PROJECT_LIST)) {
            return new Command_request_project_list();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_PROJECTFIELDS)) {
            return new Command_request_projectfields();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_RECIPIENT_LIST)) {
            return new Command_request_recipient_list();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_REQUEUE)) {
            return new Command_request_requeue();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_TABLEVALUE_SWEEP)) {
            return new Command_request_tablevalue_sweep();
        } else if (actionName.equalsIgnoreCase(T_SAMPLE_QA)) {
            return new Command_sample_qa();
        } else if (actionName.equalsIgnoreCase(T_SHUTDOWN)) {
            return new Command_shutdown();
        } else if (actionName.equalsIgnoreCase(T_SQL_UPDATE)) {
            return new Command_sql_update();
        } else if (actionName.equalsIgnoreCase(T_TABLESPEC)) {
            return new Command_tablespec();
        } else if (actionName.equalsIgnoreCase(T_TABLEVALUE)) {
            return new Command_tablevalue();
        } else if (actionName.equalsIgnoreCase(T_TEAMS_DATA)) {
            return new Command_teams_data();
        } else if (actionName.equalsIgnoreCase(T_TEAMSVOLUME)) {
            return new Command_teamsvolume();
        } else if (actionName.equalsIgnoreCase(T_TERMINATE_SESSION)) {
            return new Command_terminate_session();
        } else if (actionName.equalsIgnoreCase(T_UNITPRICE)) {
            return new Command_unitprice();
        } else if (actionName.equalsIgnoreCase(T_UPDATE_VALUES)) {
            return new Command_update_values();
        } else if (actionName.equalsIgnoreCase(T_USERS_DATA)) {
            return new Command_users_data();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_PAGE_BY_BATES)) {
            return new Command_request_page_by_bates();
        } else if (actionName.equalsIgnoreCase(T_VALIDATE_BATCH)) {
            return new Command_validate_batch();
        } else if (actionName.equalsIgnoreCase(T_FIELD_DESCRIPTION)) {
            return new Command_field_description();
        } else if (actionName.equalsIgnoreCase(T_CODING_MANUAL)) {
            return new Command_coding_manual();
        } else if (actionName.equalsIgnoreCase(T_EDIT_CODING_MANUAL)) {
            return new Command_edit_codingManual();
        } else if (actionName.equalsIgnoreCase(T_EDIT_CODING_MANUAL_DATA)) {
            return new Command_edit_coding_manual_data();
        } else if (actionName.equalsIgnoreCase(T_CODING_MANUAL_TRACKING)) {
            return new Command_Coding_Manual_Tracking();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_FIELDVALUE)) {
            return new Command_request_fieldvalue();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_FIELDVALUEDETAILS)) {
            return new Command_request_fieldvalue_details();
        } else if (actionName.equalsIgnoreCase(T_SEND_FIELDVALUEDETAILS)) {
            return new Command_send_fieldvalue_details();
        } else if (actionName.equalsIgnoreCase(T_SAVE_LISTINGREPORT)) {
            return new Command_save_listing_report();
        } else if (actionName.equalsIgnoreCase(T_CREATE_LISTING_BATCH)) {
            return new Command_create_listing_batches();
        } else if (actionName.equalsIgnoreCase(T_LISTING_QUEUE)) {
            return new Command_listing_queue();
        } else if (actionName.equalsIgnoreCase(T_ADD_VALIDATION_DATA)) {
            return new Command_add_validation_data();
        } else if (actionName.equalsIgnoreCase(T_SAVE_LISTING_QC)) {
            return new Command_save_listing_qc();
        } else if (actionName.equalsIgnoreCase(T_DELETE_LISTING_QC)) {
            return new Command_delete_listing_qc();
        } else if (actionName.equalsIgnoreCase(T_IS_ASSIGNED_LISTING_QC)) {
            return new Command_is_assigned_for_listingQc();
        } else if (actionName.equalsIgnoreCase(T_CREATE_TALLY_BATCH)) {
            return new Command_create_tally_batches();
        } else if (actionName.equalsIgnoreCase(T_TALLY_QUEUE)) {
            return new Command_tally_queue();
        } else if (actionName.equalsIgnoreCase(T_CLOSE_VOLUME)) {
            return new Command_close_volume();
        } else if (actionName.equalsIgnoreCase(T_FIELD_VALUE_COUNT)) {
            return new Command_field_value_count();
        } else if (actionName.equalsIgnoreCase(T_TALLY_QC_DONE)) {
            return new Command_qc_done();
        } else if (actionName.equalsIgnoreCase(T_CHECK_BATCH_AVAILABLE)) {
            return new Command_check_batch_available();
        } else if (actionName.equalsIgnoreCase(T_ADD_VALIDATION_DATA)) {
            return new Command_add_validation_data();
        } else if (actionName.equalsIgnoreCase(T_VIEW_ADVANCE_VALIDATIONS)) {
            return new Command_view_advance_validations();
        } else if (actionName.equalsIgnoreCase(T_EDIT_ADVANCE_VALIDATIONS)) {
            return new Command_edit_advance_validations();
        } else if (actionName.equalsIgnoreCase(T_DELETE_ADVANCE_VALIDATIONS)) {
            return new Command_delete_advance_validations();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_INVESTIGATION_DATA)) {
            return new Command_request_investigation_data();
        } else if (actionName.equalsIgnoreCase(T_START_OTHER_ACTIVITY)) {
            return new Command_start_other_activity();
        } else if (actionName.equalsIgnoreCase(T_STOP_OTHER_ACTIVITY)) {
            return new Command_stop_other_activity();
        } else if (actionName.equalsIgnoreCase(T_DISPLAY_SUMMARY_LIST)) {
            return new Command_display_summary_list();
        } else if (actionName.equalsIgnoreCase(T_QUERY_DATA)) {
            return new Command_query_data();
        } else if (actionName.equalsIgnoreCase(T_CHECK_QUERY_RAISED)) {
            return new Command_check_query_raised();
        } else if (actionName.equalsIgnoreCase(T_CHECK_LEVEL)) {
            return new Command_check_level();
        } else if (actionName.equalsIgnoreCase(T_GET_CODER_BATCH_INFO)) {
            return new Command_get_coder_batch_info();
        } else if (actionName.equalsIgnoreCase(T_SEND_PROJECT_PARAMETERS)) {
            return new Command_send_project_parameters();
        } else if (actionName.equalsIgnoreCase(T_ISO_SAMPLING)) {
            return new Command_iso_sampling();
        } else if (actionName.equalsIgnoreCase(TASK_GET_SAMPLING_PARAMETERS)) {
            return new Command_get_sampling_parameters();
        } else if (actionName.equalsIgnoreCase(TASK_SAVE_QAIR_NOTES)) {
            return new Command_save_qair_notes();
        } else if (actionName.equalsIgnoreCase(T_GET_LISTING_QC_FIELD)) {
            return new Command_get_listing_qc_field();
        } else if (actionName.equalsIgnoreCase(T_CREATE_MODIFY_ERRORS)) {
            return new Command_create_modify_error_batch();
        } else if (actionName.equalsIgnoreCase(T_SEND_POST_VALIDATION)) {
            return new Command_send_post_validation();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_POST_VALIDATIONS_REPORT)) {
            return new Command_request_post_validation_report();
        } else if (actionName.equalsIgnoreCase(T_SAVE_DTYG_MOD_DATA)) {
            return new Command_save_dtyg_mod_data();
        } else if (actionName.equalsIgnoreCase(T_REQUEST_IMPORT_PROGRESS_STATUS)) {
            return new Command_request_import_progress_status();
        } else if (actionName.equalsIgnoreCase(T_EDIT_PROJECT)) {
            return new Command_edit_project();
        }else if (actionName.equalsIgnoreCase(T_EDIT_PROJECT_DATA)) {
            return new Command_edit_project_data();
        }else if (actionName.equalsIgnoreCase(T_QA_GROUP)) {
            return new Command_List_QA_Groups();
        }else if (actionName.equalsIgnoreCase(T_SAVE_LISTING_OCCURRENCE_LIST)) {
            return new Command_save_listing_occurrence_list();
        }else if (actionName.equalsIgnoreCase(T_SAVE_LISTING_MARKING_LIST)) {
            return new Command_save_listing_marking_list();
        }else if (actionName.equalsIgnoreCase(T_QA_PR_ASSIGN_USER)) {
            return new Command_qa_assign_user();
        }else if (actionName.equalsIgnoreCase(T_QA_PR_SAMPLED_DOCUMENT)) {
            return new Command_show_sampled_document();
        }else if (actionName.equalsIgnoreCase(T_TALLY_PROCESS)) {
            return new Command_tally_process();
        }else if (actionName.equalsIgnoreCase(T_QA_PR_GET_FIELDVALUES)) {
            return new Command_Get_QA_FieldValue();
        }else if (actionName.equalsIgnoreCase(T_SAVE_QA_CORRECTION_DATA)) {
            return new Command_Save_QA_Correction_Data();
        }else if (actionName.equalsIgnoreCase(T_QA_PR_CLOSE_GROUP)) {
            return new Command_QA_Close_Group();
        }else if (actionName.equalsIgnoreCase(T_QA_SAMPLING_REPORT)) {
            return new Command_QA_Inspection_Report();
        }else if (actionName.equalsIgnoreCase(T_ADD_NEW_PROJECT)) {
            return new CommandAddNewProject();
        }else if (actionName.equalsIgnoreCase(T_CHARACTER_SAMPLING)) {
            return new Command_create_char_sampling();
        }else if (actionName.equalsIgnoreCase(T_EXPORT_DATA)) {
            return new Command_export_data(0,null);
        }else if (actionName.equalsIgnoreCase(T_REQUEST_OUTPUT_FORMAT)) {
            return new Command_request_ouput_format();
        }else if (actionName.equalsIgnoreCase(T_SAVE_OUTPUT_FORMAT)) {
            return new Command_save_output_format();
        }else if (actionName.equalsIgnoreCase(T_TALLY_ASSIGN_USER)) {
            return new Command_tally_assign_user();
        }else if (actionName.equalsIgnoreCase(T_SERVER_QUEUE)) {
            return new CommandTaskServerQueue();
        }else {
            return null;
        }
    }
}
