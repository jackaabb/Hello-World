
/****** Object:  StoredProcedure [dbo].[usp_update_dw_seller_consumption_merchant_branch]    Script Date: 2023/4/13 下午 03:41:16 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO




CREATE  PROCEDURE [dbo].[usp_update_dw_seller_consumption_merchant_branch](
   @qStartDate  VARCHAR(8) ,   @qEndDate AS VARCHAR(8)  , @isAuto  VARCHAR(1)
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
	
	PRINT 'procudure [usp_update_dw_seller_consumption_merchant_branch] 執行開始!' ;
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
	-- for
 		IF EXISTS (SELECT 1 FROM [dbo].[dw_seller_consumption_merchant_branch] WHERE CONVERT(VARCHAR(8),cmb_update_time,112)  BETWEEN @searchStartDate and  @searchEndDate)
	DELETE FROM dw_seller_consumption_merchant_branch WHERE CONVERT(VARCHAR(8),cmb_update_time,112) BETWEEN @searchStartDate and  @searchEndDate ;
	
	PRINT '刪除 [dw_seller_consumption_merchant_branch] 既有資料: ' + CAST(@@ROWCOUNT AS VARCHAR(MAX)) +'筆。';  

	 IF OBJECT_ID('tempdb..#TMPMSETTING') IS NOT NULL
	 DROP TABLE #TMPMSETTING;  
	-- save to tmp table
	 SELECT a.* INTO #TMPMSETTING FROM (

			
	 SELECT
          cmb.cmb_id	 
		 ,cmb.cmb_merchant_id  
		 ,cmb.cmb_merchant_no  
		 ,cmb.cmb_no   
		 ,cmb.cmb_name  
		 ,cmb.cmb_simple_name  
		 ,cmb.cmb_name_en  
		 ,cmb.cmb_city   
		 ,cmb.cmb_address  
		 ,cmb.cmb_cus_telephone  
		 ,cmb.cmb_status  
		 ,cmb.cmb_type  
		 ,cmb.cmb_acct_bank_id  
		 ,cmb.cmb_acct_no1  
		 ,cmb.cmb_acct_no2  
		 ,cmb.cmb_is_need_refund_pwd  
		 ,cmb.cmb_transfer_bank_code  
		 ,cmb.cmb_transfer_accou  
		 ,cmb.cmb_transfer_name  
		 ,cmb.cmb_transfer_branch_code  
		 ,cmb.cmb_update_time  

		   ,CASE
				WHEN dw.cmb_id IS NOT NULL THEN
					'U'
				ELSE
					'A'
			END AS　action_flag
		 

		FROM
		dbo.[consumption_merchant_branch] cmb
		left join dw_seller_consumption_merchant_branch dw on cmb.cmb_id = dw.cmb_id
		WHERE
		CONVERT(VARCHAR(8),cmb.cmb_update_time,112) BETWEEN @searchStartDate and  @searchEndDate


	 ) a
	PRINT '預計轉檔筆數:' + CAST(@@ROWCOUNT AS VARCHAR(MAX));  
    CREATE NONCLUSTERED INDEX ix_action_flag ON #TMPMSETTING (action_flag);
	 -- add to dw table
	IF EXISTS (SELECT 1 FROM #TMPMSETTING WHERE action_flag ='A') 	
    BEGIN
     
INSERT INTO [dbo].[dw_seller_consumption_merchant_branch]
           ([cmb_id]
		   ,[cmb_merchant_id]
           ,[cmb_merchant_no]
           ,[cmb_no]
           ,[cmb_name]
           ,[cmb_simple_name]
           ,[cmb_name_en]
           ,[cmb_city]
           ,[cmb_address]
           ,[cmb_cus_telephone]
           ,[cmb_status]
           ,[cmb_type]
           ,[cmb_acct_bank_id]
           ,[cmb_acct_no1]
           ,[cmb_acct_no2]
           ,[cmb_is_need_refund_pwd]
           ,[cmb_transfer_bank_code]
           ,[cmb_transfer_accou]
           ,[cmb_transfer_name]
           ,[cmb_transfer_branch_code]
           ,[cmb_update_time])
	  SELECT 
          cmb_id	  
		 ,cmb_merchant_id  
		 ,cmb_merchant_no  
		 ,cmb_no   
		 ,cmb_name  
		 ,cmb_simple_name  
		 ,cmb_name_en  
		 ,cmb_city   
		 ,cmb_address  
		 ,cmb_cus_telephone  
		 ,cmb_status  
		 ,cmb_type  
		 ,cmb_acct_bank_id  
		 ,cmb_acct_no1  
		 ,cmb_acct_no2  
		 ,cmb_is_need_refund_pwd  
		 ,cmb_transfer_bank_code  
		 ,cmb_transfer_accou  
		 ,cmb_transfer_name  
		 ,cmb_transfer_branch_code  
		 ,cmb_update_time  
	   FROM #TMPMSETTING WHERE action_flag ='A'

		
    END
SET @insertRow = CAST(@@ROWCOUNT AS VARCHAR(MAX));
	 -- update to dw table
	IF EXISTS (SELECT 1 FROM #TMPMSETTING WHERE action_flag ='U') 	
	BEGIN
		UPDATE t1 set
		t1.cmb_merchant_id = t2.cmb_merchant_id
      ,t1.cmb_merchant_no = t2.cmb_merchant_no
      ,t1.cmb_no = t2.cmb_no
      ,t1.cmb_name = t2.cmb_name
      ,t1.cmb_simple_name = t2.cmb_simple_name
      ,t1.cmb_name_en = t2.cmb_name_en
      ,t1.cmb_city = t2.cmb_city
      ,t1.cmb_address = t2.cmb_address
      ,t1.cmb_cus_telephone = t2.cmb_cus_telephone
      ,t1.cmb_status = t2.cmb_status
      ,t1.cmb_type = t2.cmb_type
      ,t1.cmb_acct_bank_id = t2.cmb_acct_bank_id
      ,t1.cmb_acct_no1 = t2.cmb_acct_no1
      ,t1.cmb_acct_no2 = t2.cmb_acct_no2
      ,t1.cmb_is_need_refund_pwd = t2.cmb_is_need_refund_pwd
      ,t1.cmb_transfer_bank_code = t2.cmb_transfer_bank_code
      ,t1.cmb_transfer_accou = t2.cmb_transfer_accou
      ,t1.cmb_transfer_name = t2.cmb_transfer_name
      ,t1.cmb_transfer_branch_code = t2.cmb_transfer_branch_code
      ,t1.cmb_update_time = t2.cmb_update_time
	
		FROM [dw_seller_consumption_merchant_branch] t1 INNER JOIN #TMPMSETTING t2 ON t1.cmb_id = t2.cmb_id WHERE t2.action_flag = 'U' ; 
		
           
	END
SET @updateRow = CAST(@@ROWCOUNT AS VARCHAR(MAX));  
	PRINT '新增筆數:' + @insertRow  ;
	PRINT '更新筆數:' + @updateRow ;
	PRINT 'procudure [usp_update_dw_seller_consumption_merchant_branch] 執行結束!' 



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


