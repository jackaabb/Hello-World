USE [ewallet]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[dw_seller_consumption_merchant]') AND type in (N'U'))
DROP TABLE [dbo].[dw_seller_consumption_merchant]
GO

CREATE TABLE [dbo].[dw_seller_consumption_merchant](
	[cm_id] [bigint] NOT NULL,
	[cm_company_id] [bigint] NULL,
	[cm_hqbanidn_no] [nvarchar](10) NULL,
	[cm_invoice_no] [nvarchar](10) NOT NULL,
	[cm_merchant_no] [nvarchar](15) NOT NULL,
	[cm_name1] [nvarchar](80) NOT NULL,
	[cm_name4] [nvarchar](12) NULL,
	[cm_address] [nvarchar](82) NULL,
	[cm_en_name] [nvarchar](80) NULL,
	[cm_business_address] [nvarchar](80) NULL,
	[cm_en_address] [nvarchar](240) NULL,
	[cm_contact1] [nvarchar](100) NOT NULL,
	[cm_telephone] [nvarchar](16) NOT NULL,
	[cm_cellphone] [nvarchar](10) NULL,
	[cm_email1] [nvarchar](600) NOT NULL,
	[cm_is_self_generate] [nvarchar](1) NULL,
	[cm_status] [nvarchar](1) NOT NULL,
	[cm_type] [nvarchar](1) NOT NULL,
	[cm_mcc_code] [char](4) NULL,
	[cm_branch] [nvarchar](4) NULL,
	[cm_paytool] [nvarchar](2) NULL,
	[cm_self_pur_rate] [decimal](6, 4) NULL,
	[cm_intbk_pur_rate] [decimal](6, 4) NULL,
	[cm_self_refund_rate] [decimal](6, 4) NULL,
	[cm_intbk_refund_rate] [decimal](6, 4) NULL,
	[cm_self_return_rfnd_rate] [decimal](6, 4) NULL,
	[cm_intbk_return_rfnd_rate] [decimal](6, 4) NULL,
	[cm_first_year_charge] [varchar](6) NULL,
	[cm_renewal_charge] [varchar](6) NULL,
	[cm_max_amt_purchase_mon] [bigint] NULL,
	[cm_max_amt_refund_mon] [bigint] NULL,
	[cm_max_amt_purchase_day] [bigint] NULL,
	[cm_max_amt_refund_day] [bigint] NULL,
	[cm_discount_charge_start_time] [datetime] NULL,
	[cm_discount_charge_end_time] [datetime] NULL,
	[cm_discount_self_pur_rate] [decimal](6, 4) NULL,
	[cm_discount_intbk_pur_rate] [decimal](6, 4) NULL,
	[cm_allocated_day] [char](1) NULL,
	[cm_refund_day] [varchar](3) NULL,
	[cm_is_reversal] [varchar](1) NULL,
	[cm_is_rfnd_bef_day] [varchar](1) NULL,
	[cm_is_rfnd_aft_day] [varchar](1) NULL,
	[cm_is_small_scale_biz] [varchar](1) NULL,
	[cm_is_natural_person] [varchar](1) NULL,
	[cm_withholding_ban] [varchar](8) NULL,
	[cm_acct_bank_id] [nvarchar](3) NULL,
	[cm_acct_no1] [nvarchar](80) NULL,
	[cm_acct_no2] [varchar](16) NULL,
	[cm_create_user] [nvarchar](36) NULL,
	[cm_create_time] [datetime] NULL,
	[cm_update_user] [nvarchar](36) NULL,
	[cm_update_time] [datetime] NULL,
	[cm_self_pur_amt] [bigint] NULL,
	[cm_intbk_pur_amt] [bigint] NULL,
	[cm_self_refund_amt] [bigint] NULL,
	[cm_intbk_refund_amt] [bigint] NULL,
	[cm_self_return_rfnd_amt] [bigint] NULL,
	[cm_intbk_return_rfnd_amt] [bigint] NULL,
	[cm_discount_self_pur_amt] [bigint] NULL,
	[cm_discount_intbk_pur_amt] [bigint] NULL,
	[cm_is_rfnd] [varchar](1) NULL,
	[cm_business_zip_code] [nvarchar](6) NULL,
	[cm_area_code] [nvarchar](2) NULL,
	[cm_cooperation_ban] [varchar](8) NULL,
	[cm_cooperation_acct] [varchar](16) NULL,
	[cm_du_invoice_no] [varchar](1) NULL,
	[cm_branch_check_code] [varchar](1) NULL,
	[cm_max_amt_per_purchase] [bigint] NULL,
	[cm_max_amt_per_refund] [bigint] NULL,
	[cm_collection_type] [varchar](2) NOT NULL,
	[cm_responsible] [nvarchar](40) NULL,
	[cm_responsible_id] [nvarchar](10) NULL,
	[cm_actual_address] [nvarchar](82) NULL,
	[cm_risk_level] [nvarchar](1) NULL,
	[cm_dup_code] [nvarchar](10) NULL,
	[cm_twpay_cellphone] [nvarchar](10) NULL,
	[cm_zip_code] [nvarchar](6) NULL,
	[cm_actual_zip_code] [nvarchar](6) NULL,
	[cm_twpay_use_app] [nvarchar](1) NULL,
	[cm_verify_date] [datetime] NULL,
	[cm_verify_user] [nvarchar](100) NULL,
	[cm_telephone_ext] [nvarchar](6) NULL,
	[cm_setup_date] [date] NULL,
	[cm_rfnd_method_aft_alloc_day] [char](1) NULL,
	[cm_other_epay] [nvarchar](30) NULL,
 CONSTRAINT [PK_dw_seller_consumption_merchant] PRIMARY KEY CLUSTERED 
(
	[cm_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


