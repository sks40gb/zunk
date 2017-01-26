/* $Header: /home/common/cvsarea/ibase/dia/src/common/msg/MessageConstants.java,v 1.102.2.11 2006/08/23 18:34:22 nancy Exp $ */
package com.fossa.servlet.common;

import java.nio.charset.Charset;

/**
 * Defines constants for message tags and attributes.
 */
public interface MessageConstants {

    final public static Charset UTF_8 = Charset.forName("UTF-8");
    final public static String A_LISTING_ID = "listing_id";
    final public static String A_USER_NAME = "user_name";
    final public static String T_REPLY_IS_ASSIGNED_LISTING_QC = "reply_is_assigned_listing_qc";
    final public static String A_OK_LISTING = "ok_listing";
    final public static String A_OK_LISTING_QC = "ok_listing_qc";
    final public static String A_OK_TALLY = "ok_tally";
    
    // Definitions of XML tags

    final public static String T_MESSAGE = "message";
    final public static String T_HELLO = "hello";
    final public static String T_ACCEPT = "accept";
    final public static String T_GOODBYE = "goodbye";
    final public static String T_DISCONNECT = "disconnect";
    final public static String T_OK = "ok";
    final public static String T_FAIL = "fail";
    final public static String T_PING = "ping";
    final public static String T_SHUTDOWN = "shutdown";
    final public static String T_REQUEST_CODING_VALUES = "request_coding_values";
    final public static String T_PAGE_VALUES = "page_values";
    final public static String T_PAGE_BOUNDARY = "page_boundary";
    final public static String T_PAGE_SPLIT = "page_split";
    final public static String T_PAGEISSUE = "pageissue";
    final public static String T_VALUE_LIST = "value_list";
    final public static String T_ERROR_FLAG_LIST = "error_flag_list";
    final public static String T_VALUE = "value";
    final public static String T_LONGVALUE = "longvalue";
    final public static String T_NAMEVALUE = "namevalue";
    final public static String T_IMAGE_REQUEST = "image_request";
    final public static String T_REQUEST_PAGE = "request_page";
    final public static String T_REQUEST_PAGE_BY_BATES = "request_page_by_bates";
    final public static String T_REQUEST_CODING = "request_coding";
    final public static String T_IMAGE = "image";
    final public static String T_IMAGE_DATA = "image_data";
    final public static String T_CODING_DATA = "coding_data";
    final public static String T_REQUEST_BATCH = "request_batch";
    final public static String T_VALIDATE_BATCH = "validate_batch";
    final public static String T_CLOSE_BATCH = "close_batch";
    final public static String T_COMMENTS = "comments";
    final public static String T_CREATE_CODING_BATCHES = "create_coding_batches";
    final public static String T_BATCH_BOUNDARY = "batch_boundary";
    final public static String T_BATCH_QUEUE = "batch_queue";
    final public static String T_REQUEST_PROJECTFIELDS = "request_projectfields";
    final public static String T_PROJECTFIELDS_DATA = "projectfields_data";
    final public static String T_DELETE_PROJECT = "delete_project";
    final public static String T_DELETE_VOLUME = "delete_volume";
    final public static String T_REQUEST_TABLE_VALUES = "request_table_values";
    final public static String T_TABLESPEC = "tablespec";
    final public static String T_TABLEVALUE = "tablevalue";
    final public static String T_REQUEST_DIRECTORY = "request_directory";
    final public static String T_DIRECTORY = "directory";
    final public static String T_FILENAME = "filename";
    final public static String T_REQUEST_FILE = "request_file";
    final public static String T_FILE_DELTA = "file_delta";
    final public static String T_USERS_DATA = "users_data";
    final public static String T_TEAMS_DATA = "teams_data";
    final public static String T_TEAMSVOLUME = "teamsvolume";
    final public static String T_SQL_QUERY = "sql_query";
    final public static String T_SQL_UPDATE = "sql_update";
    final public static String T_RESULT_SET = "result_set";
    final public static String T_DAILY_TOTAL_REPORT = "daily_total_report";
    final public static String T_PROFIT_DETAIL_REPORT = "profit_detail_report";
    final public static String T_TIMESHEET_REPORT = "timesheet_report";
    final public static String T_OPEN_MANAGED_MODEL = "open_managed_model";
    final public static String T_UPDATE_MANAGED_MODEL = "update_managed_model";
    final public static String T_UPDATE_RESULT = "update_result";
    final public static String T_UPDATE_COUNT = "update_count";
    final public static String T_COLUMN = "column";
    final public static String T_ROW = "row";
    final public static String T_HEADING = "heading";
    final public static String T_PARAMETER = "parameter";
    final public static String T_REQUEST_PROJECT_LIST = "request_project_list";
    final public static String T_OPEN_BATCH = "open_batch";
    final public static String T_OPEN_QA_VOLUME = "open_qa_volume";
    final public static String T_CLOSE_QA = "close_qa";
    final public static String T_SAMPLE_QA = "sample_qa";
    final public static String T_BATCH_OPENED = "batch_opened";
    final public static String T_REQUEST_REQUEUE = "request_requeue";
    final public static String T_TERMINATE_SESSION = "terminate_session";
    final public static String T_UPDATE_VALUES = "update_values";
    final public static String T_BINDER_UPDATE = "binder_update";
    final public static String T_MAILSENT_DATA = "mailsent_data";
    final public static String T_DELETE_MAILSENT = "delete_mailsent";
    final public static String T_MAILRECEIVED_DATA = "mailreceived_data";
    final public static String T_MAIL_DATA = "mail_data";
    final public static String T_REQUEST_MAIL = "request_mail";
    final public static String T_REQUEST_RECIPIENT_LIST = "request_recipient_list";
    final public static String T_DELIMITER_DATA = "delimiter_data";
    final public static String T_EXPORT_DATA = "export_data";
    final public static String T_IMPORT_DATA = "import_data";
    final public static String T_POPULATE_DATA = "populate_data";
    final public static String T_REQUEST_TABLEVALUE_SWEEP = "request_tablevalue_sweep";
    final public static String T_UNITPRICE = "unitprice";
    final public static String T_CUSTOMERPRICE = "customerprice";
    final public static String T_CLOSE_EVENT = "close_event";
    final public static String T_ERROR = "error";
    final public static String T_ADVANCE_VALIDATION = "advance_validation";
    final public static String T_UPDATE = "update";
    final public static String T_USER_INPUT = "user_input";
    final public static String T_ERROR_MESSAGE = "error_message";
    final public static String T_STATUS = "status";
    final public static String T_FIELD_DESCRIPTION = "field_description";
    final public static String T_FIELD_VALUES = "field_values";
    final public static String T_FIELD_NAMES = "field_names";
    final public static String T_VALIDATIONS = "validations";
    final public static String T_VALIDATION_DATA = "validation_data";
    final public static String T_CODING_MANUAL = "coding_manual";
    final public static String T_CODING_MANUAL_PATH = "coding_manual_path";
    final public static String T_EDIT_CODING_MANUAL = "edit_coding_manual";
    final public static String T_EDIT_CODING_MANUAL_DATA = "edit_coding_manual_data";
    final public static String T_CODING_MANUAL_TRACKING = "coding_manual_tracking";
    final public static String T_REQUEST_FIELDVALUE = "task_request_fieldvalue";
    final public static String T_REQUEST_FIELDVALUEDETAILS = "task_request_fieldvalue_details";
    final public static String T_SEND_FIELDVALUEDETAILS = "task_send_fieldvalue_details";
    final public static String T_SAVE_LISTINGREPORT = "task_save_listing_report";
    final public static String T_CREATE_LISTING_BATCH = "task_create_listing_batch";
    final public static String T_LISTING_QUEUE = "task_listing_queue";
    final public static String T_TALLY_QUEUE = "task_tally_queue";
    final public static String T_ADD_VALIDATION_DATA = "task_add_validation_data";
    final public static String T_SAVE_LISTING_QC = "save_listing_qc";
    final public static String T_DELETE_LISTING_QC = "delete_listing_qc";
    final public static String T_IS_ASSIGNED_LISTING_QC = "is_assigned_listing_qc";
    final public static String T_CREATE_TALLY_BATCH = "task_create_tally_batch";
    final public static String T_CLOSE_VOLUME = "close_volume";
    final public static String T_FIELD_VALUE_COUNT = "field_value_count";
    final public static String T_TALLY_QC_DONE = "tally_qc_done";
    final public static String T_CHECK_BATCH_AVAILABLE = "check_batch_available";
    final public static String T_QUERY_DATA = "query_data";
    final public static String T_CHECK_QUERY_RAISED = "check_query_raised";
    final public static String T_YES_QUERY_RAISED = "yes_query_raised";
    final public static String T_UPDATE_QAIR = "update_qair";
    final public static String T_CHECK_LEVEL = "task_check_level";
    final public static String T_BATCH_LEVEL = "batch_level";
    final public static String T_CREATE_MODIFY_ERRORS = "modify_error";
    final public static String T_VIEW_ADVANCE_VALIDATIONS = "task_view_advance_validations";
    final public static String T_EDIT_ADVANCE_VALIDATIONS = "task_edit_advance_validations";
    final public static String T_DELETE_ADVANCE_VALIDATIONS = "task_delete_advance_validations";
    final public static String T_SAVE_LISTING_OCCURRENCE_LIST = "task_save_listing_occurrence_list";
    final public static String T_LISTING_OCCURRENCE_LIST = "task_listing_occurrence_list";
    final public static String T_SAVE_LISTING_MARKING_LIST = "task_save_listing_marking_list";
    final public static String T_LISTING_MARKING_LIST = "task_listing_marking_list";
    final public static String T_START_OTHER_ACTIVITY = "task_start_other_activity";
    final public static String T_SEND_EVENT_BREAK_ID = "task_send_event_break_id";
    final public static String T_STOP_OTHER_ACTIVITY = "task_stop_other_activity";
    final public static String T_DISPLAY_SUMMARY_LIST = "t_display_summary_list";
    final public static String T_IMPORT_FILE = "t_import_file";
    final public static String T_SEND_FILE = "t_send_file";
    final public static String T_GET_CODER_BATCH_INFO = "t_get_coder_batch_info";
    final public static String serverPath = "http://localhost:8080/fossa/UPLOAD/";
    final public static String T_ERROR_TYPE_LIST = "task_error_type_list";
    final public static String T_SEND_PROJECT_PARAMETERS = "task_send_project_parameters";
    final public static String T_ISO_SAMPLING = "task_iso_sampling";
    final public static String TASK_GET_SAMPLING_PARAMETERS = "task_get_sampling_parameters";
    final public static String TASK_SAVE_QAIR_NOTES = "task_save_qair_notes";
    final public static String T_GET_LISTING_QC_FIELD = "task_get_listing_qc_field";
    final public static String T_REQUEST_INVESTIGATION_DATA = "request_investigation_data";
    public static final String T_SEND_POST_VALIDATION = "send_post_validation";
    public static final String T_REQUEST_POST_VALIDATIONS_REPORT = "request_post_validation_report";
    public static final String T_REQUEST_IMPORT_PROGRESS_STATUS = "request_import_progress_status";
    final public static String T_SAVE_DTYG_MOD_DATA = "save_dtyg_mod_data";
    final public static String T_EDIT_PROJECT = "edit_project";
    final public static String T_EDIT_PROJECT_DATA = "task_edit_project_data";
    final public static String T_QA_PR_ASSIGN_USER = "task_qa_pr_assign_user";
    final public static String T_TALLY_PROCESS = "tally_process";
    final public static String T_CHARACTER_SAMPLING = "task_character_sampling";

    //For Adding new project
    final public static String T_ADD_NEW_PROJECT = "task_add_new_project";
    //message constants for qa

    final public static String T_QA_PR_SAMPLED_DOCUMENT = "task_qa_pr_sampled_document";
    final public static String T_QA_PR_GET_FIELDVALUES = "task_qa_pr_get_fieldvalues";
    final public static String T_SAVE_QA_CORRECTION_DATA = "task_save_qa_correction_data";
    final public static String T_QA_PR_CLOSE_GROUP = "task_qa_pr_close_group";
    final public static String T_QA_SAMPLING_REPORT = "task_qa_sampling_report";

    public static final String T_REQUEST_OUTPUT_FORMAT = "request_output_format";
    public static final String T_SAVE_OUTPUT_FORMAT = "save_output_format";
    final public static String T_TALLY_ASSIGN_USER = "task_tally_assign_user";
    final public static String T_SERVER_QUEUE = "server_queue";
    final public static String T_SERVER_QUEUE_ID = "server_queue_id";
    
    // Definitions of XML attributess
    final public static String A_SERVER_QUEUE_ID = "serverqueue_id";
    final public static String A_USER_INPUT = "user_input";
    final public static String A_FIELD_ID = "field_id";
    final public static String A_LEVEL = "level";
    final public static String A_STD_FIELD_ID = "standard_field_id";
    final public static String A_PATH = "path";
    final public static String A_START_DATE = "start_date";
    final public static String A_END_DATE = "end_date";
    final public static String A_FIELD_NAME = "fieldName";
    final public static String A_FIELD_VALUE = "fieldValue";
    final public static String A_NEW_FIELD_VALUE = "newfieldValue";
    final public static String A_BATE = "bate";
    final public static String A_CONDITION = "condition";
    final public static String A_ERRORTYPE = "errorType";
    final public static String A_QAIR_NUMBER = "qair_number";
    final public static String A_MISCODED = "miscoded";
    final public static String A_UNCODED = "uncoded";
    final public static String A_ADDED = "added";
    final public static String A_EDIT_OR_DISPLAY = "edit_or_display";
    final public static String A_VALIDATION_MAPPING_DETAILS_ID = "validation_mapping_details_id";
    final public static String A_VALIDATION_FUNCTIONS_MASTER_ID = "validation_functions_master_id";
    final public static String A_FUNCTION_NAME = "function_name";
    final public static String A_DESCRIPTION = "description";
    final public static String A_ERROR_MESSAGE = "error_message";
    final public static String A_PARAMETER = "parameter";
    final public static String A_FUN_STATUS = "fun_status";
    final public static String A_FUN_BODY = "fun_body";
    final public static String A_NOTES = "notes";
    final public static String A_OPEN_TIMESTAMP = "open_timestamp";
    final public static String A_CLOSE_TIMESTAMP = "close_timestamp";
    final public static String A_EVENT_BREAK_ID = "event_break_id";
    final public static String A_FILE = "file";
    final public static String A_ERROR_TYPE = "error_type";
    final public static String A_LOT_SIZE = "lotsize";
    final public static String A_INSPECTION_TYPE = "inspectionType";
    final public static String A_AQL_VALUE = "aqlValue";
    final public static String A_SAMPLE_SIZE = "sample_size";
    final public static String A_ACCEPT_NUMBER = "accept_number";
    final public static String A_REJECT_NUMBER = "reject_number";
    final public static String A_ERROR_COUNT = "error_count";
    final public static String A_SAMPLING_METHOD = "sampling_method";
    final public static String A_SAMPLING_TYPE = "sampling_type";
    final public static String A_ACCURACY = "accuracy";
    final public static String A_CODERS = "coders";
    final public static String A_FIELD_COUNTED = "field_counted";
    final public static String A_TOTAL_DOCS = "total_docs";
    final public static String A_TOTAL_FIELDS = "total_fields";
    final public static String A_SAMPLING_DOCS = "sampling_docs";
    final public static String A_SAMPLING_FIELDS = "sampling_fields";
    final public static String A_SAMPLING_RESULT = "sampling_result";
    final public static String A_QA_LEVEL = "qa_level";
    final public static String A_SAMPLING_ID = "sampling_id";
    final public static String A_PROOF_READ_BY = "proof_read_by";
    final public static String A_FILE_COMPARE_BY = "file_compare_by";
    final public static String A_MDB_CHECK_BY = "mdb_check_by";
    final public static String A_OUTPUT_CHECK_BY = "output_check_by";
    final public static String A_OUTPUT_CHECK_RESULT = "output_check_result";
    final public static String A_OTHER_NON_CONFORMANCE = "other_non_conformance";
    final public static String A_RECOMMENDATIONS = "recommendations";
    final public static String A_PRODUCTION_FACILITY = "production_facility";
    final public static String A_ERRING_SECTION = "erringSection";
    final public static String A_PREPARED_BY_QA_STAFF = "preparedByQAStaff";
    final public static String A_NOTED_BY_QA_SUPV_DATE = "notedByQASupvDate";
    final public static String A_APPROVED_BY_QA_SUPV_DATE = "approvedByQASupvDate";
    final public static String A_PREVIOUS_SUBMISSION_ERROR_CORRECTED = "previousSubmissionErrorCorrected";
    final public static String A_IS_SAMPLING_DONE = "is_sampling_done";
    final public static String A_TAGS_COUNT = "tags_count";
    final public static String A_FOSSAID = "fossaSession_id";
    final public static String A_ID = "id";
    final public static String A_USERS_ID = "users_id";
    final public static String A_VERSION = "version";
    final public static String A_SESSION_KEY = "session_key";
    final public static String A_TIME_ZONE = "time_zone";
    final public static String A_NEW_PASSWORD = "new_password";
    final public static String A_NAME = "name";
    final public static String A_ADMIN = "admin";
    final public static String A_DELETE = "delete";
    final public static String A_OK_UNITIZE = "ok_unitize";
    final public static String A_OK_UQC = "ok_uqc";
    final public static String A_OK_CODING = "ok_coding";
    final public static String A_OK_CODINGQC = "ok_codingqc";
    final public static String A_OK_QA = "ok_qa";
    final public static String A_OK_TEAM_LEADER = "ok_team_leader";
    final public static String A_OK_ADMIN = "ok_admin";
    final public static String A_OK_ADMIN_USERS = "ok_admin_users";
    final public static String A_OK_ADMIN_PROJECT = "ok_admin_project";
    final public static String A_OK_ADMIN_BATCH = "ok_admin_batch";
    final public static String A_OK_ADMIN_EDIT = "ok_admin_edit";
    final public static String A_OK_ADMIN_IMPORT = "ok_admin_import";
    final public static String A_OK_ADMIN_EXPORT = "ok_admin_export";
    final public static String A_OK_ADMIN_PROFIT = "ok_admin_profit";
    final public static String A_SQLSTATE = "sqlstate";
    final public static String A_SQLCODE = "sqlcode";
    final public static String A_RESTART = "restart";
    final public static String A_LENGTH = "length";
    final public static String A_TIME = "time";
    final public static String A_COUNT = "count";
    final public static String A_IS_NULL = "is_null";
    final public static String A_REQUEST_METADATA = "request_metadata";
    final public static String A_REQUEST_ASSIGNMENTS = "request_assignments";
    final public static String A_REQUEST_QUEUES = "request_queues";
    final public static String A_BY_PROJECT = "by_project";
    final public static String A_BY_TEAM = "by_team";
    final public static String A_PROJECT_ID = "project_id";
    final public static String A_PROJECT_NAME = "project_name";
    final public static String A_PAGE_ID = "page_id";
    final public static String A_SEQUENCE = "sequence";
    final public static String A_VOLUME_ID = "volume_id";
    final public static String A_VOLUME_NAME = "volume_name";
    final public static String A_IMAGE_PATH = "image_path";
    final public static String A_RESOLUTION = "resolution";
    final public static String A_OFFSET = "offset";
    final public static String A_TEAM_NAME = "team_name";
    final public static String A_TEAMS_ID = "teams_id";
    final public static String A_BATES_NUMBER = "bates_number";
    final public static String A_BOUNDARY_FLAG = "boundary_flag";
    final public static String A_VOL_FIRST_ID = "vol_first_id";
    final public static String A_VOL_LAST_ID = "vol_last_id";
    final public static String A_CHILD_FIRST_ID = "child_first_id";
    final public static String A_CHILD_LAST_ID = "child_last_id";
    final public static String A_DOCUMENT_FIRST_ID = "document_first_id";
    final public static String A_DOCUMENT_LAST_ID = "document_last_id";
    final public static String A_ISSUE = "issue";
    final public static String A_POSITION_IN_CHILD = "position_in_child";
    final public static String A_DELTA = "delta";
    final public static String A_BOUNDARY = "boundary";
    final public static String A_FIND_LAST = "find_last";
    final public static String A_BATCH_ID = "batch_id";
    final public static String A_GROUP = "group";
    final public static String A_PERCENT = "percent";
    final public static String A_START_ID = "start_id";
    final public static String A_END_ID = "end_id";
    final public static String A_BATCH_NUMBER = "batch_number";
    final public static String A_BATCH_SPAN = "batch_span";
    final public static String A_L1_BATCH_SPAN = "l1_batch_span";
    final public static String A_FORMAT = "format";
    final public static String A_STATUS = "status";
    final public static String A_COMMENTS = "comments";
    final public static String A_REJECT = "reject";
    final public static String A_IS_UNITIZE = "is_unitize";
    final public static String A_OVERWRITE = "overwrite";
    final public static String A_SPLIT_DOCUMENTS = "split_documents";
    final public static String A_FILENAME = "filename";
    final public static String A_BRS_FILENAME = "brs_filename";
    final public static String A_IMG_FILENAME = "img_filename";
    final public static String A_TXT_FILENAME = "txt_filename";
    final public static String A_IS_L1_OPERATION = "is_l1_operation";
    final public static String A_IS_UNITIZE_OPTIONAL = "is_unitize_optional";
    final public static String A_INTERNAL_VOLUME = "is_internal_volume";
    final public static String A_VOLUME_COMPLETION_DATE = "volume_completion_date";
    final public static String A_PATHNAME = "pathname";
    final public static String A_MIN_VERSION = "min_version";
    final public static String A_MAX_VERSION = "max_version";
    final public static String A_DATA = "data";
    final public static String A_TYPE = "type";
    final public static String A_HOURS = "hours";
    final public static String A_OLD_DATA = "old_data";
    final public static String A_REMOVE = "remove";
    final public static String A_CLONE = "clone";
    final public static String A_UNSPLIT = "unsplit";
    final public static String A_NEW_SAMPLE = "new_sample";
    final public static String A_TABLESPEC_ID = "tablespec_id";
    final public static String A_GROUP_NUMBER = "group_number";
    final public static String A_SELECTED_FIELD_NAME = "field_name";
    final public static String A_ACCURACY_REQUIRED = "accuracyRequired";
    final public static String A_SAMPLING_FOR = "sampling_for";
    final public static String A_REQUIRED_ACCURACY = "required_accuracy";
    final public static String T_SAMPLE_FIXED_PERCENTAGE = "t_sample_fixed_percentage";
    // Definitions of boundary levels (correspond to enum in table `page`)
    //final public static int B_HIDDEN = 1;
    //final public static int B_NONE = 1;

    //PVR implemetion start

    public static final String A_POST_VALIDATION_STR = "post_validation_fieldIdWithFunctionsIds";
    public static final String A_IMPORT_PROGRESS_STATUS = "import_progress_status";
    public static final String A_IMPORT_ERROR_MESSAGE = "import_error_message";
    //end

    final public static int B_NONE = 1;
    //final public static int B_ADDED = 3;

    final public static int B_CHILD = 2;
    // Indicates an UNCODED child

    final public static int B_UNCODED = -B_CHILD;
    final public static int B_RANGE = 3;
    //final public static int B_BATCH = 4;

    // Flags for boundary info returned to client -- for left panel

    final public static int FIRST_CHILD_OF_BATCH = 0x0001;
    final public static int LAST_CHILD_OF_BATCH = 0x0002;
    final public static int FIRST_PAGE_OF_BATCH = 0x0004;
    final public static int LAST_PAGE_OF_BATCH = 0x0008;

    // Flags for boundary info returned to client -- for right panel

    final public static int FIRST_RANGE_OF_VOLUME = 0x0010;
    final public static int LAST_RANGE_OF_VOLUME = 0x0020;
    final public static int FIRST_CHILD_OF_VOLUME = 0x0040;
    final public static int LAST_CHILD_OF_VOLUME = 0x0080;
    final public static int FIRST_PAGE_OF_VOLUME = 0x0100;
    final public static int LAST_PAGE_OF_VOLUME = 0x0200;
    final public static int FIRST_PAGE_OF_CHILD = 0x0400;
    final public static int LAST_PAGE_OF_CHILD = 0x0800;

    /**
     * Magic number to indicate start of image in stream.
     */
    final public static short IMAGE_MAGIC = (short) 0xBE01;

    // Resolution levels

    final public static int RES_HIGH = 0;
    final public static int RES_MEDIUM = 1;
    final public static int RES_LOW = 2;
    final public static int RES_DRAFT = 3;
    final public static String A_SAMPLING_VALUE = "samplingValue";
    final public static String A_TALLY_TYPE = "tallytype";
    final public static String A_FIELD_TYPE = "fieldType";
    final public static String A_PROJECT_FIELD_ID = "projectFieldId";
    final public static String E_FIELD_NAMES = "field_names";
    final public static String E_FIELD_NAME = "field_name";
    final public static String A_FIELD_INDEX_SIZE = "field_index_size";
    final public static String T_QA_GROUP = "task_qa_Group";

    final public static String A_CHILD_ID = "child_id";
    
    //common constants for QA - Proof Reading
    final public static String A_CODED_DATA = "coded_data";
    final public static String A_CORRECTION_DATA = "correction_data";
    final public static String A_TAG_SEQUENCE = "tag_sequence";
    final public static String A_CORRECTION_TYPE = "correction_type";
    final public static String A_CREATED_DATE = "created_date";
    
    //common constants for QASampling Report
    final public static String A_CORRECTION_DATA_COUNT = "correction_data_count";
    final public static String A_DOCUMENT_COUNT = "document_count";
    final public static String A_FIELD_COUNT = "field_count";
    final public static String A_TAG_COUNT = "tag_count";
    final public static String A_SAMPLED_DOCUMENT_COUNT = "sampled_document_count";
    final public static String A_SAMPLED_FIELD_COUNT = "sampled_field_count";
    final public static String A_SAMPLED_TAG_COUNT = "sampled_tag_count";
    final public static String A_NUMBER_OF_DOCUMENT_WITH_ERROR = "number_of_document_with_error";
    final public static String A_NUMBER_OF_FIELDS_WITH_ERROR = "number_of_fields_with_error";
    final public static String A_NUMBER_OF_TAGS_WITH_ERROR = "number_of_tags_with_error";
    final public static String A_NUMBER_OF_MISCODED_FIELDS = "number_of_miscoded_fields";
    final public static String A_NUMBER_OF_UNCODED_FIELDS = "number_of_uncoded_fields";
    final public static String A_NUMBER_OF_ADDED_FIELDS = "number_of_added_fields";
    final public static String A_FUNCTION_SCOPE = "function_scope";

    final public static String A_INSERT_PROCESS = "isInsertProcess";
    final public static String A_TALLY_GROUP_ID = "tally_dictionary_group_id";
    final public static String A_TALLY_ASSIGNMENT_ID = "tally_assignment_id";
}
