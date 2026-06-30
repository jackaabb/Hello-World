package com.cht.ewallet.dto;


import java.math.BigDecimal;

import lombok.Data;

/**
 * DW同步資料傳輸物件-交易主檔收單資料
 */
@Data
public class ICMB0004Dto {

	private String tmId;
	private String tmTxnNo;
	private String supportPayType;
	private String payType;
	private String updateTime;
	private String tmPaytool;
	private String tmSellerName;
	private BigDecimal tmAmt;
	private BigDecimal tmDisedAmt;
	private String tmType;
	private String tmStatus;
	private String tmIsSelfBank;
	private String tmCreateTime;
	private String tmTxnDate;
	private String tmTxnTime;
	private String tmReplyTime;
	private String tmIsConfirm;
	private Integer tmDisamt;
	private String tmBankNo;
	private String tmStan;
	private String tmQrpTraceNo;
	private String tmQrpLocalTime;
	private String tmQrpRsCode;
	private String tmQrpRsMessage;
	private String tmQrpReplyTime;
	private String tmQrpSystemDateTime;
	private String tmResult;
	private String tmRsCode;
	private String tmRsMessage;
	private String tmCarrierType;
	private String tmStoreMemo;
	private String tmCarrierId1;
	private String tmCarrierId2;
	private String tmSrrn;
	private String tmMerchantNo;
	private String tmTermNo;
	private String tmClientIp;
	private Integer tmPartRefundCount;
	private BigDecimal tmRefundedAmt;
	private BigDecimal tmTipAmt;
	private BigDecimal tmPayAmt;
	private String tmCurrency;
	private String tmAuthCode;
	private String tmRespMsgTime;
	private String cardType;
	private String tmReconcileStatus;
	private String tmIsDisbursement;
	private String tmDisbursementNo;
	private String isSelfCard;
	private String refundType;
	private String branchNo;
	private String modifiedDate;
	private String transactionDate;
	private String accountDate;
	private String acqReceiveTime;
	private String acqProcessed;
	private String txnSource;
	private Integer nonPromoteAmt;
	private String cardNoLastDigits;
	private String expectDisbDate;
	private String tmRealCardNo;
	private String merchantAccountDate;
	private String internationalTransaction;
	private String tmIsEpay;
	private String tmIsEpayBarcode;

}
