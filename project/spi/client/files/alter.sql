
CREATE TABLE customerprice (
  customerprice_id int(11) NOT NULL auto_increment,
  project_id int(11) NOT NULL default '0',
  volume_id int(11) NOT NULL default '0',
  field_level tinyint(4) NOT NULL default '0',
  unitize_page_price smallint(3) NOT NULL default '0',
  unitize_doc_price smallint(3) NOT NULL default '0',
  coding_page_price smallint(3) NOT NULL default '0',
  coding_doc_price smallint(3) NOT NULL default '0',
  PRIMARY KEY  (customerprice_id),
  UNIQUE KEY field_level (project_id,volume_id,field_level),
  KEY project_id (project_id),
  KEY volume_id (volume_id)
) TYPE=InnoDB;

INSERT INTO customerprice VALUES (1,1,1,0,0,0,0,0);
INSERT INTO customerprice VALUES (2,2,2,0,0,0,0,0);
INSERT INTO customerprice VALUES (3,3,3,0,0,0,0,0);
INSERT INTO customerprice VALUES (4,4,4,0,0,0,0,0);
INSERT INTO customerprice VALUES (5,5,5,0,0,0,0,0);
INSERT INTO customerprice VALUES (6,6,6,0,0,0,0,0);
INSERT INTO customerprice VALUES (7,7,7,0,0,0,0,0);
INSERT INTO customerprice VALUES (8,8,8,0,0,0,0,0);
INSERT INTO customerprice VALUES (9,9,9,0,0,0,0,0);
INSERT INTO customerprice VALUES (10,10,10,0,0,0,0,0);
INSERT INTO customerprice VALUES (11,1,0,0,0,0,0,0);
INSERT INTO customerprice VALUES (12,2,0,0,0,0,0,0);
INSERT INTO customerprice VALUES (13,3,0,0,0,0,0,0);
INSERT INTO customerprice VALUES (14,4,0,0,0,0,0,0);
INSERT INTO customerprice VALUES (15,5,0,0,0,0,0,0);
INSERT INTO customerprice VALUES (16,6,0,0,0,0,0,0);
INSERT INTO customerprice VALUES (17,7,0,0,0,0,0,0);
INSERT INTO customerprice VALUES (18,8,0,0,0,0,0,0);
INSERT INTO customerprice VALUES (19,9,0,0,0,0,0,0);
INSERT INTO customerprice VALUES (20,10,0,0,0,0,0,0);

ALTER TABLE projectfields
  ADD field_group tinyint(4) NOT NULL default '0' AFTER field_level,
  ADD minimum_size smallint(5) NOT NULL default '0' AFTER field_size,
  ADD valid_chars varchar(40) NOT NULL default '' AFTER mask,
  ADD invalid_chars varchar(40) NOT NULL default ''AFTER valid_chars;

ALTER TABLE users 
  ADD admin_profit enum('No','Yes') NOT NULL default 'No' 
  AFTER admin_export;

UPDATE users SET admin_profit = 'Yes'
WHERE user_name IN ('EASTONB', 'NMCCALL', 'ROTHK', 'GRESPINJ', 'GRESPINM', 'TEST_EASTONB', 'TEST_NMCCALL', 'TEST_ROTHK', 'TEST_GRESPINJ', 'TEST_GRESPINM');

CREATE TABLE event (
  users_id int(11) NOT NULL default '0',
  volume_id int(11) NOT NULL default '0',
  batch_id int(11) NOT NULL default '0',
  status enum ('Unitize','UQC','UComplete','UBatched','Coding'
        ,'CodingQC','QCComplete','QA','QAComplete') NOT NULL default 'Unitize',
  open_timestamp bigint(20) NOT NULL default '0',
  add_timestamp bigint(20) NOT NULL default '0',
  close_timestamp bigint(20) NOT NULL default '0',
  child_count int(11) NOT NULL default '0',
  page_count int(11) NOT NULL default '0',
  field_count tinyint(4) NOT NULL default '0',
  UNIQUE KEY timestamp (users_id, volume_id, batch_id, status, open_timestamp)
) TYPE=InnoDB;

ALTER TABLE export
  MODIFY field_delimiter tinytext,
  MODIFY text_qualifier tinytext,
  MODIFY value_separator tinytext,
  ADD name_mask1 tinytext AFTER org_delimiter,
  ADD name_mask2 tinytext AFTER name_mask1,
  ADD name_mask3 tinytext AFTER name_mask2,
  ADD name_mask4 tinytext AFTER name_mask3,
  ADD brs_format enum('No','Yes') NOT NULL default 'No';

ALTER TABLE export
   DROP name_delimiter,
   DROP org_delimiter;

ALTER TABLE projectfields
  ADD tag_name VARCHAR(20) NOT NULL DEFAULT '' AFTER field_name;

ALTER TABLE tablespec
  ADD model_tablespec_id int(11) NOT NULL default '0';

ALTER TABLE tablevalue
  ADD model_value varchar(255) NOT NULL default '';

ALTER TABLE batch
  ADD active_group tinyint(4) NOT NULL default '0';

ALTER TABLE batchcredit
  ADD active_group tinyint(4) NOT NULL default '0';

ALTER TABLE batcherror
  ADD active_group tinyint(4) NOT NULL default '0';

ALTER TABLE volume
  ADD original_volume_name varchar(40) NOT NULL default '' AFTER volume_name;

UPDATE volume SET original_volume_name = volume_name
  WHERE original_volume_name = '';

ALTER TABLE page
  ADD group_one_path varchar(255) NOT NULL default '' AFTER path,
  ADD group_one_filename varchar(255) NOT NULL default '' AFTER filename,
  ADD document_number VARCHAR(20) NOT NULL DEFAULT '';

flush tables;




