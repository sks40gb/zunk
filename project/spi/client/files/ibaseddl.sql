-- DATABASE DESIGN for i-BASE CODING APPLICATION
-- ============================================
-- $Header: /home/common/cvsarea/ibase/dia/files/ibaseddl.sql,v 1.24 2003/12/07 16:16:43 mcorns Exp $
-- 
-- This document describes the server databases for the iBASE Coding Application. 
-- SQL statements to create the databases are embedded in the text as they are 
-- discussed.
-- 
-- DATABASE and IMAGEBASE
-- ======================
-- 
-- There are two separate databases, one to hold the actual TIFF images, and one to 
-- hold all other data.  `imagedb` is the image database; `codingdb`  is the 
-- database for coding and everything else.
-- 
DROP DATABASE IF EXISTS codingdb;
DROP DATABASE IF EXISTS imagedb;
-- 
CREATE DATABASE imagedb;
CREATE DATABASE codingdb;
-- 
-- The image data is large and changes infrequently, while the coding data is 
-- smaller and changes very rapidly.  Having two separate databases allows for 
-- different backup methods appropriate to the two sets of data.  In addition, 
-- separating the databases allows the image data to be kept on a different 
-- machine, or even multiple machines, if that becomes desirable.
-- 
-- Note:  We have assumed a database to hold the images, rather than a collection 
-- of files.  We have chosen this because it avoids the overhead of opening a file 
-- for each image to be viewed and because it may lead to less complex procedures 
-- for handling images and backing up the imagebase.
-- 
-- THE IMAGEBASE
-- =============
-- 
-- The image database consists of a variable number of tables:
-- -	one table for each volume of data (described as `VolumeName` below)
-- -	one table (`capability`) to hold information to allow a user to request an 
-- encrypted copy of an image.
-- 
-- Assuming that MySQL is used as the database manager, the tables in the imagebase 
-- are MyISAM (non-transactional) tables.  MyISAM tables are maintained as separate 
-- files (actually, three files per table), which can be manipulated as a unit.  In 
-- addition, they should be faster than transactional tables, and there is no 
-- reason that transactions need be used when manipulating the imagebase.
-- 
-- Each volume corresponds to a table of the form given below, with the volume name 
-- as the name of the table.  The `page_id` is a unique image number, which is 
-- assigned when the cross-reference file for the volume is loaded into the coding 
-- database.  All other information about the image is stored in the coding 
-- database
-- 
USE imagedb;
-- 
CREATE TABLE VolumeName
   (
     page_id         INTEGER     PRIMARY KEY,
     image_data      MEDIUMBLOB  NOT NULL
   )   TYPE=MyISAM;
-- 
-- 
-- Images are converted to single-page, TIFF's when they are stored.  Splitting
-- multi-page TIFF's and, possibly, converting from color or JPEG will be 
-- handled in the load process.  Therefore, a page_id corresponds to exactly one 
-- page to be viewed by the user.
-- 
-- The client program requests an image by providing a "capability"--i.e., a unique 
-- integer identifying the desired image and the key with which it will be 
-- encrypted.  Capabilities are generated for all pages of a batch when the batch 
-- is opened, and the corresponding keys will be retained by the associated
-- server process.  If desired, a client program could download and cache multiple 
-- images, which will be encrypted.  As each image is viewed, the key required to 
-- decrypt it must be obtained from the coding server; only one such key will be 
-- maintained on the client machine at a time, so, effectively, only one viewable 
-- image exists on the client machine at a time.  (Note that it is not required to 
-- use this caching capability--and it will not be used in the first version 
-- delivered--but the system should be designed so that caching is possible.)
-- 
-- Note.  "MEDIUMBLOB" allows for images up to 16 MB.  We may need to set MySQL 
-- parameters to allow packets above 1 MB to be transmitted, if images are allowed 
-- to get that big.
-- 
-- The capabilities table:
-- 
CREATE TABLE capability
   (
     capability_id   INTEGER     AUTO_INCREMENT  PRIMARY KEY,
     page_id         INTEGER     NOT NULL,
     volume_name     VARCHAR(40) NOT NULL,
     encrypt_key     VARCHAR(20) BINARY NOT NULL
   )   TYPE=MyISAM;
-- 
-- 
-- BACKING UP THE DATABASES
-- ========================
-- 
-- The coding database, which contains all information entered by the coders and 
-- other users of the system, should be backed up constantly by replicating it to a 
-- machine other than the server, preferably at a different location.  This is 
-- a standard feature of MySQL, and allows a second copy of the database to be kept 
-- in synchronization with the working database, with a delay of at most a few 
-- minutes.
-- 
-- Assuming that MySQL is used, the coding database consists of InnoDB 
-- (transactional) tables.  This allows "atomic" changes to the tables, where a 
-- change is either recorded in its entirety or not at all.  In addition, InnoDB 
-- tables provide for recovery in case of system failure, with the replicated 
-- database as a second line of defense against lost data.
-- 
-- On the other hand, replication should not be used for the image database.  The 
-- VolumeName tables change infrequently; generally, they will be loaded all at 
-- once and never changed until they are deleted from the system.  Therefore, we 
-- recommend that the VolumeName tables be copied to another system by a scheduled 
-- job that runs every night when usage can be expected to be low.  The rsync 
-- program will be used to allow only the changes to the tables to be transmitted 
-- to the backup machine.
-- 
-- The capability table need not be backed up at all, as it will be recreated if 
-- the server is restarted.
-- 
-- Note that the backup machine can be an older, slower machine, as long as it has 
-- sufficient disk space for the backups.
-- 
-- THE CODING DATABASE
-- ===================
-- 
-- The remainder of this document will describe the codingdb database:
-- 
USE codingdb;
-- 
-- TBD:  There should be a way of determining precisely when changes were made to
-- the database, and by whom.  In principle, the log can be used for this.  We
-- probably want to maintain a log as database tables and/or provide for timestamps
-- in the various records.  We have not done this in the present version of this
-- document.
-- 
-- 
-- PROJECTS, VOLUMES and PAGES
-- ========  ======= === =====
-- 
-- Projects consist of volumes, and volumes consist of pages.  Pages correspond to 
-- images in imagedb.  These tables are populated when the cross-reference file for 
-- a volume is read in.
-- 
CREATE TABLE project
   (
     project_id      INTEGER     AUTO_INCREMENT  PRIMARY KEY,
     project_name    VARCHAR(40) NOT NULL
     /* more project information */
   ) TYPE=INNODB;
-- 
CREATE TABLE volume
   (
     volume_id       INTEGER     AUTO_INCREMENT  PRIMARY KEY,
     volume_name     VARCHAR(40) NOT NULL,
     project_id      INTEGER     NOT NULL,
     sequence        SMALLINT    NOT NULL,  /* seq. of vols in proj. */
       UNIQUE (project_id, sequence),
       UNIQUE (volume_name)
   ) TYPE=INNODB;
-- 
--CREATE TABLE page
--   (
--     page_id         INTEGER     AUTO_INCREMENT  PRIMARY KEY,
--     volume_id       INTEGER     NOT NULL,
--     bates_number    VARCHAR(40) NOT NULL,
--     sequence        INTEGER     NOT NULL,  /* order of pages */
--     original_flag   CHAR(1)     NOT NULL DEFAULT ' ',
--     unit_flag       CHAR(1)     NOT NULL DEFAULT ' '
--   ) TYPE=INNODB;
CREATE TABLE page
   (
     volume_id       INTEGER     NOT NULL,
     page_id         INTEGER     UNSIGNED NOT NULL,
     image_id        INTEGER     UNSIGNED NOT NULL,
     bates_number    VARCHAR(40) NOT NULL,
     original_flag   CHAR(1)     NOT NULL DEFAULT ' ',
     original_rotate TINYINT     NOT NULL DEFAULT 0,
     path            VARCHAR(255) NOT NULL,
     filename        VARCHAR(255) NOT NULL,
     file_type       TINYINT     NOT NULL DEFAULT 0,
     boundary_flag   CHAR(1)     NOT NULL DEFAULT ' ',
     rotate          TINYINT     NOT NULL DEFAULT 0,
     boundary        ENUM ("HIDDEN", "NONE", "ADDED", "CHILD", "DOCUMENT") NOT NULL,
       PRIMARY KEY (volume_id, page_id),
       UNIQUE        (volume_id, page_id, boundary),
       UNIQUE        (volume_id, bates_number)
   ) TYPE=INNODB;
--
-- page_id and bates number are unique within a volume_id.  
-- Normally, page_id is a multiple of 1000, starting with 1000.
-- ADDED pages are for multiple coded records referring to the
-- same image; these have page_id's constructed from the base
-- record by adding 1,2,3,...; the Bates numbers have a suffix
-- of "+001","+002",... 
--
-- image_id is normally (page_id / 1000); it is a separate field
-- to allow for multiple pages pointing to the same image.  In
-- particular, this happens in the case of ADDED records.
-- 
-- project/volume/page are set up when the manifest (LFP file) for a volume is 
-- loaded.  The actual images are loaded to imagedb after the manifest is loaded.
-- 
-- original_flag is the unitizing code from the original manifest.  It is not 
-- normally modified after the manifest is loaded.
-- 
-- boundary_flag is the unitizing code assigned by the unitizer.  We chose to keep it as a 
-- code rather than having records by document, as it evidently can be changed, on 
-- occasion, after unitizing.  Also, at the database level, documents and document 
-- ranges will generally be manipulated only as part of a batch or part of a 
-- volume.
--
-- ISSUES
--
-- Corresponding to IS lines in LFP files
-- TBD: How are these used/shown?
--
-- Note: sequence field is to allow more than one issue per page and to allow retrieval
-- in order of entry.
--
CREATE TABLE page_issue
   (
     volume_id       INTEGER     NOT NULL,
     page_id         INTEGER     UNSIGNED NOT NULL,
     sequence        INTEGER     NOT NULL AUTO_INCREMENT,
     issue_name      VARCHAR(40) NOT NULL,
       PRIMARY KEY (volume_id, page_id, sequence),
       UNIQUE KEY (sequence)
   ) TYPE=INNODB;
-- 
-- BATCHES
-- 
-- Volumes get divided into batches after unitizing.
-- 
-- 
-- TBD:  How are batches named?  Count within a project may not work, because 
-- volumes may split into batches in any order.   [iBASE?]
-- 
CREATE TABLE batch
   (
     batch_id        INTEGER     AUTO_INCREMENT PRIMARY KEY,
     volume_id       INTEGER     NOT NULL,
     start_id        INTEGER     NOT NULL,
     end_id          INTEGER     NOT NULL,
     batch_name      VARCHAR(40) NOT NULL,
     status          ENUM("New",
                          "InCoding"
                          /* , etc... */ )
                                 NOT NULL DEFAULT "New",
     comments        TEXT        NOT NULL,
       UNIQUE INDEX (volume_id, start_id)
   ) TYPE=INNODB;
-- 
-- Notes: Batch is pages for which start_id <= page_id < end_id.  Initially
-- there is one batch, [0,Integer.MAX_VALUE].  It is split into smaller
-- batches during unitization by selecting a new boundary.  A batch
-- may not consist of only HIDDEN pages.  The first non-hidden page
-- must have a boundary of DOCUMENT. 

-- SESSIONS AND LOCKS

-- A session is created when a user logs in.  While a user is logged in, a session
-- corresponds to a thread in the server, represented by a ServerTask

-- TBD: For now, a session exists only while a task exists.  Eventually, we should allow
-- a session to persist after loss of connectivity and for a user to reconnect to a
-- session.  (We do have the problem of sessions persisting because a socket waiting
-- for input remains blocked forever--we need to time them out.)

-- Locking is handled in the session table, because a session will have a region
-- locked most of the time when doing useful work.

-- A client process may lock a region.  A region is consecutive set of pages
-- in a volume, where start_id <= page < end_id.  Locked regions may not overlap;
-- only one client may hold a lock on a specific region, and only that client
-- may modify records within that region.  In particular, a client must hold
-- a lock on a region in order to change boundary information or batch information,
-- although information in a locked region may be read.
--
-- Locked regions will correspond to one or more consecutive batches.  Typically,
-- a coder has a lock on a region consisting of a single batch, but a unitizer
-- may have a lock on a sequence of batches or the entire volume.
--
CREATE TABLE session
   (
     session_id      INTEGER     NOT NULL AUTO_INCREMENT PRIMARY KEY,
     users_id        INTEGER     NOT NULL,
     task_id         INTEGER     NOT NULL,
     start_time      BIGINT      NOT NULL,
     volume_id       INTEGER     NOT NULL DEFAULT 0,
     start_id        INTEGER     NOT NULL DEFAULT -1,
     end_id          INTEGER     NOT NULL DEFAULT -1,
     lock_time       BIGINT      NOT NULL DEFAULT 0,
       INDEX (volume_id, start_id),
       INDEX (task_id),
       INDEX (users_id)
   ) TYPE=INNODB;
-- 
-- Assignments of batches to various users is controlled by a separate table.  A 
-- user can write coding information only if the user is assigned that batch.  
-- Users (supervisors with appropriate permission) can read without being assigned; 
-- they can request that data be refreshed, so that they can see changes by the 
-- assigned user.
-- 
-- 
CREATE TABLE assignment
   (
     batch_id        INTEGER          NOT NULL,
     users_id        INTEGER          NOT NULL,
     timestamp       TIMESTAMP        NOT NULL    /* when assigned */
     /* more fields, perhaps indicating reason for assignment, etc. */
     /* possibly active flag, allowing history of all assignments */
   ) TYPE=INNODB;
-- 
-- Note:  The server will keep track of users actively updating a batch.  This
-- information is not placed in the database; if the server is stopped, all users
-- will become inactive and will have to log in again.
-- 
-- ENQUEUEING OF BATCHES
--
-- Batches are enqueued, with a priority, for coding or for QC.
-- There are two tables, one by users and one by teams.
-- When a batch is to be assigned to a coder or QC'er, a batch
-- is selected from these queues with the maximum priority
-- for which one is available.  To make it deterministic, we
-- choose from the user_queue first and take the oldest timestamp and
-- the lowest batch_id.
--
-- When a batch is enqueued, it is placed in one of these two queues.
-- The GUI allows enqueueing a sequence of batches at a time; each
-- individual batch is recorded in the tables.  
-- 
-- The gui allows enqueueing of a project to multiple teams; each
-- individual batch is recorded in the teams_queue for each teams_id.
--
-- When a batch is assigned, it is removed from all queues and recorded
-- in the assignment table.  Upon completion of coding, it is normally
-- placed in the team queue for QC.  When a batch is requeued, it
-- normally has the priority increased by 1; the priority can also
-- be set explicitly.
--
-- TBD:  A similar mechanism for QA?  Or, do we let the QA'ers choose
-- their own?
--
CREATE TABLE teams_project (
  teams_id           INTEGER           NOT NULL default '0',
  project_id         INTEGER           NOT NULL default '0'
) TYPE=InnoDB;

CREATE TABLE users_queue
   (
     batch_id        INTEGER           NOT NULL,
     users_id        INTEGER           NOT NULL,
     status          enum ("Coding",
                           "QC"
                           /* , etc... */ )
                                       NOT NULL,
     priority        INTEGER           NOT NULL DEFAULT 0,
     timestamp       TIMESTAMP         NOT NULL,
       primary key (batch_id, users_id),
       INDEX (users_id, status, priority, batch_id)
   ) TYPE = innodb;

CREATE TABLE teams_queue
   (
     batch_id        INTEGER           NOT NULL,
     teams_id        INTEGER           NOT NULL,
     status          enum ("Coding",
                           "QC"
                           /* , etc... */ )
                                       NOT NULL,
     priority        INTEGER           NOT NULL DEFAULT 0,
     timestamp       TIMESTAMP         NOT NULL,
       primary key (batch_id, teams_id),
       INDEX (teams_id, status, priority, batch_id)
   ) TYPE = innodb;

-- CODING INFORMATION
-- 
-- A `coded` row is created for any document or child with coding information.  It 
-- contains information as to the coding status of that record.  These records will 
-- be created for pages which are marked as documents or children; they are not 
-- created until some coding information is entered.
-- 
-- The subpage column allows for multiple coded records for a document. Normally, 
-- it will be zero.
-- 
CREATE TABLE coded
   (
     volume_id       INTEGER     NOT NULL,
     page_id         INTEGER     NOT NULL,
     subpage         INTEGER     NOT NULL DEFAULT '0',
     status          ENUM("New",
                          "Incomplete",
                          "Complete"
                          /* , etc... */ )
                                 NOT NULL DEFAULT "New"
   ) TYPE=INNODB;
-- 
-- In addition, a record will be kept of users accessing a page and whether or
-- not they updated it.  This allows determining what pages a user has accessed
-- or coded.
-- 
CREATE TABLE page_access
   (
     volume_id       INTEGER          NOT NULL,
     page_id         INTEGER          NOT NULL,
     users_id         INTEGER          NOT NULL,
     role            ENUM("Coder",
                          "QC",
                          "QA")       NOT NULL,
     changed         ENUM("No","Yes") NOT NULL,  
     timestamp       BIGINT           NOT NULL
   ) TYPE=INNODB;
-- 
-- As coding information is entered, it is added to the value, longvalue
-- and namevalue tables. Rows are 
-- identified by field and sequence.  Sequence is non-zero only for fields with 
-- multiple values.  (These are in separate records to allow searching with SQL.
-- longvalue is used for values which can be more than 128 characters.)
-- 
CREATE TABLE value
   (
     volume_id       INTEGER      NOT NULL,
     page_id         INTEGER      NOT NULL,
     field_name      VARCHAR(40)  NOT NULL,
     sequence        INTEGER      NOT NULL,
     value           VARCHAR(255) NOT NULL,
       PRIMARY KEY (volume_id, page_id, field_name, sequence)
   ) TYPE=INNODB;
-- 
CREATE TABLE longvalue
   (
     volume_id       INTEGER      NOT NULL,
     page_id         INTEGER      NOT NULL,
     field_name      VARCHAR(40)  NOT NULL,
     sequence        INTEGER      NOT NULL,
     value           TEXT         NOT NULL,
       PRIMARY KEY (volume_id, page_id, field_name, sequence)
   ) TYPE=INNODB;
-- 
CREATE TABLE namevalue
   (
     volume_id       INTEGER      NOT NULL,
     page_id         INTEGER      NOT NULL,
     field_name      VARCHAR(40)  NOT NULL,
     sequence        INTEGER      NOT NULL,
     last_name       VARCHAR(128) NOT NULL,
     first_name      VARCHAR(40)  NOT NULL,
     middle_name     VARCHAR(40)  NOT NULL,
     organization    VARCHAR(128) NOT NULL,
       PRIMARY KEY (volume_id, page_id, field_name, sequence)
   ) TYPE=INNODB;
-- 
-- TBD:  What's a reasonable maximum for field values in the value table?  What are
-- reasonable maxima for name component sizes?  Do we ever want a full middle name,
-- rather than a middle initial?  [iBASE?]
-- 
-- USERS AND TEAMS
-- 
-- Users are defined by a user table.  The table also contains their permissions 
-- and passwords.
-- 
-- The passwords are an MD5 message digest of the "real" password, so someone 
-- obtaining a copy of the user table does not have the passwords.

-- user_name is the name used on the login screen
-- TBD: last_name and first_name may or may not be retained
-- 
CREATE TABLE users
   (
     users_id     INTEGER     AUTO_INCREMENT PRIMARY KEY,
     teams_id     INTEGER     NOT NULL, /* may be 0 ? */
     user_name    VARCHAR(40) NOT NULL,
     last_name    VARCHAR(40) NOT NULL,
     first_name   VARCHAR(40) NOT NULL,
     role         ENUM ("Coder", "QC", "QA", "Admin") NOT NULL DEFAULT "Coder",
     /* other user data, as required */
     password     VARCHAR(32) NOT NULL
     /* privilege information ... */
   ) TYPE=INNODB;


-- PROJECT SETUP
-- =============
-- 
-- Project setup is handled by a table of fields that are to be entered for the 
-- project.  (Note: The project_fields table is downloaded when a user opens a
-- batch.  If project_fields changes, the changes do not take effect for a user
-- until the next time a batch is opened.)
-- 
CREATE TABLE project_fields
   (
     project_id    INTEGER UNSIGNED        NOT NULL,
     sequence      SMALLINT UNSIGNED       NOT NULL,
     field_name    VARCHAR(40)             NOT NULL,
     field_type    ENUM("text",
                        "unsigned",
                        "signed",
                        "name",
                        "date")            NOT NULL,
     field_size    SMALLINT UNSIGNED       NOT NULL,
     repeated      ENUM("No","Yes")        NOT NULL,
     required      ENUM("No","Yes")        NOT NULL,
     default_value VARCHAR(40)             NOT NULL,
     min_value     VARCHAR(40)             NOT NULL,
     max_value     VARCHAR(40)             NOT NULL,
     table_name    VARCHAR(40)             NOT NULL,
     table_mandatory ENUM("No","Yes")      NOT NULL,
     mask          VARCHAR(40)             NOT NULL,
     charset       VARCHAR(40)             NOT NULL,
     type_field    VARCHAR(40)             NOT NULL,
     type_value    VARCHAR(40)             NOT NULL 
   ) TYPE=INNODB;
-- 
-- sequence: order of this field on coding screen
--    
-- field_name: name of field (used to store values of this
-- field in the value, longvalue and datevalue tables.  Also
-- used as the title of the field on the coding screen)
-- 
-- field_type: type of field (The types name and date are special
-- built-in formats.  Note that all fields, except name, will
-- be stored in the database as text strings)
-- 
-- field_size: maximum number of text characters in a text field
-- or maximum number of digits in a signed or unsigned field.  
-- For text fields, values of this field will be stored in the longvalue
-- table instead of the value table if the size is greater than the
-- size of the value field in the value table.
-- 
-- repeated: "Yes" if this field can have multiple values; "No" if only
-- one value.
-- 
-- table_name: values of this column are selectable from the table 
-- defined by the table_spec row with the given table_name.
--
-- table_mandatory: user may not enter values in this field, other than those
-- defined in the table.
--   
-- mask: An input mask for values of this field, as defined for the Java
-- MaskFormatter class.  (TBD: Is there a better spec?)
--          
-- charset: may specify valid characters for this field, using Unix regular
-- expression syntax, e.g., "[-a-zA-Z1-9]" for letters, digits and hyphen.
-- 
-- type_field and type_value: Provide for this field to be shown conditionally
-- on the coder screen.  If type_field is non-blank, it must be the name of
-- another field F for this project.  Field F must be unconditional.  This
-- field will be shown on the coder screen only if F contains type_value.
-- 
-- For fields where values may be selected from a predefined set of values, the
-- set of values if given by table_spec and table_value, as defined below. 
-- 
-- TBD: Should table names be project specific?  Presumably, some tables (e.g., 
-- one containing "No" and "Yes") will be available to all projects.
-- 
CREATE TABLE table_spec
   (
     table_name   VARCHAR(40)       NOT NULL,
     table_type   ENUM("text",
                       "name")      NOT NULL,
     project_id   INTEGER           NOT NULL,
     requirement  ENUM("Optional",
                       "Required")  NOT NULL,
     updateable   ENUM("CoderAdd",
                       "SuperMod")  NOT NULL,
     version      INTEGER           NOT NULL
   ) TYPE=INNODB;
-- 
-- table_name: The name, referred to by project_fields.
-- 
-- project_id: If non-zero, specifies the project that uses this table.  This
-- is primarily for identification purposes and to allow removal of the table
-- when a project is removed from the database; we assume that table_name will
-- be unique over the entire database.
-- 
-- requirement:  Defines whether values must be selected from this table, and, if
-- so, who can add new values.  "CoderAdd" indicates that additions are made
-- to the table as the coder enters data.  "SuperMod" indicates that the coder may
-- not add values, but the QCer or QAer may add and delete values.  "Required"
-- indicates that changes may be made only when modifying the project setup.
-- 
-- version:  An integer which is incremented when any modification is made to the
-- values in the table.  A user's session will keep track of the version of the
-- table which is known to the user; changes indicated by an increase in the
-- version will be downloaded when the user saves a record.  (Incremental
-- downloading is needed because a table may have thousands of values, so the
-- download time would be noticeable on slow connections.)
-- 
-- 
CREATE TABLE table_value
   (
     table_name  VARCHAR(40)      NOT NULL,
     value       VARCHAR(128)     NOT NULL,
     deleted     ENUM("No","Yes") NOT NULL,
     version     INTEGER          NOT NULL
   ) TYPE=INNODB;
-- 
-- value: A value which may be selected.  
-- 
-- deleted: "Yes" indicates that this value has been deleted.  This is downloaded
-- when changes are downloaded, but not when the whole table is downloaded.  Rows
-- with deleted="Yes" may be removed when it is known that there are no connected
-- users with a local copy of the table.
-- 
-- version: Set to the table_spec version when this item is inserted or deleted.
-- 
-- TEAMS
--
-- Users can belong to teams.  Teams have leader, and teams have a collection of
-- batches that can be assigned to members.
--
-- TBD:  How does the business model really work?  [iBASE?]
--
CREATE TABLE teams
   (
     teams_id     INTEGER     AUTO_INCREMENT PRIMARY KEY,
     leader_id    INTEGER     NOT NULL,  /* may be 0 */
     team_name    VARCHAR(40) NOT NULL
     /* additional attributes of team */
   ) TYPE=INNODB;
--

-- SQL STATEMENTS
-- ==============

CREATE TABLE sql_text
   (
     name        VARCHAR(40)      NOT NULL primary key,
     role        tinyint          NOT NULL,
     text        text             NOT NULL
   ) TYPE=INNODB;

-- ADMINISTRATION
-- ==============

CREATE TABLE svradmin
   (
     min_client_version           VARCHAR(10) NOT NULL,
     max_client_version           VARCHAR(10) NOT NULL
   ) TYPE=INNODB;

-- A single row, with administrative data.
-- Indicates minimum and maximum versions for the client.


