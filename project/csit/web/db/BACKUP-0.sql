/*
SQLyog Community Edition- MySQL GUI v7.0  
MySQL - 4.1.21-community-nt : Database - csit
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`csit` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `csit`;

/*Table structure for table `address` */

DROP TABLE IF EXISTS `address`;

CREATE TABLE `address` (
  `address_id` int(11) NOT NULL auto_increment,
  `block` text,
  `city` text,
  `state` text,
  `country` text,
  `post_code` text,
  `contact_details_id` int(11) default NULL,
  PRIMARY KEY  (`address_id`),
  KEY `contact_details_id` (`contact_details_id`),
  CONSTRAINT `address_ibfk_1` FOREIGN KEY (`contact_details_id`) REFERENCES `contact_details` (`contact_details_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `address` */

insert  into `address`(`address_id`,`block`,`city`,`state`,`country`,`post_code`,`contact_details_id`) values (1,NULL,NULL,NULL,NULL,NULL,NULL),(2,'bilaspur','city','cg','IN','777890000',1),(3,'a','a','a','IN','111111111',2),(6,'cc','cc','cc','IN','333',5),(7,'bilaspur','bilaspur','cg','IN','111111111',6),(8,'surguja','ambikapur','cg','IN','2222222222',7),(9,'BILASPUR','BILASPUR','C.G','IN','1234512345',8),(10,'BILASPUR','BILASPUR','C.G','IN','1234512345',9),(11,'MANENDRAGARH','MANENDRAGARH','CG','IN','0777165659',10),(12,'RAIGARH','RAIGARH','C.G','IN','1234512345',11),(13,'RAIGARH','RAIGARH','C.G','IN','34253425',12),(14,'BILASPUR','BILASPUR','C.G','IN','678549',13),(15,'BILASPUR','BILASPUR','C.G','IN','0786543',14),(17,'BILASPUR','BILASPUR','C.G','IN','897045',16),(18,'BILASPUR','BILASPUR','C.G','IN','90876',17);

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `comment_id` int(11) NOT NULL auto_increment,
  `user_id` int(11) default NULL,
  `subject` text,
  `comment` text,
  `comment_date` date default NULL,
  PRIMARY KEY  (`comment_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `comment` */

insert  into `comment`(`comment_id`,`user_id`,`subject`,`comment`,`comment_date`) values (2,2,'photo gallery','Nice collection','2009-06-17');

/*Table structure for table `contact_details` */

DROP TABLE IF EXISTS `contact_details`;

CREATE TABLE `contact_details` (
  `contact_details_id` int(11) NOT NULL auto_increment,
  `user_id` int(11) default NULL,
  `mobile_number` text,
  `phone_number` text,
  `email_id` text,
  PRIMARY KEY  (`contact_details_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `contact_details_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `contact_details` */

insert  into `contact_details`(`contact_details_id`,`user_id`,`mobile_number`,`phone_number`,`email_id`) values (1,2,'9329199431','07752-199417','jnv.abhilasha@gmail.com'),(2,3,'1111111111','1111111111','aaaabbbb@gmail.com'),(5,7,'333','3333','ccccccccc@gmail.com'),(6,8,'9329199431','0775219943','jnv.abhilasha@gmail.com'),(7,9,'9926116399','07752-116399','ssm.anjali@gmail.com'),(8,10,'1234512345','12345-12345','amit.saxena@gmail.com'),(9,11,'1234512345','12345-12345','ASHISHRASTOGI@gmail.com'),(10,12,'9826165659','07771-65659','POOJA.AGRAWAL@GMAIL.COM'),(11,13,'9382177864','07752-77864','HSHOTA@GMAIL.COM'),(12,14,'9926554321','07752-54321','PLPUJARI@YAHOO.COM'),(13,15,'9977654531','07752-54531','AJAYSINGH@REDIFF.COM'),(14,16,'6547892341','07752-92341','GAZALA@REDIFF.COM'),(16,18,'4563729870','07752-29870','ANURAG@GMAIL.COM'),(17,19,'9300199431','07753-299431','JNV.ABHILASHA@GMAIL.COM');

/*Table structure for table `course` */

DROP TABLE IF EXISTS `course`;

CREATE TABLE `course` (
  `course_id` int(11) NOT NULL auto_increment,
  `course_category_id` int(11) default NULL,
  `name` text,
  `duration` text,
  `seat` text,
  `eligibility` text,
  PRIMARY KEY  (`course_id`),
  KEY `course_category_id` (`course_category_id`),
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`course_category_id`) REFERENCES `course_category` (`course_category_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `course` */

insert  into `course`(`course_id`,`course_category_id`,`name`,`duration`,`seat`,`eligibility`) values (3,2,'M.Phil(English)','2 Semesters','25','PG(55%)+Graduation(50%)'),(4,2,'M.A(English)','4 Semesters','25','Graduation(50%) with English as one of the paper'),(5,2,'M.Phil(Hindi)','2 Semesters','10','PG(50%)+Graduation(50%) with Hindi as one of the sibject'),(6,2,'M.A(Hindi)','4 Semesters','20','Graduatin(50%) with Hindi as one of the subject'),(7,2,'M.M.C.J','4 Semesters','20','Graduation in any discipline with second division'),(8,2,'M.Lib.& Inf.Sc','2 Semesters','20','B.Lib. & Information Science with 50% marks'),(9,2,'B.Lib & Inf. Sc.','2 Semesters','25','50% marks either at Graduate or at Post Graduate level'),(10,3,'M.Phil(Education)','2 Semesters','10','Post Gradution in Education with 55% marks & 50% marks at Graduation Level  '),(11,4,'BE(Chemical )','8 Semesters','60','PET &/or AIEEE CLEARED'),(12,4,'BE(Electronics & Communication)','8 Semesters','60','PET &/or  AIEEE CLEARED'),(13,4,'BE(Computer Science)','8 Semesters','60','PET &/or  AIEEE CLEARED'),(14,4,'BE(Information Technology)','8 Semesters','60','PET &/or  AIEEE CLEARED'),(15,4,'BE(Industrial  Production)','8 Semesters','60','PET &/or  AIEEE CLEARED'),(16,4,'BE(Mechanical)','8 Semesters','60','PET &/or  AIEEE CLEARED'),(17,5,'M.A. /M.Sc Anthropology','4 Semesters','20','B.Sc. / B.A. with minimum 45% marks'),(18,5,'M.Phil(Social Anthropology)','4 Semesters','10','M.A./M.Sc with 55% marks in Anthropology/Sociology & 50% Marks in Graduation'),(19,5,'M.Phil(Social Work)','2 Semesters','05','MSW with 55% Marks & 50% marks in Graduation'),(20,6,'M.Com(Buss.Admn.)','4 Semesters','30(20+10*)','B.Com/B.B.A. /B.B.M. with 45% marks'),(21,6,'M.Phil(Commerce)','2 Semesters','30(20+10*)','PG in the Subjectwith 55% marks and 50% marks at Graduation Level'),(22,6,'M.B.A','4 Semesters','60','Graduation in any discipline with minimum 50%marks in aggregrate'),(24,7,'M.Sc.(BioTechnology)','4 Semesters','30(10+20*)','Graduation with Aggregate 50% marks in Botany/Zoology/Biotechnology/Microbiology.'),(25,7,'B.Sc.(Hons.)BioTechnology','6 Semesters','30','(10+2) with Biologyas mainsubject with 50% Aggregate marks'),(26,7,'M.Sc(Forestry Wild Life. EnvironmentalSc.)','4 Semesters','20(15+5*)','Graduation in Science with 50% Marksin aggregate(Admission will be in merit basis)'),(27,7,'Master of Forestry Mgt.','4 Semesters','20','Graduation in any discipline Management'),(28,7,'M.Pharma.(Pharmaceutics)','4 Semesters','08','B.Pharmwith 55% marks,(GATE qualified candidate willbe preferred.)'),(29,7,'M.Pharam.(Pharm. Chemistry)','4 Semesters','10','B.Pharm with 55% marks,(GATE qualified candidate willbe preferred.)'),(30,7,'B.Pharm*','4 Semesters','60','10+2 passed with 50% marks in PCM/PCB (Admission as per guodelines issued from time to time by DTE Govt.of C.G.)'),(31,7,'D.Pharm','4 Semesters','60','10+2 passed with in PCM/PCB(Admission as per guidelines issued from time to time by DTE Govt. of C.G. )'),(32,8,'M.P.Ed.','2 Years','35(30+5*)','Candidates who have obtained at least 50% marks in the B.P.Ed degree/B.P.Ed.Integrated B.P.E(4 years.) professional degree are eligible & aptitude for sports'),(33,8,'B.P.Ed','1 Years','60(50+10*)','Graduationin any subject/B.P.E(3 years) with at least 45% marks and aptitude for sports'),(34,9,'M.Sc(I.T)','4 Semesters','30','Admission on the basis of marks secured at Graduation level.Graduation with 50% marks in Physics/Electronics/Comp. Sc. Statistics/B.Sc(IT)/BCA or equivalent'),(35,9,'M.C.A','6 Semesters','60','Admissionmthrough State Level Entrance Test'),(36,9,'M.Phil(Mathematics)','2 Semesters','10','M.A./M.Sc Mathematics with 55% Marksin Graduation with Mathematics as one subject'),(37,9,'M.A./M.Sc','4 Semesters','35(30+5*)','Graduation with Mathematics BE/B.Tech./BCA'),(38,9,'M.Sc.Physics(Spl. in Material Sc.)','4 Semesters','30(20+10*)','Graduation in Physics or in the related subject in second division'),(39,9,'M.Sc.Electronics(Spl. in Electronics)','4 Semesters','30(20+10*)','Graduation in Electronics or in the related subject in second division'),(40,9,'M.Phil(Physics)','2 Semesters','15','Min 55% at M.Sc.in Physics or related Subject'),(41,9,'M.Sc.(Rural Tech.)','4 Semesters','20','B.E/B.Tech./B.Sc(Bio/Maths/Agri)'),(42,9,'B.Sc(Hons.)(Rural Tech.)','3 Years','25','10+2with Science/Agriculture.(Rural Tech.)'),(43,10,'M.Phil.Econ','2 Semesters','15','Post Graduate in the subject with 55% marks & 50% marks at graduation level'),(44,10,'M.A.Econ.','4 Semesters','25','Graduation with 50% marks'),(45,10,'M.Phil.(History)','2 Semesters','15','Post Graduate in the subject with 55% marks & 50% marks at graduation level'),(46,10,'M.A.(History)','4 Semesters','25','Graduation with 50% marks'),(47,10,'Ph.D(Political Science)','As per the Rule','4','As per rule'),(48,10,'M.Phil(Political Science)','1 Years','20','Post Graduate in Political Science with 55% &  50% marks at Graduation Level'),(49,10,'M.A.(Public Admn.)','2 Years','20','Graduation in any subject with second division'),(50,10,'MSW','4 Semesters','30','Graduation with 50% marks'),(51,11,'PGDEESW','1 Years','30(25+5*)','Graduation in any discipline with 50% marks'),(52,11,'PGDFD','1 Years','30','Graduation in any discipline'),(53,12,'B.Lib & I.Sc.(Bachelor of Lib & Information Sc.)','1 Years','As per the Rule','Graduation'),(54,12,'M.Lib & I.Sc.(Master of Lib & Information Sc.)','1 Years','As per the Rule','B.Lib.'),(55,12,'PGDCA(Post Grad. Diploma in Comp. Sc. & Application)','1 Years','As per the Rule','Graduation(with at least II Division)'),(56,12,'P.G. Diploma in Bussiness Management','1 Years','As per the Rule','Graduation(with at least II Division)'),(57,12,'P.G. Diploma in Ind. Rel., L.W  &  Per Mgt.','1 Years','As per the Rule','Graduation(with at least II Division)');

/*Table structure for table `course_category` */

DROP TABLE IF EXISTS `course_category`;

CREATE TABLE `course_category` (
  `course_category_id` int(11) NOT NULL auto_increment,
  `name` text,
  PRIMARY KEY  (`course_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `course_category` */

insert  into `course_category`(`course_category_id`,`name`) values (2,'ARTS'),(3,'EDUCATION'),(4,'ENGINEERING'),(5,'LIFE SCIENCE'),(6,'MANAGEMENT & COMMERCE'),(7,'NATURAL RESOURCES'),(8,'PHYSICAL EDUCATION'),(9,'SCIENCE'),(10,'SOCIAL SCIENCE'),(11,'ADULT,CONTINUING EDUCATION & EXTENTION'),(12,'DISTANCE EDUCATION');

/*Table structure for table `fee` */

DROP TABLE IF EXISTS `fee`;

CREATE TABLE `fee` (
  `fee_id` int(11) NOT NULL auto_increment,
  `student_id` int(11) default NULL,
  `sem_id` int(11) default NULL,
  `amount` double default NULL,
  `late_fee` double default NULL,
  `due_date` date default NULL,
  `d_date` date default NULL,
  PRIMARY KEY  (`fee_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `fee_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `fee` */

/*Table structure for table `staff` */

DROP TABLE IF EXISTS `staff`;

CREATE TABLE `staff` (
  `staff_id` int(11) NOT NULL auto_increment,
  `user_id` int(11) default NULL,
  `type` text,
  `post` text,
  `joining_date` date default NULL,
  `releaving_date` date default NULL,
  `qualification` text,
  PRIMARY KEY  (`staff_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `staff` */

insert  into `staff`(`staff_id`,`user_id`,`type`,`post`,`joining_date`,`releaving_date`,`qualification`) values (1,10,'HOD','Proffessor','1999-06-08',NULL,'Ph.D'),(2,11,'STAFF','LECTURER','2001-06-08',NULL,'Ph.D'),(3,13,'STAFF','LECTURER','2005-04-07',NULL,'Ph.D'),(4,14,'STAFF','LECTURER','2001-04-12',NULL,'Ph.D'),(5,15,'STAFF','LECTURER','1999-03-18',NULL,'Ph.D'),(6,16,'STAFF','LECTURER','2003-05-09',NULL,'Ph.D'),(8,18,'STAFF','LECTURER','2009-06-11',NULL,'Ph.D');

/*Table structure for table `student` */

DROP TABLE IF EXISTS `student`;

CREATE TABLE `student` (
  `student_id` int(11) NOT NULL auto_increment,
  `user_id` int(11) default NULL,
  `course_id` int(11) default NULL,
  `enroll_number` text,
  `admission_date` date default NULL,
  `course_completion_date` date default NULL,
  PRIMARY KEY  (`student_id`),
  KEY `user_id` (`user_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `student_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `student` */

/*Table structure for table `subject` */

DROP TABLE IF EXISTS `subject`;

CREATE TABLE `subject` (
  `subject_id` int(11) NOT NULL auto_increment,
  `course_id` int(11) default NULL,
  `name` text,
  `description` text,
  PRIMARY KEY  (`subject_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `subject_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `subject` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL auto_increment,
  `password` text,
  `user_name` text,
  `first_name` text,
  `middle_name` text,
  `last_name` text,
  `gender` text,
  `theme` text,
  `type` text,
  `date_of_birth` date default NULL,
  PRIMARY KEY  (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `user` */

insert  into `user`(`user_id`,`password`,`user_name`,`first_name`,`middle_name`,`last_name`,`gender`,`theme`,`type`,`date_of_birth`) values (2,'abhi','abhi','abhi','abhi','abhi','F','classical','USER','1984-11-20'),(3,'a','a','a','a','a','M','classical','USER','2009-01-01'),(7,'c','c','c','c','c','M','classical','USER','2009-06-29'),(8,'abhilasha','abhilasha','abhilasha','abhilasha','singh','F','indigo','ADMIN','1984-11-20'),(9,'anjali','anjali','anjali','anjali','singh','F','classical','ADMIN','1993-11-05'),(10,'AMIT','amit','AMIT','KUMAR','SAXENA','M','classical','STAFF','1970-01-21'),(11,'ASHISH','ASHISH','ASHISH','KUMAR','RASTOGI','M','classical','STAFF','1970-06-03'),(12,'pooja','POOJA','POOJA','','AGRAWAL','F','classical','STUDENT','1986-07-01'),(13,'HSHOTA','H.S.HOTA','HARI','SHANKAR','HOTA','M','classical','STAFF','1973-02-21'),(14,'PLPUJARI','P.L.PUJARI','PUSHPALATA','','PUJARI','M','classical','STAFF','1977-08-04'),(15,'AJAYSINGH','AJAYSINGH','AJAY','KUMAR','SINGH','M','classical','STAFF','1970-02-05'),(16,'GAZALA','GAZALA','GAZALA','','MUMTAZ','M','classical','STAFF','1996-11-20'),(18,'ANURAG','ANURAG','ANURAG','','SRIVASTAVA','M','classical','STAFF','1974-06-12'),(19,'ABHILASHA','ABHILASHA','ABHILASHA','','SINGH','F','classical','STUDENT','1985-11-20');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
