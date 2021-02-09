CREATE OR REPLACE VIEW adempiere.RV_JP_ToDo_Team
 AS
SELECT
	t.JP_ToDo_Team_ID AS RV_JP_ToDo_Team_ID
	,t.AD_Client_ID
    ,t.AD_Org_ID
	,t.AD_User_ID AS JP_ToDo_Team_User_ID
    ,t.Created
    ,t.CreatedBy
    ,t.Description
    ,t.IsActive
    ,t.JP_ToDo_Team_ID
    ,t.JP_Todo_Type
    ,t.Name
    ,t.updated
    ,t.updatedby
    ,t.JP_Todo_Category_ID
    ,t.JP_ToDo_ScheduledStartDate
    ,t.JP_ToDo_ScheduledStartTime
    ,t.JP_ToDo_ScheduledEndDate
    ,t.JP_ToDo_ScheduledEndTime
    ,t.JP_ToDo_Status AS JP_ToDo_Team_Status
    ,t.JP_Mandatory_Statistics_Info
    ,t.JP_Todo_Team_Related_ID
    ,t.C_Project_ID
    ,t.C_ProjectPhase_ID
    ,t.C_ProjectTask_ID
    ,t.JP_Team_ID
    ,t.IsStartDateAllDayJP
    ,t.IsEndDateAllDayJP
    ,t.IsOpenToDoJP
	,t.processed
	,p.AD_User_ID AS JP_ToDo_Personal_User_ID
	,p.JP_ToDo_Status AS JP_ToDo_Personal_Status
	,p.comments 
    ,p.JP_Statistics_Choice
    ,p.JP_Statistics_DateAndTime
    ,p.JP_Statistics_Number
    ,p.jp_Statistics_YesNo
    ,u.C_Job_ID
    ,u.supervisor_ID
    ,u.AD_OrgTrx_ID
    ,oi.AD_OrgType_ID
    ,oi.JP_BusinessUnit_ID
    ,oi.JP_Corporation_ID
FROM adempiere.JP_ToDo_Team t
	INNER JOIN adempiere.JP_ToDo p ON (t.JP_ToDo_Team_ID = p.JP_ToDo_Team_ID)
	INNER JOIN adempiere.AD_User u ON (p.AD_User_ID = u.AD_User_ID)
     	LEFT OUTER JOIN adempiere.AD_OrgInfo oi ON (u.AD_OrgTrx_ID = oi.AD_Org_ID);

ALTER TABLE adempiere.RV_JP_ToDo_Team
    OWNER TO adempiere;
