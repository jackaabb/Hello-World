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
import com.cht.ewallet.dto.ICMB0001Dto;
import com.cht.ewallet.entity.UspExecuteInfo;
import com.cht.ewallet.repository.UspExecuteInfoRepository;
import com.cht.ewallet.utils.IDMBFileExportColumn;
import com.cht.ewallet.utils.IDMBFileExportUtil;
import com.cht.ewallet.utils.JodaTimeFormatUtil;
import com.cht.ewallet.utils.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * ICMB0001-公司檔收單資料 服務
 */
@Slf4j
@Service
public class ExportICMB0001FileService {

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
		String dataFileName = "ICMB0001.D";
		String controlFileName = "ICMB0001.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();

		// query here
		Builder builder = Query.builder();
		builder.append("SELECT c_id,c_fein,c_create_time,c_create_user, ");
		builder.append("c_update_time,c_update_user,c_admin_email, ");
		builder.append("c_name,c_qrcode_is_active,c_admin_mobile,c_host_branch ");
		builder.append("from dw_seller_company ");
		builder.append("WHERE c_update_time >= :startDate AND c_update_time <= :endDate", startDate, endDate);
		log.info("ICMB0001 sql: {}", builder.build());

		// process result here
		Page<ICMB0001Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "c_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0001Dto.class, pageable);

			List<ICMB0001Dto> result = slice.getContent();
			result.forEach(dto -> {
				this.setValue(dto);
			});

			String fileContent = idmbFileExportUtil.processResult("ICMB0001", columns, result);
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
			dateString= inputDate;
		} else {
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.now().plusDays(-1);
			dateString = date.format(format);//date.format(format); // "2019-01-23";
		}
		
		// folder path/file name here, folder path: /dw/yyyyMMdd
		String filePath = EXPORT_LOCATION.concat(File.separator).concat(dateString.replace("-", ""));
		String startTime =  dateString + " 00:00:00.000";
		String endTime =  dateString + " 23:59:59.999";
		
		String dataFileName = "ICMB0001.D";
		String controlFileName = "ICMB0001.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();

		// query here
		Builder builder = Query.builder();
		builder.append("SELECT c_id,c_fein,c_create_time,c_create_user, ");
		builder.append("c_update_time,c_update_user,c_admin_email, ");
		builder.append("c_name,c_qrcode_is_active,c_admin_mobile,c_host_branch ");
		builder.append("from dw_seller_company ");
	    builder.append("WHERE c_update_time >= :startTime AND c_update_time <= :endTime", startTime, endTime);
		log.info("ICMB0001 sql: {}", builder.build());

		// process result here
		Page<ICMB0001Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "c_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0001Dto.class, pageable);

			List<ICMB0001Dto> result = slice.getContent();
			result.forEach(dto -> {
				this.setValue(dto);
			});

			String fileContent = idmbFileExportUtil.processResult("ICMB0001", columns, result);
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
		return new ArrayList<>(
				Arrays.asList(new IDMBFileExportColumn.Builder("cId").withType(String.class).withLength(20).build(),
						new IDMBFileExportColumn.Builder("cFein").withType(String.class).withLength(10).build(),
						new IDMBFileExportColumn.Builder("cCreateTime").withType(String.class).withLength(10).build(),
						new IDMBFileExportColumn.Builder("cCreateUser").withType(String.class).withLength(36).build(),
						new IDMBFileExportColumn.Builder("cUpdateTime").withType(String.class).withLength(10).build(),
						new IDMBFileExportColumn.Builder("cUpdateUser").withType(String.class).withLength(36).build(),
						new IDMBFileExportColumn.Builder("cAdminEmail").withType(String.class).withLength(600).build(),
						new IDMBFileExportColumn.Builder("cName").withType(String.class).withLength(60).build(),
						new IDMBFileExportColumn.Builder("cQrcodeIsActive").withType(String.class).withLength(1).build(),
						new IDMBFileExportColumn.Builder("cAdminMobile").withType(String.class).withLength(20).build(),
						new IDMBFileExportColumn.Builder("cHostBranch").withType(String.class).withLength(3).build()
						));
	}
	
	private void setValue(ICMB0001Dto dto) {
		dto.setCCreateTime(JodaTimeFormatUtil.reformat(dto.getCCreateTime(), Utils.FORMAT_DATETIME_TOMILLISECOND,"yyyy-MM-dd"));
	    dto.setCUpdateTime(JodaTimeFormatUtil.reformat(dto.getCUpdateTime(), Utils.FORMAT_DATETIME_TOMILLISECOND,"yyyy-MM-dd"));
	}

}
