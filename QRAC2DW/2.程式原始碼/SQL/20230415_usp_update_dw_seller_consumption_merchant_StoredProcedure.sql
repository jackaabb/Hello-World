

/****** Object:  StoredProcedure [dbo].[usp_update_dw_seller_consumption_merchant]    Script Date: 2023/4/13 下午 03:40:19 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO




CREATE  PROCEDURE [dbo].[usp_update_dw_seller_consumption_merchant](
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
	
	PRINT 'procudure [usp_update_dw_seller_consumption_merchant] 執行開始!' ;
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
 		IF EXISTS (SELECT 1 FROM [dbo].[dw_seller_consumption_merchant] WHERE  CONVERT(VARCHAR(8),cm_update_time,112)	 BETWEEN @searchStartDate and  @searchEndDate)
	DELETE FROM dbo.[dw_seller_consumption_merchant] WHERE  CONVERT(VARCHAR(8),cm_update_time,112) BETWEEN @searchStartDate and  @searchEndDate ;
	
	PRINT '刪除 [dw_seller_consumption_merchant] 既有資料: ' + CAST(@@ROWCOUNT AS VARCHAR(MAX)) +'筆。';  

	 IF OBJECT_ID('tempdb..#TMPMSETTING') IS NOT NULL
	 DROP TABLE #TMPMSETTING;  
	-- save to tmp table
	 SELECT a.* INTO #TMPMSETTING FROM (

			
		SELECT 
		      cm.cm_id
			  ,cm.cm_company_id
			  ,cm.cm_hqbanidn_no
			  ,cm.cm_invoice_no
			  ,cm.cm_merchant_no
			  ,cm.cm_name1
			  ,cm.cm_name4
			  ,cm.cm_address
			  ,cm.cm_en_name
			  ,cm.cm_business_address
			  ,cm.cm_en_address
			  ,cm.cm_contact1
			  ,cm.cm_telephone
			  ,cm.cm_cellphone
			  ,cm.cm_email1
			  ,cm.cm_is_self_generate
			  ,cm.cm_status
			  ,cm.cm_type
			  ,cm.cm_mcc_code
			  ,cm.cm_branch
			  ,cm.cm_paytool
			  ,cm.cm_self_pur_rate
			  ,cm.cm_intbk_pur_rate
			  ,cm.cm_self_refund_rate
			  ,cm.cm_intbk_refund_rate
			  ,cm.cm_self_return_rfnd_rate
			  ,cm.cm_intbk_return_rfnd_rate
			  ,cm.cm_first_year_charge
			  ,cm.cm_renewal_charge
			  ,cm.cm_max_amt_purchase_mon
			  ,cm.cm_max_amt_refund_mon
			  ,cm.cm_max_amt_purchase_day
			  ,cm.cm_max_amt_refund_day
			  ,cm.cm_discount_charge_start_time
			  ,cm.cm_discount_charge_end_time
			  ,cm.cm_discount_self_pur_rate
			  ,cm.cm_discount_intbk_pur_rate
			  ,cm.cm_allocated_day
			  ,cm.cm_refund_day
			  ,cm.cm_is_reversal
			  ,cm.cm_is_rfnd_bef_day
			  ,cm.cm_is_rfnd_aft_day
			  ,cm.cm_is_small_scale_biz
			  ,cm.cm_is_natural_person
			  ,cm.cm_withholding_ban
			  ,cm.cm_acct_bank_id
			  ,cm.cm_acct_no1
			  ,cm.cm_acct_no2
			  ,cm.cm_create_user
			  ,cm.cm_create_time
			  ,cm.cm_update_user
			  ,cm.cm_update_time
			  ,cm.cm_self_pur_amt
			  ,cm.cm_intbk_pur_amt
			  ,cm.cm_self_refund_amt
			  ,cm.cm_intbk_refund_amt
			  ,cm.cm_self_return_rfnd_amt
			  ,cm.cm_intbk_return_rfnd_amt
			  ,cm.cm_discount_self_pur_amt
			  ,cm.cm_discount_intbk_pur_amt
			  ,cm.cm_is_rfnd
			  ,cm.cm_business_zip_code
			  ,cm.cm_area_code
			  ,cm.cm_cooperation_ban
			  ,cm.cm_cooperation_acct
			  ,cm.cm_du_invoice_no
			  ,cm.cm_branch_check_code
			  ,cm.cm_max_amt_per_purchase
			  ,cm.cm_max_amt_per_refund
			  ,cm.cm_collection_type
			  ,cm.cm_responsible
			  ,cm.cm_responsible_id
			  ,cm.cm_actual_address
			  ,cm.cm_risk_level
			  ,cm.cm_dup_code
			  ,cm.cm_twpay_cellphone
			  ,cm.cm_zip_code
			  ,cm.cm_actual_zip_code
			  ,cm.cm_twpay_use_app
			  ,cm.cm_verify_date
			  ,cm.cm_verify_user
			  ,cm.cm_telephone_ext
			  ,cm.cm_setup_date
			  ,cm.cm_rfnd_method_aft_alloc_day
			  ,cm.cm_other_epay
		   ,CASE
				WHEN  dw.cm_id IS NOT NULL THEN
					'U'
				ELSE
					'A'
			END AS　action_flag
		 

		FROM
		dbo.[consumption_merchant] cm 
		left join [dw_seller_consumption_merchant] dw on cm.cm_id = dw.cm_id
		WHERE
		CONVERT(VARCHAR(8),cm.cm_update_time,112) BETWEEN @searchStartDate and  @searchEndDate


	 ) a
	PRINT '預計轉檔筆數:' + CAST(@@ROWCOUNT AS VARCHAR(MAX));  
    CREATE NONCLUSTERED INDEX ix_action_flag ON #TMPMSETTING (action_flag);
	 -- add to dw table
	IF EXISTS (SELECT 1 FROM #TMPMSETTING WHERE action_flag ='A') 	
    BEGIN
     
INSERT INTO [dbo].[dw_seller_consumption_merchant]
           ([cm_id]
		   ,[cm_company_id]
           ,[cm_hqbanidn_no]
           ,[cm_invoice_no]
           ,[cm_merchant_no]
           ,[cm_name1]
           ,[cm_name4]
           ,[cm_address]
           ,[cm_en_name]
           ,[cm_business_address]
           ,[cm_en_address]
           ,[cm_contact1]
           ,[cm_telephone]
           ,[cm_cellphone]
           ,[cm_email1]
           ,[cm_is_self_generate]
           ,[cm_status]
           ,[cm_type]
           ,[cm_mcc_code]
           ,[cm_branch]
           ,[cm_paytool]
           ,[cm_self_pur_rate]
           ,[cm_intbk_pur_rate]
           ,[cm_self_refund_rate]
           ,[cm_intbk_refund_rate]
           ,[cm_self_return_rfnd_rate]
           ,[cm_intbk_return_rfnd_rate]
           ,[cm_first_year_charge]
           ,[cm_renewal_charge]
           ,[cm_max_amt_purchase_mon]
           ,[cm_max_amt_refund_mon]
           ,[cm_max_amt_purchase_day]
           ,[cm_max_amt_refund_day]
           ,[cm_discount_charge_start_time]
           ,[cm_discount_charge_end_time]
           ,[cm_discount_self_pur_rate]
           ,[cm_discount_intbk_pur_rate]
           ,[cm_allocated_day]
           ,[cm_refund_day]
           ,[cm_is_reversal]
           ,[cm_is_rfnd_bef_day]
           ,[cm_is_rfnd_aft_day]
           ,[cm_is_small_scale_biz]
           ,[cm_is_natural_person]
           ,[cm_withholding_ban]
           ,[cm_acct_bank_id]
           ,[cm_acct_no1]
           ,[cm_acct_no2]
           ,[cm_create_user]
           ,[cm_create_time]
           ,[cm_update_user]
           ,[cm_update_time]
           ,[cm_self_pur_amt]
           ,[cm_intbk_pur_amt]
           ,[cm_self_refund_amt]
           ,[cm_intbk_refund_amt]
           ,[cm_self_return_rfnd_amt]
           ,[cm_intbk_return_rfnd_amt]
           ,[cm_discount_self_pur_amt]
           ,[cm_discount_intbk_pur_amt]
           ,[cm_is_rfnd]
           ,[cm_business_zip_code]
           ,[cm_area_code]
           ,[cm_cooperation_ban]
           ,[cm_cooperation_acct]
           ,[cm_du_invoice_no]
           ,[cm_branch_check_code]
           ,[cm_max_amt_per_purchase]
           ,[cm_max_amt_per_refund]
           ,[cm_collection_type]
           ,[cm_responsible]
           ,[cm_responsible_id]
           ,[cm_actual_address]
           ,[cm_risk_level]
           ,[cm_dup_code]
           ,[cm_twpay_cellphone]
           ,[cm_zip_code]
           ,[cm_actual_zip_code]
           ,[cm_twpay_use_app]
           ,[cm_verify_date]
           ,[cm_verify_user]
           ,[cm_telephone_ext]
           ,[cm_setup_date]
           ,[cm_rfnd_method_aft_alloc_day]
           ,[cm_other_epay])
		SELECT cm_id
		      ,cm_company_id
			  ,cm_hqbanidn_no
			  ,cm_invoice_no
			  ,cm_merchant_no
			  ,cm_name1
			  ,cm_name4
			  ,cm_address
			  ,cm_en_name
			  ,cm_business_address
			  ,cm_en_address
			  ,cm_contact1
			  ,cm_telephone
			  ,cm_cellphone
			  ,cm_email1
			  ,cm_is_self_generate
			  ,cm_status
			  ,cm_type
			  ,cm_mcc_code
			  ,cm_branch
			  ,cm_paytool
			  ,cm_self_pur_rate
			  ,cm_intbk_pur_rate
			  ,cm_self_refund_rate
			  ,cm_intbk_refund_rate
			  ,cm_self_return_rfnd_rate
			  ,cm_intbk_return_rfnd_rate
			  ,cm_first_year_charge
			  ,cm_renewal_charge
			  ,cm_max_amt_purchase_mon
			  ,cm_max_amt_refund_mon
			  ,cm_max_amt_purchase_day
			  ,cm_max_amt_refund_day
			  ,cm_discount_charge_start_time
			  ,cm_discount_charge_end_time
			  ,cm_discount_self_pur_rate
			  ,cm_discount_intbk_pur_rate
			  ,cm_allocated_day
			  ,cm_refund_day
			  ,cm_is_reversal
			  ,cm_is_rfnd_bef_day
			  ,cm_is_rfnd_aft_day
			  ,cm_is_small_scale_biz
			  ,cm_is_natural_person
			  ,cm_withholding_ban
			  ,cm_acct_bank_id
			  ,cm_acct_no1
			  ,cm_acct_no2
			  ,cm_create_user
			  ,cm_create_time
			  ,cm_update_user
			  ,cm_update_time
			  ,cm_self_pur_amt
			  ,cm_intbk_pur_amt
			  ,cm_self_refund_amt
			  ,cm_intbk_refund_amt
			  ,cm_self_return_rfnd_amt
			  ,cm_intbk_return_rfnd_amt
			  ,cm_discount_self_pur_amt
			  ,cm_discount_intbk_pur_amt
			  ,cm_is_rfnd
			  ,cm_business_zip_code
			  ,cm_area_code
			  ,cm_cooperation_ban
			  ,cm_cooperation_acct
			  ,cm_du_invoice_no
			  ,cm_branch_check_code
			  ,cm_max_amt_per_purchase
			  ,cm_max_amt_per_refund
			  ,cm_collection_type
			  ,cm_responsible
			  ,cm_responsible_id
			  ,cm_actual_address
			  ,cm_risk_level
			  ,cm_dup_code
			  ,cm_twpay_cellphone
			  ,cm_zip_code
			  ,cm_actual_zip_code
			  ,cm_twpay_use_app
			  ,cm_verify_date
			  ,cm_verify_user
			  ,cm_telephone_ext
			  ,cm_setup_date
			  ,cm_rfnd_method_aft_alloc_day
			  ,cm_other_epay
	   FROM #TMPMSETTING WHERE action_flag ='A'

		
    END
SET @insertRow = CAST(@@ROWCOUNT AS VARCHAR(MAX));
	 -- update to dw table
	IF EXISTS (SELECT 1 FROM #TMPMSETTING WHERE action_flag ='U') 	
	BEGIN
		UPDATE t1 
		   SET t1.[cm_company_id] = t2.[cm_company_id]      
      ,t1.[cm_hqbanidn_no] = t2.[cm_hqbanidn_no]      
	  ,t1.[cm_invoice_no] = t2.[cm_invoice_no]      
	  ,t1.[cm_merchant_no] = t2.[cm_merchant_no]      
	  ,t1.[cm_name1] = t2.[cm_name1]      
	  ,t1.[cm_name4] = t2.[cm_name4]     
	  ,t1.[cm_address] = t2. [cm_address]
      ,t1.[cm_en_name] = t2.[cm_en_name]      
	  ,t1.[cm_business_address] = t2.[cm_business_address]      
	  ,t1.[cm_en_address] = t2.[cm_en_address]      
	  ,t1.[cm_contact1] = t2.[cm_contact1]      
	  ,t1.[cm_telephone] = t2.[cm_telephone]      
	  ,t1.[cm_cellphone] = t2.[cm_cellphone]      
	  ,t1.[cm_email1] = t2.[cm_email1]      
	  ,t1.[cm_is_self_generate] = t2.[cm_is_self_generate] 
	  ,t1.[cm_status] = t2.[cm_status]      
	  ,t1.[cm_type] = t2.[cm_type]      
	  ,t1.[cm_mcc_code] = t2.[cm_mcc_code]    
	  ,t1.[cm_branch] = t2.[cm_branch]      
	  ,t1.[cm_paytool] = t2.[cm_paytool]      
	  ,t1.[cm_self_pur_rate] = t2.[cm_self_pur_rate]      
	  ,t1.[cm_intbk_pur_rate] = t2.[cm_intbk_pur_rate]      
	  ,t1.[cm_self_refund_rate] = t2.[cm_self_refund_rate]      
	  ,t1.[cm_intbk_refund_rate] = t2.[cm_intbk_refund_rate]      
	  ,t1.[cm_self_return_rfnd_rate] = t2.[cm_self_return_rfnd_rate]
      ,t1.[cm_intbk_return_rfnd_rate] = t2.[cm_intbk_return_rfnd_rate]      
	  ,t1.[cm_first_year_charge] = t2.[cm_first_year_charge]      
	  ,t1.[cm_renewal_charge] = t2.[cm_renewal_charge]     
	  ,t1.[cm_max_amt_purchase_mon] = t2.[cm_max_amt_purchase_mon]      
	  ,t1.[cm_max_amt_refund_mon] = t2.[cm_max_amt_refund_mon]      
	  ,t1.[cm_max_amt_purchase_day] = t2.[cm_max_amt_purchase_day]      
	  ,t1.[cm_max_amt_refund_day] = t2.[cm_max_amt_refund_day]      
	  ,t1.[cm_discount_charge_start_time] = t2.[cm_discount_charge_start_time]      
	  ,t1.[cm_discount_charge_end_time] = t2.[cm_discount_charge_end_time]      
	  ,t1.[cm_discount_self_pur_rate] = t2.[cm_discount_self_pur_rate]      
	  ,t1.[cm_discount_intbk_pur_rate] = t2.[cm_discount_intbk_pur_rate]      
	  ,t1.[cm_allocated_day] = t2.[cm_allocated_day]
      ,t1.[cm_refund_day] = t2.[cm_refund_day]      
	  ,t1.[cm_is_reversal] = t2.[cm_is_reversal]     
	  ,t1.[cm_is_rfnd_bef_day] = t2.[cm_is_rfnd_bef_day]
      ,t1.[cm_is_rfnd_aft_day] = t2.[cm_is_rfnd_aft_day]
      ,t1.[cm_is_small_scale_biz] = t2.[cm_is_small_scale_biz]
      ,t1.[cm_is_natural_person] = t2.[cm_is_natural_person]     
	  ,t1.[cm_withholding_ban] = t2.[cm_withholding_ban]     
	  ,t1.[cm_acct_bank_id] = t2.[cm_acct_bank_id]    
	  ,t1.[cm_acct_no1] = t2.[cm_acct_no1]    
	  ,t1.[cm_acct_no2] = t2.[cm_acct_no2]    
	  ,t1.[cm_create_user] = t2.[cm_create_user]
      ,t1.[cm_create_time] = t2.[cm_create_time]    
	  ,t1.[cm_update_user] = t2.[cm_update_user]   
	  ,t1.[cm_update_time] = t2.[cm_update_time]      
	  ,t1.[cm_self_pur_amt] = t2.[cm_self_pur_amt]   
	  ,t1.[cm_intbk_pur_amt] = t2.[cm_intbk_pur_amt]    
	  ,t1.[cm_self_refund_amt] = t2.[cm_self_refund_amt]
      ,t1.[cm_intbk_refund_amt] = t2.[cm_intbk_refund_amt]
      ,t1.[cm_self_return_rfnd_amt] = t2.[cm_self_return_rfnd_amt]
      ,t1.[cm_intbk_return_rfnd_amt] = t2.[cm_intbk_return_rfnd_amt]   
	  ,t1.[cm_discount_self_pur_amt] = t2.[cm_discount_self_pur_amt]     
	  ,t1.[cm_discount_intbk_pur_amt] = t2.[cm_discount_intbk_pur_amt]
      ,t1.[cm_is_rfnd] = t2.[cm_is_rfnd]      
	  ,t1.[cm_business_zip_code] = t2.[cm_business_zip_code]      
	  ,t1.[cm_area_code] = t2.[cm_area_code]      
	  ,t1.[cm_cooperation_ban] = t2.[cm_cooperation_ban]      
	  ,t1.[cm_cooperation_acct] = t2.[cm_cooperation_acct]    
	  ,t1.[cm_du_invoice_no] = t2.[cm_du_invoice_no]      
	  ,t1.[cm_branch_check_code] = t2.[cm_branch_check_code]    
	  ,t1.[cm_max_amt_per_purchase] = t2.[cm_max_amt_per_purchase] 
	  ,t1.[cm_max_amt_per_refund] = t2.[cm_max_amt_per_refund]
      ,t1.[cm_collection_type] = t2.[cm_collection_type]
      ,t1.[cm_responsible] = t2.[cm_responsible]   
	  ,t1.[cm_responsible_id] = t2.[cm_responsible_id] 
	  ,t1.[cm_actual_address] = t2.[cm_actual_address] 
	  ,t1.[cm_risk_level] = t2.[cm_risk_level] 
	  ,t1.[cm_dup_code] = t2.[cm_dup_code] 
	  ,t1.[cm_twpay_cellphone] = t2.[cm_twpay_cellphone] 
	  ,t1.[cm_zip_code] = t2.[cm_zip_code]     
	  ,t1.[cm_actual_zip_code] = t2.[cm_actual_zip_code]
      ,t1.[cm_twpay_use_app] = t2.[cm_twpay_use_app]     
	  ,t1.[cm_verify_date] = t2.[cm_verify_date] 
	  ,t1.[cm_verify_user] = t2.[cm_verify_user]   
	  ,t1.[cm_telephone_ext] = t2.[cm_telephone_ext]    
	  ,t1.[cm_setup_date] = t2.[cm_setup_date]   
	  ,t1.[cm_rfnd_method_aft_alloc_day] = t2.[cm_rfnd_method_aft_alloc_day]  
	  ,t1.[cm_other_epay] = t2.[cm_other_epay]
		FROM dbo.[dw_seller_consumption_merchant] t1 INNER JOIN #TMPMSETTING t2 ON t1.cm_id = t2.cm_id WHERE t2.action_flag = 'U' ; 
		
           
	END
SET @updateRow = CAST(@@ROWCOUNT AS VARCHAR(MAX));  
	PRINT '新增筆數:' + @insertRow  ;
	PRINT '更新筆數:' + @updateRow ;
	PRINT 'procudure [usp_update_dw_seller_consumption_merchant] 執行結束!' 


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


