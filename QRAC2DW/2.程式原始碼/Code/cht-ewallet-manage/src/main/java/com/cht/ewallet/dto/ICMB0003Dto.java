package com.cht.ewallet.dto;

import lombok.Data;

/**
 * DW同步資料傳輸物件-消費分店檔收單資料
 */
@Data
public class ICMB0003Dto {

	private String cmbId;
	private String cmbMerchantId;
	private String cmbMerchantNo;
	private String cmbNo;
	private String cmbName;
	private String cmbSimpleName;
	private String cmbNameEn;
	private String cmbCity;
	private String cmbAddress;
	private String cmbCusTelephone;
	private String cmbStatus;
	private String cmbType;
	private String cmbAcctBankId;
	private String cmbAcctNo1;
	private String cmbAcctNo2;
	private String cmbIsNeedRefundPwd;
	private String cmbTransferBankCode;
	private String cmbTransferAccou;
	private String cmbTransferName;
	private String cmbTransferBranchCode;
	private String cmbUpdateTime;

}
