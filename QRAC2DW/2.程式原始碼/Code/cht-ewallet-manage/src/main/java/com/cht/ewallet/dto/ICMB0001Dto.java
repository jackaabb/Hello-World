package com.cht.ewallet.dto;

import lombok.Data;

/**
 * DW同步資料傳輸物件-公司檔收單資料
 */
@Data
public class ICMB0001Dto {


	private String cId;
	private String cFein;
	private String cCreateTime;
	private String cCreateUser;
	private String cUpdateTime;
	private String cUpdateUser;
	private String cAdminEmail;
	private String cName;
	private String cQrcodeIsActive;
	private String cAdminMobile;
	private String cHostBranch;

}
