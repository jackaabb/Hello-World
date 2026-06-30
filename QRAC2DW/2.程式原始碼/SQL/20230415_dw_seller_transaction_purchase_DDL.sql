USE [ewallet]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[dw_seller_transaction_purchase]') AND type in (N'U'))
DROP TABLE [dbo].[dw_seller_transaction_purchase]
GO

CREATE TABLE [dbo].[dw_seller_transaction_purchase](
	[tp_id] [bigint] NOT NULL,
	[tp_tm_id] [bigint] NOT NULL,
	[tmp_tm_id] [bigint] NULL,
	[paytool] [nvarchar](2) NULL,
	[tp_order_no] [nvarchar](30) NULL,
	[tp_is_self_bank] [nvarchar](1) NOT NULL,
	[tp_merchant_id] [bigint] NULL,
	[tp_bank_no] [nvarchar](3) NOT NULL,
	[tp_merchant_no] [nvarchar](40) NULL,
	[tp_term_no] [nvarchar](8) NOT NULL,
	[tp_TAC] [nvarchar](512) NULL,
	[tp_in_acct] [nvarchar](65) NULL,
	[tp_note] [nvarchar](19) NULL,
	[tp_rate] [decimal](6, 4) NULL,
	[tp_is_order] [nvarchar](1) NOT NULL,
	[tp_barcode_id] [bigint] NULL,
	[tp_branch_id] [bigint] NULL,
	[tp_seller_master_id] [bigint] NULL,
	[tp_deadline] [nvarchar](8) NOT NULL,
	[tp_channel_id] [nvarchar](15) NOT NULL,
	[tp_devckd] [nvarchar](8) NOT NULL,
	[tp_chip_seq] [nvarchar](10) NOT NULL,
	[tp_chip_memo] [nvarchar](500) NOT NULL,
	[tp_mcc_branchbank] [nvarchar](20) NULL,
	[tp_category] [int] NULL,
	[tp_fee] [decimal](18, 4) NULL,
	[tp_dev_type] [nvarchar](4) NULL,
	[tp_acc_date] [datetime] NULL,
	[tp_is_anti] [bit] NULL,
	[tp_is_pos] [bit] NULL,
	[tp_ic_tac_time] [datetime] NULL,
	[tp_terminal_id] [nvarchar](30) NULL,
	[tp_refund_barcode_id] [bigint] NULL,
	[tp_divided_fee] [decimal](18, 4) NULL,
	[tp_term_id] [bigint] NULL,
	[tp_host_order_no] [nvarchar](30) NULL,
	[tp_is_self_merchant] [nvarchar](1) NULL,
	[tp_bank_name] [nvarchar](100) NULL,
	[tp_in_bank_no] [nvarchar](3) NULL,
	[tp_in_bank_name] [nvarchar](100) NULL,
	[tp_interaction_info1] [nvarchar](100) NULL,
	[tp_interaction_info2] [nvarchar](100) NULL,
	[tp_interaction_info3] [nvarchar](100) NULL,
	[tp_interaction_info1_value] [nvarchar](40) NULL,
	[tp_interaction_info2_value] [nvarchar](40) NULL,
	[tp_interaction_info3_value] [nvarchar](40) NULL,
	[tp_is_payer_info] [nvarchar](1) NULL,
	[tp_payer_info] [nvarchar](30) NULL,
	[tp_transfer_discount_max_times] [bigint] NULL,
	[tp_transfer_discount_used_times] [bigint] NULL,
	[tp_transfer_name] [nvarchar](100) NULL,
	[tp_transferee_name] [nvarchar](100) NULL,
	[tp_balance] [decimal](18, 2) NULL,
	[tp_in_transfer_discount_period] [nvarchar](1) NULL,
	[tp_is_order_no_in_qrcode] [nvarchar](1) NULL,
	[tp_divided_rate] [decimal](6, 4) NULL,
	[tp_epay_irf] [decimal](6, 4) NULL,
 CONSTRAINT [PK_dw_seller_transaction_purchase] PRIMARY KEY CLUSTERED 
(
	[tp_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


