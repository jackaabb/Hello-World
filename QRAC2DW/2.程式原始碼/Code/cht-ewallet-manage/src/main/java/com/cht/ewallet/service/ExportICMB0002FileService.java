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
import com.cht.ewallet.dto.ICMB0002Dto;
import com.cht.ewallet.entity.UspExecuteInfo;
import com.cht.ewallet.repository.UspExecuteInfoRepository;
import com.cht.ewallet.utils.IDMBFileExportColumn;
import com.cht.ewallet.utils.IDMBFileExportUtil;
import com.cht.ewallet.utils.JodaTimeFormatUtil;
import com.cht.ewallet.utils.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * ICMB0002-消費特店檔收單資料 服務
 */
@Slf4j
@Service
public class ExportICMB0002FileService {

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
	    
		//抓截昨日更新的資料。
	    UspExecuteInfo executeInfo = uspExecuteInfoRepository.findOne(this.getClass().getSimpleName());
		if (executeInfo == null) {
			executeInfo = new UspExecuteInfo();
			executeInfo.setUspName(this.getClass().getSimpleName());
		}
		
		startDate = yesterday + " 00:00:00.000";
		endDate = yesterday + " 23:59:59.999";
		    
		// folder path/file name here, folder path: /dw/yyyyMMdd
		String filePath = EXPORT_LOCATION.concat(File.separator).concat(yesterday.replace("-", ""));
		String dataFileName = "ICMB0002.D";
		String controlFileName = "ICMB0002.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();

		// query here
		Builder builder = Query.builder();
		builder.append("SELECT cm_id ");
		builder.append(",cm_company_id ");
		builder.append(",cm_hqbanidn_no ");
		builder.append(",cm_invoice_no ");
		builder.append(",cm_merchant_no ");
		builder.append(",cm_name1 ");
		builder.append(",cm_name4 ");
		builder.append(",cm_address ");
		builder.append(",cm_en_name ");
		builder.append(",cm_business_address ");
		builder.append(",cm_en_address ");
		builder.append(",cm_contact1 ");
		builder.append(",cm_telephone ");
		builder.append(",cm_cellphone ");
		builder.append(",cm_email1 ");
		builder.append(",cm_is_self_generate ");
		builder.append(",cm_status ");
		builder.append(",cm_type ");
		builder.append(",cm_mcc_code ");
		builder.append(",cm_branch ");
		builder.append(",cm_paytool ");
		builder.append(",cm_self_pur_rate ");
		builder.append(",cm_intbk_pur_rate ");
		builder.append(",cm_self_refund_rate ");
		builder.append(",cm_intbk_refund_rate ");
		builder.append(",cm_self_return_rfnd_rate ");
		builder.append(",cm_intbk_return_rfnd_rate ");
		builder.append(",cm_first_year_charge ");
		builder.append(",cm_renewal_charge ");
		builder.append(",cm_max_amt_purchase_mon ");
		builder.append(",cm_max_amt_refund_mon ");
		builder.append(",cm_max_amt_purchase_day ");
		builder.append(",cm_max_amt_refund_day ");
		builder.append(",cm_discount_charge_start_time ");
		builder.append(",cm_discount_charge_end_time ");
		builder.append(",cm_discount_self_pur_rate ");
		builder.append(",cm_discount_intbk_pur_rate ");
		builder.append(",cm_allocated_day ");
		builder.append(",cm_refund_day ");
		builder.append(",cm_is_reversal ");
		builder.append(",cm_is_rfnd_bef_day ");
		builder.append(",cm_is_rfnd_aft_day ");
		builder.append(",cm_is_small_scale_biz ");
		builder.append(",cm_is_natural_person ");
		builder.append(",cm_withholding_ban ");
		builder.append(",cm_acct_bank_id ");
		builder.append(",cm_acct_no1 ");
		builder.append(",cm_acct_no2 ");
		builder.append(",cm_create_user ");
		builder.append(",cm_create_time ");
		builder.append(",cm_update_user ");
		builder.append(",cm_update_time ");
		builder.append(",cm_self_pur_amt ");
		builder.append(",cm_intbk_pur_amt ");
		builder.append(",cm_self_refund_amt ");
		builder.append(",cm_intbk_refund_amt ");
		builder.append(",cm_self_return_rfnd_amt ");
		builder.append(",cm_intbk_return_rfnd_amt ");
		builder.append(",cm_discount_self_pur_amt ");
		builder.append(",cm_discount_intbk_pur_amt ");
		builder.append(",cm_is_rfnd ");
		builder.append(",cm_business_zip_code ");
		builder.append(",cm_area_code ");
		builder.append(",cm_cooperation_ban  ");
		builder.append(",cm_cooperation_acct "); 
		builder.append(",cm_du_invoice_no ");
		builder.append(",cm_branch_check_code ");
		builder.append(",cm_max_amt_per_purchase ");
		builder.append(",cm_max_amt_per_refund ");
		builder.append(",cm_collection_type ");
		builder.append(",cm_responsible ");
		builder.append(",cm_responsible_id ");
		builder.append(",cm_actual_address ");
		builder.append(",cm_risk_level ");
		builder.append(",cm_dup_code ");
		builder.append(",cm_twpay_cellphone ");
		builder.append(",cm_zip_code ");
		builder.append(",cm_actual_zip_code ");
		builder.append(",cm_twpay_use_app ");
		builder.append(",cm_verify_date ");
		builder.append(",cm_verify_user ");
		builder.append(",cm_telephone_ext ");
		builder.append(",cm_setup_date ");
		builder.append(",cm_rfnd_method_aft_alloc_day ");
		builder.append(",cm_other_epay ");
		builder.append(" FROM dw_seller_consumption_merchant ");
		builder.append("WHERE cm_update_time >= :startDate AND cm_update_time <= :endDate", startDate, endDate);
		log.info("ICMB0002 sql: {}", builder.build());

		// process result here
		Page<ICMB0002Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "cm_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0002Dto.class, pageable);

			List<ICMB0002Dto> result = slice.getContent();
			result.forEach(dto -> {
				setValue(dto);
			});

			String fileContent = idmbFileExportUtil.processResult("ICMB0002", columns, result);
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
			dateString = date.format(format);//date.format(format); // "2019-01-23";
		}
		String filePath = EXPORT_LOCATION.concat(File.separator).concat(dateString.replace("-", ""));
		String startTime =  dateString + " 00:00:00.000";
		String endTime =  dateString + " 23:59:59.999";
		// folder path/file name here, folder path: /dw/yyyyMMdd

		String dataFileName = "ICMB0002.D";
		String controlFileName = "ICMB0002.H";

		ArrayList<IDMBFileExportColumn> columns = this.genExportColumns();

		// query here
		Builder builder = Query.builder();
		builder.append("SELECT cm_id ");
		builder.append(",cm_company_id ");
	
		builder.append(",cm_hqbanidn_no ");
		builder.append(",cm_invoice_no ");
		builder.append(",cm_merchant_no ");
		builder.append(",cm_name1 ");
		builder.append(",cm_name4 ");
		builder.append(",cm_address ");
		builder.append(",cm_en_name ");
		builder.append(",cm_business_address ");
		builder.append(",cm_en_address ");
		builder.append(",cm_contact1 ");
		builder.append(",cm_telephone ");
		builder.append(",cm_cellphone ");
		builder.append(",cm_email1 ");
		builder.append(",cm_is_self_generate ");
		builder.append(",cm_status ");
		builder.append(",cm_type ");
		builder.append(",cm_mcc_code ");
		builder.append(",cm_branch ");
		builder.append(",cm_paytool ");
		
		builder.append(",cm_self_pur_rate ");
		builder.append(",cm_intbk_pur_rate ");
		builder.append(",cm_self_refund_rate ");
		builder.append(",cm_intbk_refund_rate ");
		builder.append(",cm_self_return_rfnd_rate ");
		builder.append(",cm_intbk_return_rfnd_rate ");
		builder.append(",cm_first_year_charge ");
		builder.append(",cm_renewal_charge ");
		builder.append(",cm_max_amt_purchase_mon ");
		builder.append(",cm_max_amt_refund_mon ");
		builder.append(",cm_max_amt_purchase_day ");
		builder.append(",cm_max_amt_refund_day ");
		builder.append(",cm_discount_charge_start_time ");
		builder.append(",cm_discount_charge_end_time ");
		builder.append(",cm_discount_self_pur_rate ");
		builder.append(",cm_discount_intbk_pur_rate ");
		builder.append(",cm_allocated_day ");
		builder.append(",cm_refund_day ");
		builder.append(",cm_is_reversal ");
		builder.append(",cm_is_rfnd_bef_day ");
		builder.append(",cm_is_rfnd_aft_day ");
		builder.append(",cm_is_small_scale_biz ");
		builder.append(",cm_is_natural_person ");
		builder.append(",cm_withholding_ban ");
		builder.append(",cm_acct_bank_id ");
		builder.append(",cm_acct_no1 ");
		builder.append(",cm_acct_no2 ");
		builder.append(",cm_create_user ");
		builder.append(",cm_create_time ");
		builder.append(",cm_update_user ");
		builder.append(",cm_update_time ");
		builder.append(",cm_self_pur_amt ");
		builder.append(",cm_intbk_pur_amt ");
		builder.append(",cm_self_refund_amt ");
		builder.append(",cm_intbk_refund_amt ");
		builder.append(",cm_self_return_rfnd_amt ");
		builder.append(",cm_intbk_return_rfnd_amt ");
		builder.append(",cm_discount_self_pur_amt ");
		builder.append(",cm_discount_intbk_pur_amt ");
		builder.append(",cm_is_rfnd ");
		builder.append(",cm_business_zip_code ");
		builder.append(",cm_area_code ");
		builder.append(",cm_cooperation_ban  ");
		builder.append(",cm_cooperation_acct "); 
		builder.append(",cm_du_invoice_no ");
		builder.append(",cm_branch_check_code ");
		builder.append(",cm_max_amt_per_purchase ");
		builder.append(",cm_max_amt_per_refund ");
		builder.append(",cm_collection_type ");
		builder.append(",cm_responsible ");
		builder.append(",cm_responsible_id ");
		builder.append(",cm_actual_address ");
		builder.append(",cm_risk_level ");
		builder.append(",cm_dup_code ");
		builder.append(",cm_twpay_cellphone ");
		builder.append(",cm_zip_code ");
		builder.append(",cm_actual_zip_code ");
		builder.append(",cm_twpay_use_app ");
		builder.append(",cm_verify_date ");
		builder.append(",cm_verify_user ");
		builder.append(",cm_telephone_ext ");
		builder.append(",cm_setup_date ");
		builder.append(",cm_rfnd_method_aft_alloc_day ");
		builder.append(",cm_other_epay ");
		builder.append(" FROM dw_seller_consumption_merchant ");
		builder.append("WHERE cm_update_time >= :startTime AND cm_update_time <= :endTime", startTime, endTime);
		log.info("ICMB0002 sql: {}", builder.build());

		// process result here
		Page<ICMB0002Dto> slice = new PageImpl<>(Collections.emptyList());
		Pageable pageable = new PageRequest(0, 20000, new Sort(Direction.ASC, "cm_id"));
		do {
			slice = sqlExecutor.queryForPage(builder.build(), ICMB0002Dto.class, pageable);

			List<ICMB0002Dto> result = slice.getContent();
			result.forEach(dto -> {
				setValue(dto);
			});
			String fileContent = idmbFileExportUtil.processResult("ICMB0002", columns, result);
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
				new IDMBFileExportColumn.Builder("cmId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("cmCompanyId").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("cmHqbanidnNo").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmInvoiceNo").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmMerchantNo").withType(String.class).withLength(15).build(),
				new IDMBFileExportColumn.Builder("cmName1").withType(String.class).withLength(80).build(),
				new IDMBFileExportColumn.Builder("cmName4").withType(String.class).withLength(12).build(),
				new IDMBFileExportColumn.Builder("cmAddress").withType(String.class).withLength(82).build(),
				new IDMBFileExportColumn.Builder("cmEnName").withType(String.class).withLength(80).build(),
				new IDMBFileExportColumn.Builder("cmBusinessAddress").withType(String.class).withLength(80).build(),
				new IDMBFileExportColumn.Builder("cmEnAddress").withType(String.class).withLength(240).build(),
				new IDMBFileExportColumn.Builder("cmContact1").withType(String.class).withLength(100).build(),
				new IDMBFileExportColumn.Builder("cmTelephone").withType(String.class).withLength(16).build(),
				new IDMBFileExportColumn.Builder("cmCellphone").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmEmail1").withType(String.class).withLength(600).build(),
				new IDMBFileExportColumn.Builder("cmIsSelfGenerate").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmStatus").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmType").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmMccCode").withType(String.class).withLength(4).build(),
				new IDMBFileExportColumn.Builder("cmBranch").withType(String.class).withLength(4).build(),
				new IDMBFileExportColumn.Builder("cmPaytool").withType(String.class).withLength(2).build(),
				new IDMBFileExportColumn.Builder("cmSelfPurRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmIntbkPurRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmSelfRefundRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmIntbkRefundRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmSelfReturnRfndRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmIntbkReturnRfndRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmFirstYearCharge").withType(String.class).withLength(6).build(),
				new IDMBFileExportColumn.Builder("cmRenewalCharge").withType(String.class).withLength(6).build(),
				new IDMBFileExportColumn.Builder("cmMaxAmtPurchaseMon").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmMaxAmtRefundMon").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmMaxAmtPurchaseDay").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmMaxAmtRefundDay").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmDiscountChargeStartTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmDiscountChargeEndTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmDiscountSelfPurRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmDiscountIntbkPurRate").withType(BigDecimal.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmAllocatedDay").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmRefundDay").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("cmIsReversal").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmIsRfndBefDay").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmIsRfndAftDay").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmIsSmallScaleBiz").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmIsNaturalPerson").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmWithholdingBan").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("cmAcctBankId").withType(String.class).withLength(3).build(),
				new IDMBFileExportColumn.Builder("cmAcctNo1").withType(String.class).withLength(80).build(),
				new IDMBFileExportColumn.Builder("cmAcctNo2").withType(String.class).withLength(16).build(),
				new IDMBFileExportColumn.Builder("cmCreateUser").withType(String.class).withLength(36).build(),
				new IDMBFileExportColumn.Builder("cmCreateTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmUpdateUser").withType(String.class).withLength(36).build(),
				new IDMBFileExportColumn.Builder("cmUpdateTime").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmSelfPurAmt").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmIntbkPurAmt").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmSelfRefundAmt").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmIntbkRefundAmt").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmSelfReturnRfndAmt").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmIntbkReturnRfndAmt").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmDiscountSelfPurAmt").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmDiscountIntbkPurAmt").withType(Integer.class).withLength(19).build(),	
				new IDMBFileExportColumn.Builder("cmIsRfnd").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmBusinessZipCode").withType(String.class).withLength(6).build(),
				new IDMBFileExportColumn.Builder("cmAreaCode").withType(String.class).withLength(2).build(),
				new IDMBFileExportColumn.Builder("cmCooperationBan").withType(String.class).withLength(8).build(),
				new IDMBFileExportColumn.Builder("cmCooperationAcct").withType(String.class).withLength(16).build(),
				new IDMBFileExportColumn.Builder("cmDuInvoiceNo").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmBranchCheckCode").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmMaxAmtPerPurchase").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmMaxAmtPerRefund").withType(Integer.class).withLength(19).build(),
				new IDMBFileExportColumn.Builder("cmCollectionType").withType(String.class).withLength(2).build(),
				new IDMBFileExportColumn.Builder("cmResponsible").withType(String.class).withLength(40).build(),
				new IDMBFileExportColumn.Builder("cmResponsibleId").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmActualAddress").withType(String.class).withLength(82).build(),
				new IDMBFileExportColumn.Builder("cmRiskLevel").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmDupCode").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmTwpayCellphone").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmZipCode").withType(String.class).withLength(6).build(),
				new IDMBFileExportColumn.Builder("cmActualZipCode").withType(String.class).withLength(6).build(),
				new IDMBFileExportColumn.Builder("cmTwpayUseApp").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmVerifyDate").withType(String.class).withLength(10).build(),
				new IDMBFileExportColumn.Builder("cmVerifyUser").withType(String.class).withLength(100).build(),
				new IDMBFileExportColumn.Builder("cmTelephoneExt").withType(String.class).withLength(6).build(),
				new IDMBFileExportColumn.Builder("cmSetupDate").withType(String.class).withLength(20).build(),
				new IDMBFileExportColumn.Builder("cmRfndMethodAftAllocDay").withType(String.class).withLength(1).build(),
				new IDMBFileExportColumn.Builder("cmOtherEpay").withType(String.class).withLength(30).build()
		));
	}
	
	private void setValue(ICMB0002Dto dto) {
		dto.setCmCreateTime(JodaTimeFormatUtil.reformat(dto.getCmCreateTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
	    dto.setCmUpdateTime(JodaTimeFormatUtil.reformat(dto.getCmUpdateTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		if (dto.getCmVerifyDate() != null) {
			dto.setCmVerifyDate(JodaTimeFormatUtil.reformat(dto.getCmVerifyDate(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		}
		if(dto.getCmDiscountChargeStartTime()!=null) {
			dto.setCmDiscountChargeStartTime(JodaTimeFormatUtil.reformat(dto.getCmDiscountChargeStartTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		}
		if(dto.getCmDiscountChargeEndTime()!=null) {
			dto.setCmDiscountChargeEndTime(JodaTimeFormatUtil.reformat(dto.getCmDiscountChargeEndTime(), Utils.FORMAT_DATETIME_TOMILLISECOND, "yyyy-MM-dd"));
		}
		if (dto.getCmMaxAmtPurchaseMon() == null) {
			dto.setCmMaxAmtPurchaseMon(0);
		}
		if (dto.getCmMaxAmtRefundMon() == null) {
			dto.setCmMaxAmtRefundMon(0);
		}
		if (dto.getCmMaxAmtPurchaseDay() == null) {
			dto.setCmMaxAmtPurchaseDay(0);
		}
		if (dto.getCmMaxAmtRefundDay() == null) {
			dto.setCmMaxAmtRefundDay(0);
		}
		if (dto.getCmSelfPurAmt() == null) {
			dto.setCmSelfPurAmt(0);
		}
		if (dto.getCmIntbkPurAmt() == null) {
			dto.setCmIntbkPurAmt(0);
		}
		if (dto.getCmSelfRefundAmt() == null) {
			dto.setCmSelfRefundAmt(0);
		}
		if (dto.getCmIntbkRefundAmt() == null) {
			dto.setCmIntbkRefundAmt(0);
		}
		if (dto.getCmSelfReturnRfndAmt() == null) {
			dto.setCmSelfReturnRfndAmt(0);
		}
		if (dto.getCmIntbkReturnRfndAmt() == null) {
			dto.setCmIntbkReturnRfndAmt(0);
		}
		if (dto.getCmDiscountSelfPurAmt() == null) {
			dto.setCmDiscountSelfPurAmt(0);
		}
		if (dto.getCmDiscountIntbkPurAmt() == null) {
			dto.setCmDiscountIntbkPurAmt(0);
		}
		if (dto.getCmMaxAmtPerPurchase() == null) {
			dto.setCmMaxAmtPerPurchase(0);
		}
		if (dto.getCmMaxAmtPerRefund() == null) {
			dto.setCmMaxAmtPerRefund(0);
		}
		
		BigDecimal bgZeroValue = new BigDecimal ("0.0000");
		if (dto.getCmSelfPurRate() == null) {
			dto.setCmSelfPurRate(bgZeroValue);
		}
		if (dto.getCmSelfRefundRate() == null) {
			dto.setCmSelfRefundRate(bgZeroValue);
		}
		if (dto.getCmIntbkRefundRate() == null) {
			dto.setCmIntbkRefundRate(bgZeroValue);
		}
		if (dto.getCmSelfReturnRfndRate() == null) {
			dto.setCmSelfReturnRfndRate(bgZeroValue);
		}
		if (dto.getCmIntbkReturnRfndRate() == null) {
			dto.setCmIntbkReturnRfndRate(bgZeroValue);
		}
		if (dto.getCmDiscountSelfPurRate() == null) {
			dto.setCmDiscountSelfPurRate(bgZeroValue);
		}
		if (dto.getCmDiscountIntbkPurRate() == null) {
			dto.setCmDiscountIntbkPurRate(bgZeroValue);
		}
		if (dto.getCmIntbkPurRate() == null) {
			dto.setCmIntbkPurRate(bgZeroValue);
		}
	}
}
