USE [ewallet]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[dw_seller_company]') AND type in (N'U'))
DROP TABLE [dbo].[dw_seller_company]
GO

CREATE TABLE [dbo].[dw_seller_company](
	[c_id] [bigint] NOT NULL,
	[c_fein] [nvarchar](10) NOT NULL,
	[c_create_time] [datetime] NOT NULL,
	[c_create_user] [nvarchar](36) NULL,
	[c_update_time] [datetime] NULL,
	[c_update_user] [nvarchar](36) NULL,
	[c_admin_email] [nvarchar](600) NULL,
	[c_name] [nvarchar](60) NULL,
	[c_qrcode_is_active] [nvarchar](1) NULL,
	[c_admin_mobile] [nvarchar](20) NULL,
	[c_host_branch] [varchar](3) NULL,
 CONSTRAINT [PK_dw_seller_company] PRIMARY KEY CLUSTERED 
(
	[c_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[c_fein] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


