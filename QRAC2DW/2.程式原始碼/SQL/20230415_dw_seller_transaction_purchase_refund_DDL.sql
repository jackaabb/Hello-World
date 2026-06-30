USE [ewallet]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[dw_seller_transaction_purchase_refund]') AND type in (N'U'))
DROP TABLE [dbo].[dw_seller_transaction_purchase_refund]
GO

CREATE TABLE [dbo].[dw_seller_transaction_purchase_refund](
	[tpr_id] [bigint] NOT NULL,
	[tpr_amt] [decimal](18, 2) NOT NULL,
	[tpr_tm_id] [bigint] NOT NULL,
	[tpr_refund_tm_id] [bigint] NOT NULL,
	[tpr_refund_tp_id] [bigint] NOT NULL,
	[tpr_remark] [nvarchar](120) NOT NULL,
	[tpr_seller_master_id] [bigint] NULL,
	[tpr_create_time] [datetime] NOT NULL,
	[tpr_txn_date] [datetime] NOT NULL,
	[tpr_txn_time] [datetime] NOT NULL,
	[tpr_fee] [decimal](18, 4) NULL,
	[tpr_acc_date] [datetime] NULL,
	[tpr_is_pos] [bit] NULL,
	[tpr_terminal_id] [nvarchar](30) NULL,
	[tpr_lanuch_from] [nvarchar](3) NULL,
	[tpr_is_order] [bit] NULL,
	[tpr_rate] [decimal](6, 4) NULL,
	[tpr_divided_fee] [decimal](18, 4) NULL,
	[tpr_host_order_no] [nvarchar](30) NULL,
	[tpr_is_refund_payment] [bit] NULL,
	[tpr_refund_payment_id] [varchar](23) NULL,
 CONSTRAINT [PK_dw_seller_transaction_purchase_refund] PRIMARY KEY CLUSTERED 
(
	[tpr_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


