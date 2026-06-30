package com.cht.ewallet.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * DW同步資料傳輸物件-消費購物收單資料
 */
@Data
public class ICMB0005Dto {

	private String tpId;
	private String tpTmId;
	private String tmpTmId;
	private String payTool;
	private String tpOrderNo;
	private String tpIsSelfBank;
	private String tpMerchantId;
	private String tpBankNo;
	private String tpMerchantNo;
	private String tpTermNo;
	private String tpTac;
	private String tpInAcct;
	private String tpNote;
	private BigDecimal tpRate;
	private String tpIsOrder;
	private String tpBarcodeId;
	private String tpBranchId;
	private String tpSellerMasterId;
	private String tpDeadline;
	private String tpChannelId;
	private String tpDevckd;
	private String tpChipSeq;
	private String tpChipMemo;
	private String tpMccBranchBank;
	private String tpCategory;
	private BigDecimal tpFee;
	private String tpDevType;
	private String tpAccDate;
	private String tpIsAnti;
	private String tpIsPos;
	private String tpIcTacTime;
	private String tpTerminalId;
	private String tpRefundBarcodeId;
	private BigDecimal tpDividedFee;
	private String tpTermId;
	private String tpHostOrderNo;
	private String tpIsSelfMerchant;
	private String tpBankName;
	private String tpInBankNo;
	private String tpInBankName;
	private String tpInteractionInfo1;
	private String tpInteractionInfo2;
	private String tpInteractionInfo3;
	private String tpInteractionInfo1Value;
	private String tpInteractionInfo2Value;
	private String tpInteractionInfo3Value;
	private String tpIsPayerInfo;
	private String tpPayerInfo;
	private Integer tpTransferDiscountMaxTimes;
	private Integer tpTransferDiscountUsedTimes;
	private String tpTransferName;
	private String tpTransfereeName;
	private BigDecimal tpBalance;
	private String tpInTransferDiscountPeriod;
	private String tpIsOrderNoInQrcode;
	private BigDecimal tpDividedRate;
	private BigDecimal tpEpayIrf;	

}
