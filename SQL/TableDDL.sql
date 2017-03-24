----------------------------------------------------
-- NORMAL QUESTIONS
----------------------------------------------------
CREATE TABLE "H664800"."NORMAL_QUESTIONS" 
   (	"QUESTION_ID" NUMBER(*,0) NOT NULL ENABLE, 
	"QUESTION" VARCHAR2(255) NOT NULL ENABLE, 
	"RIGHT_ANSWER" VARCHAR2(255) NOT NULL ENABLE, 
	"ANSWER1" VARCHAR2(255) NOT NULL ENABLE, 
	"ANSWER2" VARCHAR2(255) NOT NULL ENABLE, 
	"ANSWER3" VARCHAR2(255) NOT NULL ENABLE, 
	"TOPIC_ID" NUMBER(*,0), 
	"DIFFICULTY" NUMBER(*,0), 
	"USER_ID" NUMBER(*,0), 
	 CONSTRAINT "NORMALQUESTIONS_PK" PRIMARY KEY ("QUESTION_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE, 
	 CONSTRAINT "QUESTIONS_TOPICS_FK" FOREIGN KEY ("TOPIC_ID")
	  REFERENCES "H664800"."QUESTION_TOPICS" ("TOPIC_ID") ON DELETE SET NULL ENABLE
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS";

----------------------------------------------------
-- QUESTION_TOPICS
----------------------------------------------------
CREATE TABLE "H664800"."QUESTION_TOPICS" 
   (	"TOPIC_ID" NUMBER(*,0) NOT NULL ENABLE, 
	"NAME" VARCHAR2(255) NOT NULL ENABLE, 
	 CONSTRAINT "QUESTION_TOPICS_PK" PRIMARY KEY ("TOPIC_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS";

