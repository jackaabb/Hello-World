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
import com.cht.ewallet.dto.ICMB0005Dto;
import com.cht.ewallet.entity.UspExecuteInfo;
import com.cht.ewallet.repository.UspExecuteInfoRepository;
import com.cht.ewallet.utils.IDMBFileExportColumn;
import com.cht.ewallet.utils.IDMBFileExportUtil;
import com.cht.ewallet.utils.JodaTimeFormatUtil;
import com.cht.ewallet.utils.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * ICMB0005-消費購物收單資料 服務
 */
@Slf4j
@Service
public class ExportICMB0005FileService {

	@Autowired
	private CmsSqlPaginationHelper sqlExecutor;

	@Autowired
	private IDMBFileExportUtil idmbFileExportUtil;

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
		
		UspExecuteInfo executeInfo = uspExecuteInfoRepository.findOne(this.getClass().getSimpleName());
		//抓截昨日更新的資料。
		if (executeInfo == null) {
			executeInfo = new UspExecuteInfo();
			executeInfo.setUspName(this.getClass().getSimpleName());
		} 

		// folder path/file name here, folder path: /dw/yyyyMMdd
		String filePath = EXPORT_LOCATION.concat(File.separator).concat(yesterday.replace("-", ""));
		String dataFileName = "ICMB0005.D";
		String controlFileName = "ICMB0005.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();
		
		// query here
		Builder builder = Query.builder();
		builder.append(" SELECT  ");
		builder.append("tp.tp_id ");
		builder.append(",tp.tp_tm_id ");
		builder.append(",tp.tmp_tm_id ");
		builder.append(",tp.paytool ");
		builder.append(",tp.tp_order_no ");
		builder.append(",tp.tp_is_self_bank ");
		builder.append(",tp.tp_merchant_id ");
		builder.append(",tp.tp_bank_no ");
		builder.append(",tp.tp_merchant_no ");
		builder.append(",tp.tp_term_no ");
		builder.append(",tp.tp_TAC ");
		builder.append(",tp.tp_in_acct ");
		builder.append(",tp.tp_note ");
		builder.append(",tp.tp_rate ");
		builder.append(",tp.tp_is_order ");
		builder.append(",tp.tp_barcode_id ");
		builder.append(",tp.tp_branch_id ");
		builder.append(",tp.tp_seller_master_id ");
		builder.append(",tp.tp_deadline ");
		builder.append(",tp.tp_channel_id ");
		builder.append(",tp.tp_devckd ");
		builder.append(",tp.tp_chip_seq ");
		builder.append(",tp.tp_chip_memo ");
		builder.append(",tp.tp_mcc_branchbank ");
		builder.append(",tp.tp_category ");
		builder.append(",tp.tp_fee ");
		builder.append(",tp.tp_dev_type ");
		builder.append(",tp.tp_acc_date ");
		builder.append(",tp.tp_is_anti ");
		builder.append(",tp.tp_is_pos ");
		builder.append(",tp.tp_ic_tac_time ");
		builder.append(",tp.tp_terminal_id ");
		builder.append(",tp.tp_refund_barcode_id ");
		builder.append(",tp.tp_divided_fee ");
		builder.append(",tp.tp_term_id ");
		builder.append(",tp.tp_host_order_no ");
		builder.append(",tp.tp_is_self_merchant ");
		builder.append(",tp.tp_bank_name ");
		builder.append(",tp.tp_in_bank_no ");
		builder.append(",tp.tp_in_bank_name ");
		builder.append(",tp.tp_interaction_info1 ");
		builder.append(",tp.tp_interaction_info2 ");
		builder.append(",tp.tp_interaction_info3");
		builder.append(",tp.tp_interaction_info1_value ");
		builder.append(",tp.tp_interaction_info2_value ");
		builder.append(",tp.tp_interaction_info3_value ");
		builder.append(",tp.tp_is_payer_info ");
		builder.append(",tp.tp_payer_info ");
		builder.append(",tp.tp_transfer_discount_max_times ");
		builder.append(",tp.tp_transfer_discount_used_times ");
		builder.append(",tp.tp_transfer_name ");
		builder.append(",tp.tp_transferee_name ");
		builder.append(",tp.tp_balance ");
		builder.append(",tp.tp_in_transfer_discount_period ");
		builder.append(",tp.tp_is_order_no_in_qrcode ");
		builder.append(",tp.tp_divided_rate ");
		builder.append(",tp.tp_epay_irf ");

		builder.append(" FROM dw_seller_transaction_purchase tp ");
		builder.append(" left join transaction_master tm on tm.tm_id = tp.tp_tm_id and tm.update_time is not null ");
		builder.append("WHERE tm.update_time >= :startDate AND tm.update_time <= :endDate", startDate, endDate);
		log.info("ICMB0005 sql: {}", builder.build());

		// process result here
		Page<ICMB0005Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "tp_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0005Dto.class, pageable);

			List<ICMB0005Dto> result = slice.getContent();
			result.forEach(dto -> {
				this.setValue(dto);
			});

			String fileContent = idmbFileExportUtil.processResult("ICMB0005", columns, result);
			// generate file here,
			idmbFileExportUtil.exportDataFile(filePath, fileContent, dataFileName, slice.isFirst());
			pageable = slice.nextPageable();
			
		} while (slice.hasNext());

		idmbFileExportUtil.exportControlFile(filePath, dataFileName, controlFileName, startDate, endDate, Long.toString(slice.getTotalElements()));
		
		idmbFileExportUtil.uploadFile(
				new String[] {dataFileName, controlFileName },
				new String[] {filePath, filePath }, 
				new String[] {dataFileName, controlFileName });
		 
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

		String dataFileName = "ICMB0005.D";
		String controlFileName = "ICMB0005.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();
		
		// query here
		Builder builder = Query.builder();
		builder.append(" SELECT  ");
		builder.append("tp.tp_id ");
		builder.append(",tp.tp_tm_id ");
		builder.append(",tp.tmp_tm_id ");
		builder.append(",tp.paytool ");
		builder.append(",tp.tp_order_no ");
		builder.append(",tp.tp_is_self_bank ");
		builder.append(",tp.tp_merchant_id ");
		builder.append(",tp.tp_bank_no ");
		builder.append(",tp.tp_merchant_no ");
		builder.append(",tp.tp_term_no ");
		builder.append(",tp.tp_TAC ");
		builder.append(",tp.tp_in_acct ");
		builder.append(",tp.tp_note ");
		builder.append(",tp.tp_rate ");
		builder.append(",tp.tp_is_order ");
		builder.append(",tp.tp_barcode_id ");
		builder.append(",tp.tp_branch_id ");
		builder.append(",tp.tp_seller_master_id ");
		builder.append(",tp.tp_deadline ");
		builder.append(",tp.tp_channel_id ");
		builder.append(",tp.tp_devckd ");
		builder.append(",tp.tp_chip_seq ");
		builder.append(",tp.tp_chip_memo ");
		builder.append(",tp.tp_mcc_branchbank ");
		builder.append(",tp.tp_category ");
		builder.append(",tp.tp_fee ");
		builder.append(",tp.tp_dev_type ");
		builder.append(",tp.tp_acc_date ");
		builder.append(",tp.tp_is_anti ");
		builder.append(",tp.tp_is_pos ");
		builder.append(",tp.tp_ic_tac_time ");
		builder.append(",tp.tp_terminal_id ");
		builder.append(",tp.tp_refund_barcode_id ");
		builder.append(",tp.tp_divided_fee ");
		builder.append(",tp.tp_term_id ");
		builder.append(",tp.tp_host_order_no ");
		builder.append(",tp.tp_is_self_merchant ");
		builder.append(",tp.tp_bank_name ");
		builder.append(",tp.tp_in_bank_no ");
		builder.append(",tp.tp_in_bank_name ");
		builder.append(",tp.tp_interaction_info1 ");
		builder.append(",tp.tp_interaction_info2 ");
		builder.append(",tp.tp_interaction_info3");
		builder.append(",tp.tp_interaction_info1_value ");
		builder.append(",tp.tp_interaction_info2_value ");
		builder.append(",tp.tp_interaction_info3_value ");
		builder.append(",tp.tp_is_payer_info ");
		builder.append(",tp.tp_payer_info ");
		builder.append(",tp.tp_transfer_discount_max_times ");
		builder.append(",tp.tp_transfer_discount_used_times ");
		builder.append(",tp.tp_transfer_name ");
		builder.append(",tp.tp_transferee_name ");
		builder.append(",tp.tp_balance ");
		builder.append(",tp.tp_in_transfer_discount_period ");
		builder.append(",tp.tp_is_order_no_in_qrcode ");
		builder.append(",tp.tp_divided_rate ");
		builder.append(",tp.tp_epay_irf ");

		builder.append(" FROM dw_seller_transaction_purchase tp ");
		builder.append(" left join transaction_master tm on tm.tm_id = tp.tp_tm_id and tm.update_time is not null ");
		builder.append("WHERE tm.update_time >= :startTime AND tm.update_time <= :endTime", startTime, endTime);
		log.info("ICMB0005 sql: {}", builder.build());

		// process result here
		Page<ICMB0005Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "tp_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0005Dto.class, pageable);

			List<ICMB0005Dto> result = slice.getContent();
			result.forEach(dto -> {
				this.setValue(dto);
			});
			String fileContent = idmbFileExportUtil.processResult("ICMB0005", columns, result);
			// generate file here,
			idmbFileExportUtil.exportDataFile(filePath, fileContent, dataFileName, slice.isFirst());

			pageable = slice.nextPageable();
		} while (slice.hasNext());

		idmbFileExportUtil.exportControlFile(filePath, dataFileName, controlFileName, startTime, endTime,
				Long.toString(slice.getTotalElements()));
		
		idmbFileExportUtil.uploadFile(
				new String[] { dataFileName, controlFileName },
				new String[] { filePath, filePath }, 
				new String[] { dataFileName, controlFileName }
				);
		 
	}
	
	private ArrayList<IDMBFileExportColumn> genExportColumns() {
		// define columns here， 為什麼不用 annotation + reflection 呢？ 因為 java 雖然取得 fields
		// 的先後順序跟定義時是一樣的，但是 java 並沒有絕對保證是如此，故用自己定義的 ArrayList<colum definitions> 來保證
		return new ArrayList<>(Arrays.asList(
				new IDMBFileExportColumn.Builder("tpId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpTmId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tmpTmId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("payTool").withType(String.class).withLength(2).build(),
				new IDMBFileExportColumn.Builder("tpOrderNo").withType(String.class).withLength(30).build(),
				new IDMBFileExportColumn.Builder("tpIsSelfBank").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tpMerchantId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpBankNo").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("tpMerchantNo").withType(String.class).withLength(40).build(),
				new IDMBFileExportColumn.Builder("tpTermNo").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("tpTac").withType(String.class).withLength(512).build(),
				new IDMBFileExportColumn.Builder("tpInAcct").withType(String.class).withLength(65).build(),
				new IDMBFileExportColumn.Builder("tpNote").withType(String.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tpRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tpIsOrder").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tpBarcodeId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpBranchId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpSellerMasterId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpDeadline").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("tpChannelId").withType(String.class).withLength(15).build(),
				new IDMBFileExportColumn.Builder("tpDevckd").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("tpChipSeq").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tpChipMemo").withType(String.class).withLength(500).build(),
				new IDMBFileExportColumn.Builder("tpMccBranchBank").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpCategory").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpFee").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tpDevType").withType(String.class).withLength(4).build(),
				new IDMBFileExportColumn.Builder("tpAccDate").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tpIsAnti").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpIsPos").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpIcTacTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tpTerminalId").withType(String.class).withLength(30).build(),
				new IDMBFileExportColumn.Builder("tpRefundBarcodeId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpDividedFee").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tpTermId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tpHostOrderNo").withType(String.class).withLength(30).build(),
				new IDMBFileExportColumn.Builder("tpIsSelfMerchant").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tpBankName").withType(String.class).withLength(100).build(),
				new IDMBFileExportColumn.Builder("tpInBankNo").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("tpInBankName").withType(String.class).withLength(100).build(),
				new IDMBFileExportColumn.Builder("tpInteractionInfo1").withType(String.class).withLength(100).build(),
				new IDMBFileExportColumn.Builder("tpInteractionInfo2").withType(String.class).withLength(100).build(),
				new IDMBFileExportColumn.Builder("tpInteractionInfo3").withType(String.class).withLength(100).build(),
				new IDMBFileExportColumn.Builder("tpInteractionInfo1Value").withType(String.class).withLength(40).build(),
				new IDMBFileExportColumn.Builder("tpInteractionInfo2Value").withType(String.class).withLength(40).build(),
				new IDMBFileExportColumn.Builder("tpInteractionInfo3Value").withType(String.class).withLength(40).build(),
				new IDMBFileExportColumn.Builder("tpIsPayerInfo").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tpPayerInfo").withType(String.class).withLength(30).build(),
				new IDMBFileExportColumn.Builder("tpTransferDiscountMaxTimes").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tpTransferDiscountUsedTimes").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tpTransferName").withType(String.class).withLength(100).build(),
				new IDMBFileExportColumn.Builder("tpTransfereeName").withType(String.class).withLength(100).build(),
				new IDMBFileExportColumn.Builder("tpBalance").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tpInTransferDiscountPeriod").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tpIsOrderNoInQrcode").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tpDividedRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tpEpayIrf").withType(BigDecimal.class).withLength(19).build()
		));
	}
	
	private void setValue(ICMB0005Dto dto) {
		dto.setTpAccDate(JodaTimeFormatUtil.reformat(dto.getTpAccDate(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		dto.setTpIcTacTime(JodaTimeFormatUtil.reformat(dto.getTpIcTacTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		
		if (dto.getTpTransferDiscountMaxTimes() == null) {
			dto.setTpTransferDiscountMaxTimes(0);
		}
		if (dto.getTpTransferDiscountUsedTimes() == null) {
			dto.setTpTransferDiscountUsedTimes(0);
		}
		
		BigDecimal bgZeroValue = new BigDecimal("0.0000");
		if (dto.getTpRate() == null) {
			dto.setTpRate(bgZeroValue);
		}
		if (dto.getTpFee() == null) {
			dto.setTpFee(bgZeroValue);
		}
		if (dto.getTpDividedFee() == null) {
			dto.setTpDividedFee(bgZeroValue);
		}
		if (dto.getTpBalance() == null) {
			dto.setTpBalance(bgZeroValue);
		}
		if (dto.getTpDividedRate() == null) {
			dto.setTpDividedRate(bgZeroValue);
		}
		if (dto.getTpEpayIrf() == null) {
			dto.setTpEpayIrf(bgZeroValue);
		}


	}
}
