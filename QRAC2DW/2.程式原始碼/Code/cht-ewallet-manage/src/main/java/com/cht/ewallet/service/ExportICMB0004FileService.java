package com.cht.ewallet.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.cht.cms.commons.util.CmsSqlPaginationHelper;
import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.Query.Builder;
import com.cht.ewallet.dto.ICMB0004Dto;
import com.cht.ewallet.entity.UspExecuteInfo;
import com.cht.ewallet.repository.UspExecuteInfoRepository;
import com.cht.ewallet.utils.IDMBFileExportColumn;
import com.cht.ewallet.utils.IDMBFileExportUtil;
import com.cht.ewallet.utils.JodaTimeFormatUtil;
import com.cht.ewallet.utils.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * ICMB0004-交易主檔收單資料 服務
 */
@Slf4j
@Service
public class ExportICMB0004FileService {

	@Autowired
	private CmsSqlPaginationHelper sqlExecutor;

	@Autowired
	private IDMBFileExportUtil idmbFileExportUtil;

	@Autowired
	private DwFtpUploadService dwFtpUploadService;

	@Autowired
	private UspExecuteInfoRepository uspExecuteInfoRepository;

	// 檔案產製位置
	@Value("${ewallet.file.send.folder:/ewalletdata/send}" + "${ewallet.manage.dw.filePath:/dw}")
	private String EXPORT_LOCATION;

	public void createExport() throws IOException {
		// get dates here
		String startDate = null;
		String endDate = null;
		String yesterday = null;
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.now().plusDays(-1);
		yesterday = date.format(format);//date.format(format); // "2019-01-23";
	    startDate = yesterday + " 00:00:00.000";
	    endDate = yesterday + " 23:59:59.999";
		
		//抓截昨日更新的資料。
	    UspExecuteInfo executeInfo = uspExecuteInfoRepository.findOne(this.getClass().getSimpleName());
		if (executeInfo == null) {
			executeInfo = new UspExecuteInfo();
			executeInfo.setUspName(this.getClass().getSimpleName());
		}

		// folder path/file name here, folder path: /dw/yyyyMMdd
		String filePath = EXPORT_LOCATION.concat(File.separator).concat(yesterday.replace("-", ""));
		String dataFileName = "ICMB0004.D";
		String controlFileName = "ICMB0004.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();

		// query here
		Builder builder = Query.builder();
		builder.append(" SELECT  ");
		builder.append(" tm_id ");
		builder.append(" ,tm_txn_no  ");
		builder.append(" ,support_pay_type   ");
		builder.append(" ,pay_type   ");
		builder.append(" ,update_time   ");
		builder.append(" ,tm_paytool   ");
		builder.append(" ,tm_seller_name   ");
		builder.append(" ,tm_amt   ");
		builder.append(" ,tm_dised_amt   ");
		builder.append(" ,tm_type   ");
		builder.append(" ,tm_status   ");
		builder.append(" ,tm_is_self_bank   ");
		builder.append(" ,tm_create_time   ");
		builder.append(" ,tm_txn_time   ");
		builder.append(" ,tm_txn_date   ");
		builder.append(" ,tm_reply_time   ");
		builder.append(" ,tm_is_confirm  ");
		builder.append(" ,tm_disamt   ");
		builder.append(" ,tm_bank_no   ");
		builder.append(" ,tm_stan   ");
		builder.append(" ,tm_qrp_trace_no   ");
		builder.append(" ,tm_qrp_local_time   ");
		builder.append(" ,tm_qrp_rs_code  ");
		builder.append(" ,tm_qrp_rs_message  ");
		builder.append(" ,tm_qrp_reply_time  ");
		builder.append(" ,tm_qrp_system_date_time  ");
		builder.append(" ,tm_result  ");
		builder.append(" ,tm_rs_code  ");
		builder.append(" ,tm_rs_message  ");
		builder.append(" ,tm_carrier_type  ");
		builder.append(" ,tm_store_memo  ");
		builder.append(" ,tm_carrier_id_1  ");
		builder.append(" ,tm_carrier_id_2  ");
		builder.append(" ,tm_srrn  ");
		builder.append(" ,tm_merchant_no  ");
		builder.append(" ,tm_term_no  ");
		builder.append(" ,tm_client_ip  ");
		builder.append(" ,tm_part_refund_count  ");
		builder.append(" ,tm_refunded_amt  ");
		builder.append(" ,tm_tip_amt  ");
		builder.append(" ,tm_pay_amt  ");
		builder.append(" ,tm_currency  ");
		builder.append(" ,tm_auth_code  ");
		builder.append(" ,tm_resp_msg_time  ");
		builder.append(" ,card_type  ");
		builder.append(" ,tm_reconcile_status  ");
		builder.append(" ,tm_is_disbursement  ");
		builder.append(" ,tm_disbursement_no  ");
		builder.append(" ,is_self_card  ");
		builder.append(" ,refund_type  ");
		builder.append(" ,branch_no  ");
		builder.append(" ,modified_date  ");
		builder.append(" ,transaction_date  ");
		builder.append(" ,account_date  ");
		builder.append(" ,acq_receive_time  ");
		builder.append(" ,acq_processed  ");
		builder.append(" ,txn_source  ");
		builder.append(" ,non_promote_amt  ");
		builder.append(" ,card_no_last_digits  ");
		builder.append(" ,expect_disb_date  ");
		builder.append(" ,tm_real_card_no  ");
		builder.append(" ,merchant_account_date  ");
		builder.append(" ,international_transaction  ");
		builder.append(" ,tm_is_epay  ");
		builder.append(" ,tm_is_epay_barcode  ");
		builder.append(" FROM dw_seller_transaction_master  ");
		builder.append("WHERE update_time >= :startDate AND update_time <= :endDate", startDate, endDate);
		log.info("ICMB0004 sql: {}", builder.build());

		// process result here
		Page<ICMB0004Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "tm_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0004Dto.class, pageable);

			List<ICMB0004Dto> result = slice.getContent();
			result.forEach(dto -> {
				setValue(dto);
			});

			String fileContent = idmbFileExportUtil.processResult("ICMB0004", columns, result);
			// generate file here,
			idmbFileExportUtil.exportDataFile(filePath, fileContent, dataFileName, slice.isFirst());
			pageable = slice.nextPageable();
			
		} while (slice.hasNext());

		idmbFileExportUtil.exportControlFile(filePath, dataFileName, controlFileName, startDate, endDate, Long.toString(slice.getTotalElements()));

		dwFtpUploadService.uploadToBoth(filePath, new String[] { dataFileName, controlFileName });

		executeInfo.setUspDate(yesterday.replace("-", ""));
		uspExecuteInfoRepository.save(executeInfo);
	}

	public void createExport(String inputDate) throws IOException {
		// get dates here
		String dateString = null;

		if (StringUtils.isNotBlank(inputDate)) {
			dateString = inputDate;
		} else {
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.now().plusDays(-1);
			dateString = date.format(format);// date.format(format); // "2019-01-23";
		}
		
		// folder path/file name here, folder path: /dw/yyyyMMdd
		String filePath = EXPORT_LOCATION.concat(File.separator).concat(dateString.replace("-", ""));
		String startTime = dateString + " 00:00:00.000";
		String endTime = dateString + " 23:59:59.999";

		String dataFileName = "ICMB0004.D";
		String controlFileName = "ICMB0004.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();

		// query here
		Builder builder = Query.builder();
		builder.append(" SELECT  ");
		builder.append(" tm_id ");
		builder.append(" ,tm_txn_no  ");
		builder.append(" ,support_pay_type   ");
		builder.append(" ,pay_type   ");
		builder.append(" ,update_time   ");
		builder.append(" ,tm_paytool   ");
		builder.append(" ,tm_seller_name   ");
		builder.append(" ,tm_amt   ");
		builder.append(" ,tm_dised_amt   ");
		builder.append(" ,tm_type   ");
		builder.append(" ,tm_status   ");
		builder.append(" ,tm_is_self_bank   ");
		builder.append(" ,tm_create_time   ");
		builder.append(" ,tm_txn_date   ");
		builder.append(" ,tm_txn_time   ");
		builder.append(" ,tm_reply_time   ");
		builder.append(" ,tm_is_confirm  ");
		builder.append(" ,tm_disamt   ");
		builder.append(" ,tm_bank_no   ");
		builder.append(" ,tm_stan   ");
		builder.append(" ,tm_qrp_trace_no   ");
		builder.append(" ,tm_qrp_local_time   ");
		builder.append(" ,tm_qrp_rs_code  ");
		builder.append(" ,tm_qrp_rs_message  ");
		builder.append(" ,tm_qrp_reply_time  ");
		builder.append(" ,tm_qrp_system_date_time  ");
		builder.append(" ,tm_result  ");
		builder.append(" ,tm_rs_code  ");
		builder.append(" ,tm_rs_message  ");
		builder.append(" ,tm_carrier_type  ");
		builder.append(" ,tm_store_memo  ");
		builder.append(" ,tm_carrier_id_1  ");
		builder.append(" ,tm_carrier_id_2  ");
		builder.append(" ,tm_srrn  ");
		builder.append(" ,tm_merchant_no  ");
		builder.append(" ,tm_term_no  ");
		builder.append(" ,tm_client_ip  ");
		builder.append(" ,tm_part_refund_count  ");
		builder.append(" ,tm_refunded_amt  ");
		builder.append(" ,tm_tip_amt  ");
		builder.append(" ,tm_pay_amt  ");
		builder.append(" ,tm_currency  ");
		builder.append(" ,tm_auth_code  ");
		builder.append(" ,tm_resp_msg_time  ");
		builder.append(" ,card_type  ");
		builder.append(" ,tm_reconcile_status  ");
		builder.append(" ,tm_is_disbursement  ");
		builder.append(" ,tm_disbursement_no  ");
		builder.append(" ,is_self_card  ");
		builder.append(" ,refund_type  ");
		builder.append(" ,branch_no  ");
		builder.append(" ,modified_date  ");
		builder.append(" ,transaction_date  ");
		builder.append(" ,account_date  ");
		builder.append(" ,acq_receive_time  ");
		builder.append(" ,acq_processed  ");
		builder.append(" ,txn_source  ");
		builder.append(" ,non_promote_amt  ");
		builder.append(" ,card_no_last_digits  ");
		builder.append(" ,expect_disb_date  ");
		builder.append(" ,tm_real_card_no  ");
		builder.append(" ,merchant_account_date  ");
		builder.append(" ,international_transaction  ");
		builder.append(" ,tm_is_epay  ");
		builder.append(" ,tm_is_epay_barcode  ");
		builder.append(" FROM dw_seller_transaction_master  ");
		builder.append("WHERE update_time >= :startTime AND update_time <= :endTime", startTime, endTime);

		log.info("ICMB0004 sql: {}", builder.build());
		// process result here
		Page<ICMB0004Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "tm_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0004Dto.class, pageable);

			List<ICMB0004Dto> result = slice.getContent();
			result.forEach(dto -> {
				setValue(dto);
			});
			String fileContent = idmbFileExportUtil.processResult("ICMB0004", columns, result);
			// generate file here,
			idmbFileExportUtil.exportDataFile(filePath, fileContent, dataFileName, slice.isFirst());
			pageable = slice.nextPageable();
			
		} while (slice.hasNext());

		idmbFileExportUtil.exportControlFile(filePath, dataFileName, controlFileName, startTime, endTime,
				Long.toString(slice.getTotalElements()));

		dwFtpUploadService.uploadToBoth(filePath, new String[] { dataFileName, controlFileName });

	}
	
	private ArrayList<IDMBFileExportColumn> genExportColumns() {
		// define columns here， 為什麼不用 annotation + reflection 呢？ 因為 java 雖然取得 fields
		// 的先後順序跟定義時是一樣的，但是 java 並沒有絕對保證是如此，故用自己定義的 ArrayList<colum definitions> 來保證
		return new ArrayList<>(Arrays.asList(
				new IDMBFileExportColumn.Builder("tmId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tmTxnNo").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("supportPayType").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("payType").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("updateTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmPaytool").withType(String.class).withLength(2).build(),
				new IDMBFileExportColumn.Builder("tmSellerName").withType(String.class).withLength(200).build(),
				new IDMBFileExportColumn.Builder("tmAmt").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tmDisedAmt").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tmType").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tmStatus").withType(String.class).withLength(2).build(),
				new IDMBFileExportColumn.Builder("tmIsSelfBank").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tmCreateTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmTxnDate").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmTxnTime").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("tmReplyTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmIsConfirm").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tmDisamt").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tmBankNo").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("tmStan").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tmQrpTraceNo").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmQrpLocalTime").withType(String.class).withLength(14).build(),
				new IDMBFileExportColumn.Builder("tmQrpRsCode").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmQrpRsMessage").withType(String.class).withLength(300).build(),
				new IDMBFileExportColumn.Builder("tmQrpReplyTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmQrpSystemDateTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmResult").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tmRsCode").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmRsMessage").withType(String.class).withLength(300).build(),
				new IDMBFileExportColumn.Builder("tmCarrierType").withType(String.class).withLength(6).build(),
				new IDMBFileExportColumn.Builder("tmStoreMemo").withType(String.class).withLength(40).build(),
				new IDMBFileExportColumn.Builder("tmCarrierId1").withType(String.class).withLength(64).build(),
				new IDMBFileExportColumn.Builder("tmCarrierId2").withType(String.class).withLength(64).build(),
				new IDMBFileExportColumn.Builder("tmSrrn").withType(String.class).withLength(64).build(),
				new IDMBFileExportColumn.Builder("tmMerchantNo").withType(String.class).withLength(40).build(),
				new IDMBFileExportColumn.Builder("tmTermNo").withType(String.class).withLength(26).build(),
				new IDMBFileExportColumn.Builder("tmClientIp").withType(String.class).withLength(40).build(),
				new IDMBFileExportColumn.Builder("tmPartRefundCount").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tmRefundedAmt").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tmTipAmt").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tmPayAmt").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tmCurrency").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("tmAuthCode").withType(String.class).withLength(6).build(),
				new IDMBFileExportColumn.Builder("tmRespMsgTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cardType").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tmReconcileStatus").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tmIsDisbursement").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tmDisbursementNo").withType(String.class).withLength(16).build(),
				new IDMBFileExportColumn.Builder("isSelfCard").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("refundType").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("branchNo").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("modifiedDate").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("transactionDate").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("accountDate").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("acqReceiveTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("acqProcessed").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("txnSource").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("nonPromoteAmt").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cardNoLastDigits").withType(String.class).withLength(4).build(),
				new IDMBFileExportColumn.Builder("expectDisbDate").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("tmRealCardNo").withType(String.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("merchantAccountDate").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("internationalTransaction").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tmIsEpay").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tmIsEpayBarcode").withType(String.class).withLength(1).build()
		));
	}
	
	private void setValue(ICMB0004Dto dto) {
		dto.setUpdateTime(JodaTimeFormatUtil.reformat(dto.getUpdateTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		dto.setTmCreateTime(JodaTimeFormatUtil.reformat(dto.getTmCreateTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		dto.setTmTxnDate(JodaTimeFormatUtil.reformat(dto.getTmTxnDate(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		dto.setTmTxnTime(JodaTimeFormatUtil.reformat(dto.getTmTxnTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "HHmmss"));
		dto.setTmReplyTime(JodaTimeFormatUtil.reformat(dto.getTmReplyTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		if (dto.getTmQrpReplyTime() != null) {
			dto.setTmQrpReplyTime(JodaTimeFormatUtil.reformat(dto.getTmQrpReplyTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		}
		if (dto.getTmRespMsgTime() != null) {
			dto.setTmRespMsgTime(JodaTimeFormatUtil.reformat(dto.getTmRespMsgTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		}
		if (dto.getAcqReceiveTime() != null) {
			dto.setAcqReceiveTime(JodaTimeFormatUtil.reformat(dto.getAcqReceiveTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		}

		if (dto.getTmDisamt() == null) {
			dto.setTmDisamt(0);
		}
		if (dto.getTmPartRefundCount() == null) {
			dto.setTmPartRefundCount(0);
		}
		if (dto.getNonPromoteAmt() == null) {
			dto.setNonPromoteAmt(0);
		}
		
		BigDecimal bgZeroValue = new BigDecimal ("0.0000");
		if (dto.getTmAmt() == null) {
			dto.setTmAmt(bgZeroValue);
		}
		if (dto.getTmDisedAmt() == null) {
			dto.setTmDisedAmt(bgZeroValue);
		}
		if (dto.getTmRefundedAmt() == null) {
			dto.setTmRefundedAmt(bgZeroValue);
		}
		if (dto.getTmTipAmt() == null) {
			dto.setTmTipAmt(bgZeroValue);
		}
		if (dto.getTmPayAmt() == null) {
			dto.setTmPayAmt(bgZeroValue);
		}
	}
}
