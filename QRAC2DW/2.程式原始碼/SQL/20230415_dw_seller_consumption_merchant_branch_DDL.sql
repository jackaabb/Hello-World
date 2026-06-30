USE [ewallet]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[dw_seller_consumption_merchant_branch]') AND type in (N'U'))
DROP TABLE [dbo].[dw_seller_consumption_merchant_branch]
GO

CREATE TABLE [dbo].[dw_seller_consumption_merchant_branch](
	[cmb_id] [bigint] NOT NULL,
	[cmb_merchant_id] [bigint] NOT NULL,
	[cmb_merchant_no] [nvarchar](15) NOT NULL,
	[cmb_no] [char](4) NULL,
	[cmb_name] [nvarchar](80) NULL,
	[cmb_simple_name] [nvarchar](20) NULL,
	[cmb_name_en] [nvarchar](22) NULL,
	[cmb_city] [nvarchar](14) NULL,
	[cmb_address] [nvarchar](80) NULL,
	[cmb_cus_telephone] [nvarchar](16) NULL,
	[cmb_status] [nvarchar](1) NOT NULL,
	[cmb_type] [nvarchar](1) NOT NULL,
	[cmb_acct_bank_id] [varchar](3) NULL,
	[cmb_acct_no1] [varchar](16) NULL,
	[cmb_acct_no2] [varchar](16) NULL,
	[cmb_is_need_refund_pwd] [char](1) NULL,
	[cmb_transfer_bank_code] [nvarchar](3) NULL,
	[cmb_transfer_accou] [nvarchar](19) NULL,
	[cmb_transfer_name] [nvarchar](40) NULL,
	[cmb_transfer_branch_code] [nvarchar](4) NULL,
	[cmb_update_time] [datetime] NULL,
 CONSTRAINT [PK_dw_seller_consumption_merchant_branch] PRIMARY KEY CLUSTERED 
(
	[cmb_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


