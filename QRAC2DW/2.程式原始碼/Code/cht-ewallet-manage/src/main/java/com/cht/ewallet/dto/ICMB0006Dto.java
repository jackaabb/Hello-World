package com.cht.ewallet.dto;


import java.math.BigDecimal;

import lombok.Data;

/**
 * DW同步資料傳輸物件-消費購物退貨收單資料
 */
@Data
public class ICMB0006Dto {

	private String tprId;
	private BigDecimal tprAmt;
	private String tprTmId;
	private String tprRefundTmId;
	private String tprRefundTpId;
	private String tprRemark;
	private String tprSellerMasterId;
	private String tprCreateTime;
	private String tprTxnDate;
	private String tprTxnTime;
	private BigDecimal tprFee;
	private String tprAccDate;
	private String tprIsPos;
	private String tprTerminalId;
	private String tprLanuchFrom;
	private String tprIsOrder;
	private BigDecimal tprRate;
	private BigDecimal tprDividedFee;
	private String tprHostOrderNo;
	private String tprIsRefundPayment;
	private String tprRefundPaymentId;

}
