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
import com.cht.ewallet.dto.ICMB0006Dto;
import com.cht.ewallet.entity.UspExecuteInfo;
import com.cht.ewallet.repository.UspExecuteInfoRepository;
import com.cht.ewallet.utils.IDMBFileExportColumn;
import com.cht.ewallet.utils.IDMBFileExportUtil;
import com.cht.ewallet.utils.JodaTimeFormatUtil;
import com.cht.ewallet.utils.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * ICMB0006-消費購物退貨收單資料服務
 */
@Slf4j
@Service
public class ExportICMB0006FileService {

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
		String dataFileName = "ICMB0006.D";
		String controlFileName = "ICMB0006.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();
		
		// query here
		Builder builder = Query.builder();
		builder.append(" SELECT  ");
		builder.append(" tpr_id ");
		builder.append(",tpr_amt ");
		builder.append(",tpr_tm_id ");
		builder.append(",tpr_refund_tm_id ");
		builder.append(",tpr_refund_tp_id ");
		builder.append(",tpr_remark ");
		builder.append(",tpr_seller_master_id  ");
		builder.append(",tpr_create_time ");
		builder.append(",tpr_txn_date ");
		builder.append(",tpr_txn_time ");
		builder.append(",tpr_fee ");
		builder.append(",tpr_acc_date ");
		builder.append(",tpr_is_pos ");
		builder.append(",tpr_terminal_id ");
		builder.append(",tpr_lanuch_from  ");
		builder.append(",tpr_is_order ");
		builder.append(",tpr_rate ");
		builder.append(",tpr_divided_fee ");
		builder.append(",tpr_host_order_no ");
		builder.append(",tpr_is_refund_payment ");
		builder.append(",tpr_refund_payment_id ");
		builder.append("FROM dw_seller_transaction_purchase_refund ");

		builder.append("WHERE tpr_txn_time >= :startDate AND tpr_txn_time <= :endDate", startDate, endDate);
		log.info("ICMB0006 sql: {}", builder.build());
		
		// process result here
		Page<ICMB0006Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "tpr_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0006Dto.class, pageable);

			List<ICMB0006Dto> result = slice.getContent();
			result.forEach(dto -> {
				this.setValue(dto);
			});

			String fileContent = idmbFileExportUtil.processResult("ICMB0006", columns, result);
			// generate file here,
			idmbFileExportUtil.exportDataFile(filePath, fileContent, dataFileName, slice.isFirst());
			pageable = slice.nextPageable();
			
		} while (slice.hasNext());

		idmbFileExportUtil.exportControlFile(filePath, dataFileName, controlFileName, startDate, endDate, Long.toString(slice.getTotalElements()));

		idmbFileExportUtil.uploadFile(
				new String[] {dataFileName, controlFileName },
				new String[] {filePath, filePath }, 
				new String[] { dataFileName, controlFileName });
		 
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
		
		String dataFileName = "ICMB0006.D";
		String controlFileName = "ICMB0006.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();
		
		// query here
		Builder builder = Query.builder();
		builder.append(" SELECT  ");
		builder.append(" tpr_id ");
		builder.append(",tpr_amt ");
		builder.append(",tpr_tm_id ");
		builder.append(",tpr_refund_tm_id ");
		builder.append(",tpr_refund_tp_id ");
		builder.append(",tpr_remark ");
		builder.append(",tpr_seller_master_id  ");
		builder.append(",tpr_create_time ");
		builder.append(",tpr_txn_date ");
		builder.append(",tpr_txn_time ");
		builder.append(",tpr_fee ");
		builder.append(",tpr_acc_date ");
		builder.append(",tpr_is_pos ");
		builder.append(",tpr_terminal_id ");
		builder.append(",tpr_lanuch_from  ");
		builder.append(",tpr_is_order ");
		builder.append(",tpr_rate ");
		builder.append(",tpr_divided_fee ");
		builder.append(",tpr_host_order_no ");
		builder.append(",tpr_is_refund_payment ");
		builder.append(",tpr_refund_payment_id ");
		builder.append("FROM dw_seller_transaction_purchase_refund ");

		builder.append("WHERE tpr_txn_time >= :startTime AND tpr_txn_time <= :endTime", startTime, endTime);
		log.info("ICMB0006 sql: {}", builder.build());

		// process result here
		Page<ICMB0006Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "tpr_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0006Dto.class, pageable);

			List<ICMB0006Dto> result = slice.getContent();
			result.forEach(dto -> {
				this.setValue(dto);
			});
			String fileContent = idmbFileExportUtil.processResult("ICMB0006", columns, result);
			// generate file here,
			idmbFileExportUtil.exportDataFile(filePath, fileContent, dataFileName, slice.isFirst());

			pageable = slice.nextPageable();
		} while (slice.hasNext());

		idmbFileExportUtil.exportControlFile(filePath, dataFileName, controlFileName, startTime, endTime,
				Long.toString(slice.getTotalElements()));

		idmbFileExportUtil.uploadFile(new String[] { dataFileName, controlFileName },
				new String[] { filePath, filePath }, new String[] { dataFileName, controlFileName });
	}
	
	private ArrayList<IDMBFileExportColumn> genExportColumns() {
		// define columns here， 為什麼不用 annotation + reflection 呢？ 因為 java 雖然取得 fields
		// 的先後順序跟定義時是一樣的，但是 java 並沒有絕對保證是如此，故用自己定義的 ArrayList<colum definitions> 來保證
		return new ArrayList<>(Arrays.asList(
				new IDMBFileExportColumn.Builder("tprId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tprAmt").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tprTmId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tprRefundTmId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tprRefundTpId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tprRemark").withType(String.class).withLength(120).build(),
				new IDMBFileExportColumn.Builder("tprSellerMasterId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("tprCreateTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tprTxnDate").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tprTxnTime").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("tprFee").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tprAccDate").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("tprIsPos").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tprTerminalId").withType(String.class).withLength(30).build(),
				new IDMBFileExportColumn.Builder("tprLanuchFrom").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("tprIsOrder").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tprRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tprDividedFee").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("tprHostOrderNo").withType(String.class).withLength(30).build(),
				new IDMBFileExportColumn.Builder("tprIsRefundPayment").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("tprRefundPaymentId").withType(String.class).withLength(23).build()
		));
	}
	
	private void setValue(ICMB0006Dto dto) {
		dto.setTprCreateTime(JodaTimeFormatUtil.reformat(dto.getTprCreateTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		dto.setTprAccDate(JodaTimeFormatUtil.reformat(dto.getTprAccDate(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		dto.setTprTxnDate(JodaTimeFormatUtil.reformat(dto.getTprTxnDate(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		dto.setTprTxnTime(JodaTimeFormatUtil.reformat(dto.getTprTxnTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "HHmmss"));
	
		BigDecimal bgZeroValue = new BigDecimal("0.0000");
		if (dto.getTprAmt() == null) {
			dto.setTprAmt(bgZeroValue);
		}
		if (dto.getTprFee() == null) {
			dto.setTprFee(bgZeroValue);
		}
		if (dto.getTprRate() == null) {
			dto.setTprRate(bgZeroValue);
		}
		if (dto.getTprDividedFee() == null) {
			dto.setTprDividedFee(bgZeroValue);
		}
	}

}
