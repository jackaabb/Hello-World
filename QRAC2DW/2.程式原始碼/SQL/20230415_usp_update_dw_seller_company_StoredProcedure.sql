
/****** Object:  StoredProcedure [dbo].[usp_update_dw_seller_company]    Script Date: 2023/4/13 下午 03:39:51 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO




CREATE  PROCEDURE [dbo].[usp_update_dw_seller_company] (
   @qStartDate  VARCHAR(8) ,   @qEndDate AS VARCHAR(8) , @isAuto  VARCHAR(1)
  )

	-- Add the parameters for the stored procedure here
	
AS
BEGIN --TRY
	-- SET NOCOUNT ON added to prevent extra result sets from

	SET NOCOUNT ON;
	
	DECLARE @searchStartDate VARCHAR(8) , @searchEndDate VARCHAR(8);

	
	IF @qStartDate = '' 
		SET @searchStartDate = CONVERT(VARCHAR(8), GETDATE()-1,112)	
	ELSE 
		SET @searchStartDate = CONVERT(VARCHAR(8),DATEADD(day, -1, CONVERT(VARCHAR(8), @qStartDate ,112)),112)
	IF @qEndDate = '' 
		SET @searchEndDate = CONVERT(VARCHAR(8), GETDATE()-1,112);
	ELSE 
		SET @searchEndDate = CONVERT(VARCHAR(8),DATEADD(day, -1, CONVERT(VARCHAR(8), @qEndDate ,112)),112) 


	IF( @qEndDate < @qStartDate)
		BEGIN
			PRINT '輸入時間錯誤' ; 
			RETURN;

		END
	
	DECLARE @ProcName nvarchar(128);
	SET @ProcName = OBJECT_NAME(@@PROCID);  
	PRINT 'procudure [usp_update_dw_seller_company] 執行開始!' ;
	
	IF ( UPPER(@isAuto) ='Y')
    BEGIN
	DECLARE @usp_date varchar(8) 
		-- 若存在，代表不是第一次跑，要檢查最新異動時間是不是前一天，每天都得執行。
		SELECT @usp_date = usp_date FROM usp_execute_info where usp_name = @ProcName 
		IF( @usp_date != '')
		BEGIN
			IF ( (select DATEDIFF(day, @usp_date, @searchEndDate)) <> 1)
			BEGIN
					
					PRINT '即將轉檔的日期為:'+ @searchEndDate ;
					PRINT 'dw交易紀錄最新異動時間為:' + @usp_date  +'，非前一天，不執行!。'; 
					RETURN;
			END
		END
	END
  

	PRINT '抓取異動日期區間為: '+ @searchStartDate +'-'+ @searchEndDate;
	-- Declare variables used in log.
	DECLARE @updateRow NVARCHAR(MAX) , @insertRow NVARCHAR(MAX), @today  VARCHAR(8);
  SET @today =  CONVERT(VARCHAR(8), GETDATE(),112);
	-- for reun
	IF EXISTS (SELECT 1 FROM dbo.dw_seller_company WHERE   CONVERT(VARCHAR(8),c_update_time ,112)	 BETWEEN @searchStartDate and  @searchEndDate) 	
	DELETE FROM dbo.dw_seller_company WHERE  CONVERT(VARCHAR(8),c_update_time ,112) BETWEEN @searchStartDate and  @searchEndDate ;
	
	PRINT '刪除 [dw_seller_company] 既有資料: ' + CAST(@@ROWCOUNT AS VARCHAR(MAX)) +'筆。';  

	 IF OBJECT_ID('tempdb..#TMPCOMPANY') IS NOT NULL
	 DROP TABLE #TMPCOMPANY;  
	-- save to tmp table
	 SELECT a.* INTO #TMPCOMPANY FROM (

			
		SELECT c.c_id,c.c_fein, c.c_create_time, c.c_create_user, 
		 c.c_update_time, c.c_update_user, c.c_admin_email, 
		 c.c_name, c.c_qrcode_is_active, c.c_admin_mobile, c.c_host_branch 
		   ,CASE
				WHEN dw.c_id IS NOT NULL THEN
					'U'
				ELSE
					'A'
			END AS　action_flag
		 

		FROM
			dbo.company c 
        left join dw_seller_company dw on c.c_id = dw.c_id  
		WHERE
		 CONVERT(VARCHAR(8),c.c_update_time ,112)	BETWEEN @searchStartDate and  @searchEndDate


	 ) a
	PRINT '預計轉檔筆數:' + CAST(@@ROWCOUNT AS VARCHAR(MAX));  
    CREATE NONCLUSTERED INDEX ix_action_flag ON #TMPCOMPANY (action_flag);
	 -- add to dw table
	IF EXISTS (SELECT 1 FROM #TMPCOMPANY WHERE action_flag ='A') 	
    BEGIN
        INSERT INTO dbo.dw_seller_company(c_id, c_fein,c_create_time,c_create_user, 
		c_update_time,c_update_user,c_admin_email, 
		c_name,c_qrcode_is_active,c_admin_mobile,c_host_branch 
		 )
		SELECT c_id, c_fein,c_create_time,c_create_user, 
		c_update_time,c_update_user,c_admin_email, 
		c_name,c_qrcode_is_active,c_admin_mobile,c_host_branch 
	   FROM #TMPCOMPANY WHERE action_flag ='A'

		
    END
SET @insertRow = CAST(@@ROWCOUNT AS VARCHAR(MAX));
	 -- update to dw table
	IF EXISTS (SELECT 1 FROM #TMPCOMPANY WHERE action_flag ='U') 	
	BEGIN
		UPDATE t1 set
		 t1.c_fein = t2.c_fein, 
		 t1.c_create_time = t2.c_create_time,
		 t1.c_create_user = t2.c_create_user, 
		 t1.c_update_time = t2.c_update_time,
		 t1.c_update_user = t2.c_update_user,
		 t1.c_admin_email =t2.c_admin_email, 
		 t1.c_name =t2.c_name, 
		 t1.c_qrcode_is_active =t2.c_qrcode_is_active, 
		 t1.c_admin_mobile =t2.c_admin_mobile, 
		 t1.c_host_branch =t2.c_host_branch
		FROM dbo.dw_seller_company t1 INNER JOIN #TMPCOMPANY t2 ON t1.c_id = t2.c_id WHERE t2.action_flag = 'U' ; 
		
           
	END
SET @updateRow = CAST(@@ROWCOUNT AS VARCHAR(MAX));  
	PRINT '新增筆數:' + @insertRow  ;
	PRINT '更新筆數:' + @updateRow ;
	PRINT 'procudure [usp_update_dw_seller_company] 執行結束!' 


	IF EXISTS (SELECT 1 FROM usp_execute_info WITH (nolock) where usp_name = @ProcName)
		UPDATE usp_execute_info SET usp_date = @searchEndDate WHERE usp_name = @ProcName AND usp_date < @searchEndDate
	ELSE 
		INSERT INTO usp_execute_info (usp_name , usp_date) VALUES (@ProcName , @searchEndDate)



END --TRY
/**
BEGIN CATCH
	SELECT  
        ERROR_NUMBER() AS ErrorNumber      
        ,ERROR_PROCEDURE() AS ErrorProcedure  
        ,ERROR_LINE() AS ErrorLine  
        ,ERROR_MESSAGE() AS ErrorMessage;  
	

END CATCH
**/







GO


