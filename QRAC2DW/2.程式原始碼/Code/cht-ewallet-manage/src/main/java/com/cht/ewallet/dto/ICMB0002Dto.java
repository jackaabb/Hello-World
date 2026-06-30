package com.cht.ewallet.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * DW同步資料傳輸物件-消費特店檔收單資料
 */
@Data
public class ICMB0002Dto {

	private String cmId;
	private String cmCompanyId;
	private String cmHqbanidnNo;
	private String cmInvoiceNo;
	private String cmMerchantNo;
	private String cmName1;
	private String cmName4;
	private String cmAddress;
	private String cmEnName;
	private String cmBusinessAddress;
	
	private String cmEnAddress;
	private String cmContact1;
	private String cmTelephone;
	private String cmCellphone;
	private String cmEmail1;
	private String cmIsSelfGenerate;
	private String cmStatus;
	private String cmType;
	private String cmMccCode;
	private String cmBranch;
	private String cmPaytool;
	
	private BigDecimal cmSelfPurRate;
	private BigDecimal cmIntbkPurRate;
	private BigDecimal cmSelfRefundRate;
	private BigDecimal cmIntbkRefundRate;
	private BigDecimal cmSelfReturnRfndRate;
	private BigDecimal cmIntbkReturnRfndRate;
	private String cmFirstYearCharge;
	private String cmRenewalCharge;
	private Integer cmMaxAmtPurchaseMon;
	private Integer cmMaxAmtRefundMon;
	private Integer cmMaxAmtPurchaseDay;
	private Integer cmMaxAmtRefundDay;
	
	private String cmDiscountChargeStartTime;
	private String cmDiscountChargeEndTime;
	private BigDecimal cmDiscountSelfPurRate;
	private BigDecimal cmDiscountIntbkPurRate;
	private String cmAllocatedDay;
	private String cmRefundDay;
	private String cmIsReversal;
	private String cmIsRfndBefDay;
	private String cmIsRfndAftDay;
	private String cmIsSmallScaleBiz;
	private String cmIsNaturalPerson;
	
	private String cmWithholdingBan;
	private String cmAcctBankId;
	private String cmAcctNo1;
	private String cmAcctNo2;
	private String cmCreateUser;
	private String cmCreateTime;
	private String cmUpdateUser;
	private String cmUpdateTime;
	
	private Integer cmSelfPurAmt;
	private Integer cmIntbkPurAmt;
	private Integer cmSelfRefundAmt;
	private Integer cmIntbkRefundAmt;
	private Integer cmSelfReturnRfndAmt;
	private Integer cmIntbkReturnRfndAmt;
	private Integer cmDiscountSelfPurAmt;
	private Integer cmDiscountIntbkPurAmt;
	
	private String cmIsRfnd;
	private String cmBusinessZipCode;
	private String cmAreaCode;
	private String cmCooperationBan;
	private String cmCooperationAcct;
	private String cmDuInvoiceNo;
	private String cmBranchCheckCode;
	private Integer cmMaxAmtPerPurchase;
	private Integer cmMaxAmtPerRefund;
	private String cmCollectionType;
	private String cmResponsible;
	private String cmResponsibleId;
	private String cmActualAddress;
	private String cmRiskLevel;
	private String cmDupCode;
	private String cmTwpayCellphone;
	private String cmZipCode;
	private String cmActualZipCode;
	private String cmTwpayUseApp;
	private String cmVerifyDate;
	private String cmVerifyUser;
	private String cmTelephoneExt;
	private String cmSetupDate;
	private String cmRfndMethodAftAllocDay;
	private String cmOtherEpay;

}
