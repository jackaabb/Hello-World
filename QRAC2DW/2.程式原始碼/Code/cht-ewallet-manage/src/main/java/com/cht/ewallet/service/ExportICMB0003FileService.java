package com.cht.ewallet.service;

import java.io.File;
import java.io.IOException;
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
import com.cht.ewallet.dto.ICMB0003Dto;
import com.cht.ewallet.entity.UspExecuteInfo;
import com.cht.ewallet.repository.UspExecuteInfoRepository;
import com.cht.ewallet.utils.IDMBFileExportColumn;
import com.cht.ewallet.utils.IDMBFileExportUtil;
import com.cht.ewallet.utils.JodaTimeFormatUtil;
import com.cht.ewallet.utils.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * ICMB0003-消費分店檔收單資料 服務
 */
@Slf4j
@Service
public class ExportICMB0003FileService {

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
		
		//抓截昨日更新的資料。
	    UspExecuteInfo executeInfo = uspExecuteInfoRepository.findOne(this.getClass().getSimpleName());
		if (executeInfo == null) {
			executeInfo = new UspExecuteInfo();
			executeInfo.setUspName(this.getClass().getSimpleName());
		}

		// folder path/file name here, folder path: /dw/yyyyMMdd
		String filePath = EXPORT_LOCATION.concat(File.separator).concat(yesterday.replace("-", ""));
		String dataFileName = "ICMB0003.D";
		String controlFileName = "ICMB0003.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();

		// query here
		Builder builder = Query.builder();
		builder.append(" SELECT  ");
		builder.append(" cmb_id ");
		builder.append(",cmb_merchant_id  ");
		builder.append(",cmb_merchant_no  ");
		builder.append(",cmb_no   ");
		builder.append(",cmb_name  ");
		builder.append(",cmb_simple_name  ");
		builder.append(",cmb_name_en  ");
		builder.append(",cmb_city  "); 
		builder.append(",cmb_address  ");
		builder.append(",cmb_cus_telephone  ");
		builder.append(",cmb_status  ");
		builder.append(",cmb_type  ");
		builder.append(",cmb_acct_bank_id  ");
		builder.append(",cmb_acct_no1  ");
		builder.append(",cmb_acct_no2  ");
		builder.append(",cmb_is_need_refund_pwd  ");
		builder.append(",cmb_transfer_bank_code  ");
		builder.append(",cmb_transfer_accou  ");
		builder.append(",cmb_transfer_name  ");
		builder.append(",cmb_transfer_branch_code  ");
		builder.append(",cmb_update_time  ");
		builder.append(" FROM dw_seller_consumption_merchant_branch  ");
		builder.append("WHERE cmb_update_time >= :startDate AND cmb_update_time <= :endDate", startDate, endDate);
		log.info("ICMB0003 sql: {}", builder.build());

		// process result here
		Page<ICMB0003Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "cmb_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0003Dto.class, pageable);

			List<ICMB0003Dto> result = slice.getContent();
			result.forEach(dto -> {
				setValue(dto);
			});

			String fileContent = idmbFileExportUtil.processResult("ICMB0003", columns, result);
			// generate file here,
			idmbFileExportUtil.exportDataFile(filePath, fileContent, dataFileName, slice.isFirst());
			pageable = slice.nextPageable();
			
		} while (slice.hasNext());

		idmbFileExportUtil.exportControlFile(filePath, dataFileName, controlFileName, startDate, endDate, Long.toString(slice.getTotalElements()));
		
		idmbFileExportUtil.uploadFile(
				new String[] {dataFileName, controlFileName}, 
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
			dateString = date.format(format);//date.format(format); // "2019-01-23";
		}
		
		// folder path/file name here, folder path: /dw/yyyyMMdd
		String filePath = EXPORT_LOCATION.concat(File.separator).concat(dateString.replace("-", ""));
		String startTime =  dateString + " 00:00:00.000";
		String endTime =  dateString + " 23:59:59.999";

		String dataFileName = "ICMB0003.D";
		String controlFileName = "ICMB0003.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();

		// query here
		Builder builder = Query.builder();
		builder.append(" SELECT  ");
		builder.append(" cmb_id ");
		builder.append(",cmb_merchant_id  ");
		builder.append(",cmb_merchant_no  ");
		builder.append(",cmb_no   ");
		builder.append(",cmb_name  ");
		builder.append(",cmb_simple_name  ");
		builder.append(",cmb_name_en  ");
		builder.append(",cmb_city  "); 
		builder.append(",cmb_address  ");
		builder.append(",cmb_cus_telephone  ");
		builder.append(",cmb_status  ");
		builder.append(",cmb_type  ");
		builder.append(",cmb_acct_bank_id  ");
		builder.append(",cmb_acct_no1  ");
		builder.append(",cmb_acct_no2  ");
		builder.append(",cmb_is_need_refund_pwd  ");
		builder.append(",cmb_transfer_bank_code  ");
		builder.append(",cmb_transfer_accou  ");
		builder.append(",cmb_transfer_name  ");
		builder.append(",cmb_transfer_branch_code  ");
		builder.append(",cmb_update_time  ");
		builder.append(" FROM dw_seller_consumption_merchant_branch  ");
		builder.append("WHERE cmb_update_time >= :startTime AND cmb_update_time <= :endTime", startTime, endTime);
		
		log.info("ICMB0003 sql: {}", builder.build());
		// process result here
		Page<ICMB0003Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "cmb_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0003Dto.class, pageable);

			List<ICMB0003Dto> result = slice.getContent();
			result.forEach(dto -> {
				setValue(dto);
			});
			
			String fileContent = idmbFileExportUtil.processResult("ICMB0003", columns, result);
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
				new IDMBFileExportColumn.Builder("cmbId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("cmbMerchantId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("cmbMerchantNo").withType(String.class).withLength(15).build(),
				new IDMBFileExportColumn.Builder("cmbNo").withType(String.class).withLength(4).build(),
				new IDMBFileExportColumn.Builder("cmbName").withType(String.class).withLength(80).build(),
				new IDMBFileExportColumn.Builder("cmbSimpleName").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("cmbNameEn").withType(String.class).withLength(22).build(),
				new IDMBFileExportColumn.Builder("cmbCity").withType(String.class).withLength(14).build(),
				new IDMBFileExportColumn.Builder("cmbAddress").withType(String.class).withLength(80).build(),
				new IDMBFileExportColumn.Builder("cmbCusTelephone").withType(String.class).withLength(16).build(),
				new IDMBFileExportColumn.Builder("cmbStatus").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmbType").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmbAcctBankId").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("cmbAcctNo1").withType(String.class).withLength(16).build(),
				new IDMBFileExportColumn.Builder("cmbAcctNo2").withType(String.class).withLength(16).build(),
				new IDMBFileExportColumn.Builder("cmbIsNeedRefundPwd").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmbTransferBankCode").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("cmbTransferAccou").withType(String.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmbTransferName").withType(String.class).withLength(40).build(),
				new IDMBFileExportColumn.Builder("cmbTransferBranchCode").withType(String.class).withLength(4).build(),
				new IDMBFileExportColumn.Builder("cmbUpdateTime").withType(String.class).withLength(10).build()));
	}
	
	private void setValue(ICMB0003Dto dto) {
		 dto.setCmbUpdateTime(JodaTimeFormatUtil.reformat(dto.getCmbUpdateTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));		  
	}
}
