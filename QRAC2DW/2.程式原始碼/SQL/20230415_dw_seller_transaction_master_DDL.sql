USE [ewallet]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[dw_seller_transaction_master]') AND type in (N'U'))
DROP TABLE [dbo].[dw_seller_transaction_master]
GO

CREATE TABLE [dbo].[dw_seller_transaction_master](
	[tm_id] [bigint] NOT NULL,
	[tm_txn_no] [nvarchar](20) NULL,
	[support_pay_type] [nvarchar](10) NULL,
	[pay_type] [nvarchar](1) NULL,
	[update_time] [datetime] NULL,
	[tm_paytool] [nvarchar](2) NULL,
	[tm_seller_name] [nvarchar](200) NULL,
	[tm_amt] [decimal](18, 2) NULL,
	[tm_dised_amt] [decimal](18, 2) NULL,
	[tm_type] [int] NULL,
	[tm_status] [nvarchar](2) NULL,
	[tm_is_self_bank] [nvarchar](1) NULL,
	[tm_create_time] [datetime] NULL,
	[tm_txn_date] [datetime] NULL,
	[tm_txn_time] [datetime] NULL,
	[tm_reply_time] [datetime] NULL,
	[tm_is_confirm] [nvarchar](1) NULL,
	[tm_disamt] [int] NULL,
	[tm_bank_no] [nvarchar](3) NULL,
	[tm_stan] [nvarchar](20) NULL,
	[tm_qrp_trace_no] [nvarchar](10) NULL,
	[tm_qrp_local_time] [nvarchar](14) NULL,
	[tm_qrp_rs_code] [nvarchar](10) NULL,
	[tm_qrp_rs_message] [nvarchar](300) NULL,
	[tm_qrp_reply_time] [datetime] NULL,
	[tm_qrp_system_date_time] [nvarchar](10) NULL,
	[tm_result] [nvarchar](1) NULL,
	[tm_rs_code] [nvarchar](10) NULL,
	[tm_rs_message] [nvarchar](300) NULL,
	[tm_carrier_type] [nvarchar](6) NULL,
	[tm_store_memo] [nvarchar](40) NULL,
	[tm_carrier_id_1] [nvarchar](64) NULL,
	[tm_carrier_id_2] [nvarchar](64) NULL,
	[tm_srrn] [nvarchar](64) NULL,
	[tm_merchant_no] [nvarchar](40) NULL,
	[tm_term_no] [nvarchar](26) NULL,
	[tm_client_ip] [nvarchar](40) NULL,
	[tm_part_refund_count] [bigint] NULL,
	[tm_refunded_amt] [decimal](18, 2) NULL,
	[tm_tip_amt] [decimal](18, 2) NULL,
	[tm_pay_amt] [decimal](18, 2) NULL,
	[tm_currency] [nvarchar](3) NULL,
	[tm_auth_code] [nvarchar](6) NULL,
	[tm_resp_msg_time] [datetime] NULL,
	[card_type] [nvarchar](10) NULL,
	[tm_reconcile_status] [nvarchar](1) NULL,
	[tm_is_disbursement] [nvarchar](1) NULL,
	[tm_disbursement_no] [nvarchar](16) NULL,
	[is_self_card] [nvarchar](1) NULL,
	[refund_type] [varchar](1) NULL,
	[branch_no] [varchar](3) NULL,
	[modified_date] [varchar](8) NULL,
	[transaction_date] [varchar](8) NULL,
	[account_date] [varchar](8) NULL,
	[acq_receive_time] [datetime] NULL,
	[acq_processed] [nvarchar](1) NULL,
	[txn_source] [nvarchar](10) NULL,
	[non_promote_amt] [int] NULL,
	[card_no_last_digits] [nvarchar](4) NULL,
	[expect_disb_date] [varchar](8) NULL,
	[tm_real_card_no] [nvarchar](19) NULL,
	[merchant_account_date] [varchar](8) NULL,
	[international_transaction] [char](1) NULL,
	[tm_is_epay] [char](1) NULL,
	[tm_is_epay_barcode] [char](1) NULL,
 CONSTRAINT [PK_dw_seller_transaction_master] PRIMARY KEY CLUSTERED 
(
	[tm_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


