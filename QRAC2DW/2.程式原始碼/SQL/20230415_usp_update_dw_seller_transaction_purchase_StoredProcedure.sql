

/****** Object:  StoredProcedure [dbo].[usp_update_dw_seller_transaction_purchase]    Script Date: 2023/4/13 下午 03:42:45 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE  PROCEDURE [dbo].[usp_update_dw_seller_transaction_purchase] (
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

	
	PRINT 'procudure [usp_update_dw_seller_transaction_purchase] 執行開始!' ;

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
	IF EXISTS (SELECT 1 FROM dbo.dw_seller_transaction_purchase tp left join transaction_master tm on tm.tm_id = tp.tp_tm_id and tm.update_time is not null WHERE CONVERT(VARCHAR(8), tm.update_time ,112) BETWEEN @searchStartDate and @searchEndDate) 	
	--DELETE FROM dbo.dw_seller_transaction_purchase WHERE c_update_time BETWEEN @searchStartDate and  @searchEndDate ;
	DELETE FROM dbo.dw_seller_transaction_purchase where tp_tm_id in (select tm_id from transaction_master where CONVERT(VARCHAR(8), update_time ,112) BETWEEN @searchStartDate and @searchEndDate )
	PRINT '刪除 [dw_seller_transaction_purchase] 既有資料: ' + CAST(@@ROWCOUNT AS VARCHAR(MAX)) +'筆。';  

	 IF OBJECT_ID('tempdb..#TMPMSETTING') IS NOT NULL
	 DROP TABLE #TMPMSETTING;  
	-- save to tmp table
	 SELECT a.* INTO #TMPMSETTING FROM (

			
		 SELECT  
		 tp.tp_id 
		,tp.tp_tm_id 
		,tp.tmp_tm_id 
		,tp.paytool 
		,tp.tp_order_no 
		,tp.tp_is_self_bank 
		,tp.tp_merchant_id 
		,tp.tp_bank_no 
		,tp.tp_merchant_no 
		,tp.tp_term_no 
		,tp.tp_TAC 
		,tp.tp_in_acct 
		,tp.tp_note 
		,tp.tp_rate 
		,tp.tp_is_order 
		,tp.tp_barcode_id 
		,tp.tp_branch_id 
		,tp.tp_seller_master_id 
		,tp.tp_deadline 
		,tp.tp_channel_id 
		,tp.tp_devckd 
		,tp.tp_chip_seq 
		,tp.tp_chip_memo 
		,tp.tp_mcc_branchbank 
		,tp.tp_category 
		,tp.tp_fee 
		,tp.tp_dev_type 
		,tp.tp_acc_date 
		,tp.tp_is_anti 
		,tp.tp_is_pos 
		,tp.tp_ic_tac_time 
		,tp.tp_terminal_id 
		,tp.tp_refund_barcode_id 
		,tp.tp_divided_fee 
		,tp.tp_term_id 
		,tp.tp_host_order_no 
		,tp.tp_is_self_merchant 
		,tp.tp_bank_name 
		,tp.tp_in_bank_no 
		,tp.tp_in_bank_name 
		,tp.tp_interaction_info1 
		,tp.tp_interaction_info2 
		,tp.tp_interaction_info3
		,tp.tp_interaction_info1_value 
		,tp.tp_interaction_info2_value 
		,tp.tp_interaction_info3_value 
		,tp.tp_is_payer_info 
		,tp.tp_payer_info 
		,tp.tp_transfer_discount_max_times 
		,tp.tp_transfer_discount_used_times 
		,tp.tp_transfer_name 
		,tp.tp_transferee_name 
		,tp.tp_balance 
		,tp.tp_in_transfer_discount_period 
		,tp.tp_is_order_no_in_qrcode 
		,tp.tp_divided_rate 
		,tp.tp_epay_irf 
		   ,CASE
				WHEN dw.tp_id IS NOT NULL THEN
					'U'
				ELSE
					'A'
			END AS　action_flag

		FROM
		dbo.transaction_purchase tp 
		left join dw_seller_transaction_purchase dw on tp.tp_id = dw.tp_id
		left join transaction_master tm on tm.tm_id = tp.tp_tm_id and tm.update_time is not null 
	    WHERE CONVERT(VARCHAR(8), tm.update_time ,112) BETWEEN @searchStartDate and @searchEndDate

	 ) a
	PRINT '預計轉檔筆數:' + CAST(@@ROWCOUNT AS VARCHAR(MAX));  
    CREATE NONCLUSTERED INDEX ix_action_flag ON #TMPMSETTING (action_flag);
	 -- add to dw table
	IF EXISTS (SELECT 1 FROM #TMPMSETTING WHERE action_flag ='A') 	
    BEGIN
      --insert sql
	  
INSERT INTO [dbo].[dw_seller_transaction_purchase]
           ([tp_id]
		   ,[tp_tm_id]
           ,[tmp_tm_id]
           ,[paytool]
           ,[tp_order_no]
           ,[tp_is_self_bank]
           ,[tp_merchant_id]
           ,[tp_bank_no]
           ,[tp_merchant_no]
           ,[tp_term_no]
           ,[tp_TAC]
           ,[tp_in_acct]
           ,[tp_note]
           ,[tp_rate]
           ,[tp_is_order]
           ,[tp_barcode_id]
           ,[tp_branch_id]
           ,[tp_seller_master_id]
           ,[tp_deadline]
           ,[tp_channel_id]
           ,[tp_devckd]
           ,[tp_chip_seq]
           ,[tp_chip_memo]
           ,[tp_mcc_branchbank]
           ,[tp_category]
           ,[tp_fee]
           ,[tp_dev_type]
           ,[tp_acc_date]
           ,[tp_is_anti]
           ,[tp_is_pos]
           ,[tp_ic_tac_time]
           ,[tp_terminal_id]
           ,[tp_refund_barcode_id]
           ,[tp_divided_fee]
           ,[tp_term_id]
           ,[tp_host_order_no]
           ,[tp_is_self_merchant]
           ,[tp_bank_name]
           ,[tp_in_bank_no]
           ,[tp_in_bank_name]
           ,[tp_interaction_info1]
           ,[tp_interaction_info2]
           ,[tp_interaction_info3]
           ,[tp_interaction_info1_value]
           ,[tp_interaction_info2_value]
           ,[tp_interaction_info3_value]
           ,[tp_is_payer_info]
           ,[tp_payer_info]
           ,[tp_transfer_discount_max_times]
           ,[tp_transfer_discount_used_times]
           ,[tp_transfer_name]
           ,[tp_transferee_name]
           ,[tp_balance]
           ,[tp_in_transfer_discount_period]
           ,[tp_is_order_no_in_qrcode]
           ,[tp_divided_rate]
           ,[tp_epay_irf])
	 SELECT
         tp_id	 
	    ,tp_tm_id 
		,tmp_tm_id 
		,paytool 
		,tp_order_no 
		,tp_is_self_bank 
		,tp_merchant_id 
		,tp_bank_no 
		,tp_merchant_no 
		,tp_term_no 
		,tp_TAC 
		,tp_in_acct 
		,tp_note 
		,tp_rate 
		,tp_is_order 
		,tp_barcode_id 
		,tp_branch_id 
		,tp_seller_master_id 
		,tp_deadline 
		,tp_channel_id 
		,tp_devckd 
		,tp_chip_seq 
		,tp_chip_memo 
		,tp_mcc_branchbank 
		,tp_category 
		,tp_fee 
		,tp_dev_type 
		,tp_acc_date 
		,tp_is_anti 
		,tp_is_pos 
		,tp_ic_tac_time 
		,tp_terminal_id 
		,tp_refund_barcode_id 
		,tp_divided_fee 
		,tp_term_id 
		,tp_host_order_no 
		,tp_is_self_merchant 
		,tp_bank_name 
		,tp_in_bank_no 
		,tp_in_bank_name 
		,tp_interaction_info1 
		,tp_interaction_info2 
		,tp_interaction_info3
		,tp_interaction_info1_value 
		,tp_interaction_info2_value 
		,tp_interaction_info3_value 
		,tp_is_payer_info 
		,tp_payer_info 
		,tp_transfer_discount_max_times 
		,tp_transfer_discount_used_times 
		,tp_transfer_name 
		,tp_transferee_name 
		,tp_balance 
		,tp_in_transfer_discount_period 
		,tp_is_order_no_in_qrcode 
		,tp_divided_rate 
		,tp_epay_irf 

	   FROM #TMPMSETTING WHERE action_flag ='A'

		
    END
SET @insertRow = CAST(@@ROWCOUNT AS VARCHAR(MAX));
	 -- update to dw table
	IF EXISTS (SELECT 1 FROM #TMPMSETTING WHERE action_flag ='U') 	
	BEGIN
		UPDATE t1 set
	   t1.tp_tm_id = t2.tp_tm_id
      ,t1.tmp_tm_id = t2.tmp_tm_id
      ,t1.paytool = t2.paytool
      ,t1.tp_order_no = t2.tp_order_no
      ,t1.tp_is_self_bank = t2.tp_is_self_bank
      ,t1.tp_merchant_id = t2.tp_merchant_id
      ,t1.tp_bank_no = t2.tp_bank_no
      ,t1.tp_merchant_no = t2.tp_merchant_no
      ,t1.tp_term_no = t2.tp_term_no
      ,t1.tp_TAC = t2.tp_TAC
      ,t1.tp_in_acct = t2.tp_in_acct
      ,t1.tp_note = t2.tp_note
      ,t1.tp_rate = t2.tp_rate
      ,t1.tp_is_order = t2.tp_is_order
      ,t1.tp_barcode_id = t2.tp_barcode_id
      ,t1.tp_branch_id = t2.tp_branch_id
      ,t1.tp_seller_master_id = t2.tp_seller_master_id
      ,t1.tp_deadline = t2.tp_deadline
      ,t1.tp_channel_id = t2.tp_channel_id
      ,t1.tp_devckd = t2.tp_devckd
      ,t1.tp_chip_seq = t2.tp_chip_seq
      ,t1.tp_chip_memo = t2.tp_chip_memo
      ,t1.tp_mcc_branchbank = t2.tp_mcc_branchbank
      ,t1.tp_category = t2.tp_category
      ,t1.tp_fee = t2.tp_fee
      ,t1.tp_dev_type = t2.tp_dev_type
      ,t1.tp_acc_date = t2.tp_acc_date
      ,t1.tp_is_anti = t2.tp_is_anti
      ,t1.tp_is_pos = t2.tp_is_pos
      ,t1.tp_ic_tac_time = t2.tp_ic_tac_time
      ,t1.tp_terminal_id = t2.tp_terminal_id
      ,t1.tp_refund_barcode_id = t2.tp_refund_barcode_id
      ,t1.tp_divided_fee = t2.tp_divided_fee
      ,t1.tp_term_id = t2.tp_term_id
      ,t1.tp_host_order_no = t2.tp_host_order_no
      ,t1.tp_is_self_merchant = t2.tp_is_self_merchant
      ,t1.tp_bank_name = t2.tp_bank_name
      ,t1.tp_in_bank_no = t2.tp_in_bank_no
      ,t1.tp_in_bank_name = t2.tp_in_bank_name
      ,t1.tp_interaction_info1 = t2.tp_interaction_info1
      ,t1.tp_interaction_info2 = t2.tp_interaction_info2
      ,t1.tp_interaction_info3 = t2.tp_interaction_info3
      ,t1.tp_interaction_info1_value = t2.tp_interaction_info1_value
      ,t1.tp_interaction_info2_value = t2.tp_interaction_info2_value
      ,t1.tp_interaction_info3_value = t2.tp_interaction_info3_value
      ,t1.tp_is_payer_info = t2.tp_is_payer_info
      ,t1.tp_payer_info = t2.tp_payer_info
      ,t1.tp_transfer_discount_max_times = t2.tp_transfer_discount_max_times
      ,t1.tp_transfer_discount_used_times = t2.tp_transfer_discount_used_times
      ,t1.tp_transfer_name = t2.tp_transfer_name
      ,t1.tp_transferee_name = t2.tp_transferee_name
      ,t1.tp_balance = t2.tp_balance
      ,t1.tp_in_transfer_discount_period = t2.tp_in_transfer_discount_period
      ,t1.tp_is_order_no_in_qrcode = t2.tp_is_order_no_in_qrcode
      ,t1.tp_divided_rate = t2.tp_divided_rate
      ,t1.tp_epay_irf = t2.tp_epay_irf
	FROM dbo.dw_seller_transaction_purchase t1 INNER JOIN #TMPMSETTING t2 ON t1.tp_id = t2.tp_id WHERE t2.action_flag = 'U' ; 
		
           
	END
SET @updateRow = CAST(@@ROWCOUNT AS VARCHAR(MAX));  
	PRINT '新增筆數:' + @insertRow  ;
	PRINT '更新筆數:' + @updateRow ;
	PRINT 'procudure [usp_update_dw_seller_transaction_purchase] 執行結束!' 

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
