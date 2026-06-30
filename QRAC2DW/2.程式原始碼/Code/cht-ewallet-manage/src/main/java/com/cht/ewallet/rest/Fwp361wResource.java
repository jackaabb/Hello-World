package com.cht.ewallet.rest;

import com.cht.cms.manage.features.Messages;
import com.cht.commons.base.StatusCode.Level;
import com.cht.commons.base.text.XssStringEscaper;
import com.cht.commons.web.Alerter;
import com.cht.ewallet.annotation.ApplicationAudit;
import com.cht.ewallet.constants.Code;
import com.cht.ewallet.dto.Fwp354xDisbursementEmailDto;
import com.cht.ewallet.dto.Fwp354xDisbursementInfoDto;
import com.cht.ewallet.entity.Company;
import com.cht.ewallet.exception.EwalletMonitoredException;
import com.cht.ewallet.formbean.Fwp361wFormBean;
import com.cht.ewallet.repository.CompanyRepository;
import com.cht.ewallet.service.*;
import com.cht.ewallet.utils.DisbursementUtils;
import com.cht.ewallet.utils.Utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;
/**
 * FWP361W-收單批次執行功能 資源
 */
@RestController
@RequestMapping("FWP361W")
@PreAuthorize("hasAuthority('AUTHORITY_FWP361W')")
@ApplicationAudit(function = "FWP361W")
@Slf4j
public class Fwp361wResource {

    @Autowired
    private Fwp352xService fwp352xService;

    @Autowired
    private Fwp350xService fwp350xService;

    @Autowired
    private Fwp358xService fwp358xService;

    @Autowired
    private Fwp354xBatchService fwp354xBatchService;

    @Autowired
    private Fwp367xBatchService fwp367xBatchService;

    @Autowired
    private Fwp361xBatchService fwp361xBatchService;

    @Autowired
    private Fwp360xService fwp360xService;

    @Autowired
    private Fwp366xService fwp366xService;

    @Autowired
    private Fwp371xService fwp371xService;

    @Autowired
    private Fwp372xService fwp372xService;

    @Autowired
    private Fwp373xService fwp373xService;

    @Autowired
    private Fwp374xService fwp374xService;

    @Autowired
    private Fwp375xService fwp375xService;

    @Autowired
    private Fwp376xService fwp376xService;

    @Autowired

    private Fwp378xService fwp378xService;

    @Autowired
    private Fwp379xService fwp379xService;

    @Autowired
    private Fwp380xService fwp380xService;

    @Autowired
    private Fwp381xService fwp381xService;

    @Autowired
    private Fwp382xService fwp382xService;

    @Autowired
    private Fwp383xService fwp383xService;

    @Autowired
    private Fwp384xService fwp384xService;

    @Autowired
    private Fwp385xService fwp385xService;

    @Autowired
    private Fwp386xService fwp386xService;

    @Autowired
    private Fwp490xService fwp490xService;


    @Autowired
    private Fwp368xService fwp368xService;

    @Autowired
    private Fwp370xService fwp370xService;
    
    @Autowired
    private ExportICMB0001FileService exportICMB0001FileService;
    
    @Autowired
    private ExportICMB0002FileService exportICMB0002FileService;
    
    @Autowired
    private ExportICMB0003FileService exportICMB0003FileService;
    
    @Autowired
    private ExportICMB0004FileService exportICMB0004FileService;
        
    @Autowired
    private ExportICMB0005FileService exportICMB0005FileService;
   
    @Autowired
    private ExportICMB0006FileService exportICMB0006FileService;
    
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DisbursementUtils disbursementUtils;

    @Autowired
    private Environment environment;

    @Value("${spring.profiles: production}")
    private String isProd ;

    @PostMapping(value = "/isProd", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Object> isProd(Alerter alerter) throws IOException, ParseException {
        HashMap<String, Object> result = new HashMap<>();

        result.put("result", isProd);

        return result;
    }

    @PostMapping(value = "/batch352Service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Object> batch352Service(@RequestBody Fwp361wFormBean form, Alerter alerter) throws IOException, ParseException {
        HashMap<String, Object> result = new HashMap<>();

        String resultStr = fwp352xService.synchronize(form.getTmTxnTime352());
        result.put("result", resultStr);

        return result;
    }

    @PostMapping(value = "/batch350Service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Object> batch350Service(@RequestBody Fwp361wFormBean form, Alerter alerter) throws IOException, ParseException {
        String resultStr = fwp350xService.synchronize(form.getTmTxnTime350());

        HashMap<String, Object> result = new HashMap<>();
        result.put("result", resultStr);

        return result;
    }

    @PostMapping(value = "/batch354Service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void batch354Service(@RequestBody Fwp361wFormBean form, Alerter alerter) throws IOException, ParseException {
        try {
        	
        	// store mail record
            if (StringUtils.isBlank(form.getFwp354xOption()) || "S".equalsIgnoreCase(form.getFwp354xOption())) {
                fwp354xBatchService.saveStoreMailRecord(Long.valueOf(form.getTextInput0()), null);
            }

            // email
            if (StringUtils.isBlank(form.getFwp354xOption()) || "M".equalsIgnoreCase(form.getFwp354xOption())) {
                List<Fwp354xDisbursementEmailDto> dtos = fwp354xBatchService.findMerchantDisbursement(Long.valueOf(form.getTextInput0()));
                List<Fwp354xDisbursementInfoDto> infos = fwp354xBatchService.findMerchantInfo(Long.valueOf(form.getTextInput0()), dtos);
                fwp354xBatchService.sendMerchantDisbursementEmail(dtos, infos);

                dtos = fwp354xBatchService.findCompanyDisbursement(Long.valueOf(form.getTextInput0()));
                infos = fwp354xBatchService.findCompanyInfo(Long.valueOf(form.getTextInput0()), dtos);
                fwp354xBatchService.sendCompanyDisbursementEmail(dtos, infos, Long.valueOf(form.getTextInput0()));
            }
                       
            // create receipt
            if (StringUtils.isBlank(form.getFwp354xOption()) || "R".equalsIgnoreCase(form.getFwp354xOption())) {
                fwp354xBatchService.createDisbursementReceipt(Long.valueOf(form.getTextInput0()));
            }

            alerter.success(Messages.success_create());
        } catch (EwalletMonitoredException e) {
            alerter.fatal(e.getCode().getMessage());
        } catch (Exception e) {
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/batch358Service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Object> batch358Service(@RequestBody Fwp361wFormBean form, Alerter alerter) throws IOException, ParseException {
        HashMap<String, Object> result = new HashMap<>();
        try {
            String resultStr = fwp358xService.synchronize(form.getTmTxnTime358());
            result.put("result", resultStr);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            result.put("result", e.getMessage());
        }

        return result;
    }


    @PostMapping(value = "/produceRec", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Object> produceRec(@RequestBody Fwp361wFormBean form, Alerter alerter) throws IOException, ParseException {
        fwp352xService.createRecFile(form.getProduceRec());

        HashMap<String, Object> result = new HashMap<>();
        result.put("result", "ok");

        return result;
    }

    @PostMapping(value = "/batch367Service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void batch367Service(@RequestBody Fwp361wFormBean form, Alerter alerter) throws IOException, ParseException {
        try {
            String errorMsg = fwp367xBatchService.saveFindAndGenerateDisbursementDetail(Long.valueOf(form.getTextInput0()));
            if (errorMsg.length() > 0) {
                throw new EwalletMonitoredException(Code.DISBURSEMENT_POS_FILE_ERROR, errorMsg, Level.ERROR);
            }
            alerter.success(Messages.success_create());
        } catch (EwalletMonitoredException e) {
            alerter.fatal(e.getCode().getMessage());
        } catch (Exception e) {
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/batch360Service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Object> batch360Service(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        HashMap<String, Object> result = new HashMap<>();

        String resultStr = fwp360xService.saveAndSendRenewalAnnualFee(form.getTmTxnTime360());
        result.put("result", resultStr);

        return result;
    }

    @PostMapping(value = "/batch361Service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void batch361Service(@RequestBody Fwp361wFormBean form, Alerter alerter) throws IOException, ParseException {
        try {
            // email
            fwp361xBatchService.saveBatchGenerateAndUploadBillPaymentFile();
            alerter.success(Messages.success_create());
        } catch (EwalletMonitoredException e) {
            alerter.fatal(e.getCode().getMessage());
        } catch (Exception e) {
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/batch366Service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Object> batch362Service(@RequestBody Fwp361wFormBean form, Alerter alerter)
            throws IOException, ParseException {
        HashMap<String, Object> result = new HashMap<>();

        try {
            String resultStr = fwp366xService.synchronize(form.getTmTxnTime366());
            result.put("result", resultStr);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            result.put("result", e.getMessage());
        }

        return result;

    }

    /**
     * Fwp360x首年年費寄信通知
     * @param form
     * @param alerter
     * @throws Exception
     */
    @PostMapping(value = "/sendFirst360Service", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void sendFirst360Service(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            if (StringUtils.isNotBlank(form.getCompanyIdnBan())) {
                //撈CID
                Company company = companyRepository.getByInvoiceNo(form.getCompanyIdnBan());
                if(null != company) {
                    List<Long> cidList = new ArrayList<Long>();
                    cidList.add(company.getCId());
                    String msg = fwp360xService.saveAndSendFirstYearAnnualFee(cidList);
                    alerter.success(msg);
                }else {
                    alerter.warn("請輸入統編");
                }
            } else {
                alerter.warn("請輸入統編");
            }

        } catch (EwalletMonitoredException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getCode().getMessage());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }

    }

    @PostMapping(value = "/gen371xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen371xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp371xService.createReport(form.getTwPayReportTime371());
            fwp371xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen372xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen372xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp372xService.createReport(form.getTwPayReportTime372());
            fwp372xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen373xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen373xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp373xService.createReport(form.getTwPayReportTime373());
            fwp373xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen374xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen374xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp374xService.createReport(form.getTwPayReportTime374());
            fwp374xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen375xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen375xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp375xService.createReport(form.getTwPayReportTime375());
            fwp375xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen376xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen376xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp376xService.createReport(form.getTwPayReportTime376());
            fwp376xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen378xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen378xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp378xService.createReport(form.getTwPayReportTime378());
            fwp378xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen379xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen379xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp379xService.createReport(form.getTwPayReportTime379());
            fwp379xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen380xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen380xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp380xService.createReport(form.getTwPayReportTime380());
            fwp380xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen381xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen381xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp381xService.createReport(form.getTwPayReportTime381());
            fwp381xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen382xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen382xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp382xService.createReport(form.getTwPayReportTime382());
            fwp382xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/batch368x", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void batch368x(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            fwp368xService.saveUpdateBusinessCalendarAfterTwoMonths();
            LocalDate today = LocalDate.now();
            //fwp368xService.saveUpdateBusinessDay(today);
            alerter.success(Messages.success_update());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/getDisbursementDate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getDisbursementDate(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        String disbursementDate = null;
        try {
            disbursementDate = disbursementUtils.getDisbursementDate(form.getAccountDate(), form.getAllocatedDays());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
        return XssStringEscaper.escape(disbursementDate);
    }

    @PostMapping(value = "/batch490x", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void batch490x(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            fwp490xService.saveUpdateCreditcardTransactionRealCardNo(form.getTmTxnTime490());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen383xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen383xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String fileName = fwp383xService.createReport(form.getTwPayReportTime383());
            fwp383xService.uploadFile(fileName);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen384xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen384xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            fwp384xService.createReport(form.getTwPayReportTime384());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen385xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen385xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            Path path = fwp385xService.downloadFile();
            int numOfRecords = fwp385xService.saveToDB(path);
            String fileName = fwp385xService.createReport(numOfRecords);
            if (StringUtils.isNotBlank(fileName)) {
                fwp385xService.uploadFile(fileName);
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/gen386xReport", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void gen386xReport(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        try {
            String hqbanidns = StringUtils.trimToNull(environment.getProperty("ewallet.manage.Fwp386xBatch.hqbanidns"));
            if (hqbanidns != null) {
                List<String> hqbanidnLs = Arrays.asList(StringUtils.splitByWholeSeparatorPreserveAllTokens(hqbanidns, ","));
                hqbanidnLs = hqbanidnLs.stream().map(h -> StringUtils.trimToEmpty(h)).collect(Collectors.toList());
                String fileName = fwp386xService.createReport(form.getTwPayReportTime386(), hqbanidnLs);
                fwp386xService.uploadFile(fileName);
            } else {
                alerter.warn("應退店家手續費報表-LLMTP006 總公司統編/身份證字號 未設定");
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alerter.fatal(e.getMessage());
        }
    }

    @PostMapping(value = "/batch370x", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void batch370x(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        String partialFileName = "WalletListIcon_".concat(form.getWalletListTime().toString("yyyyMMdd"));
        //TODO  請將fileName組合後帶入
        /*fileName 規則
         * 錢包清單：WalletListIcon_[yyyymmdd][nn].txt
         * yyyy: 西元年
         * mm: 月
         * dd: 日
         * nn: 流水碼序號
         * */
        String newestFileName = fwp370xService.getNewestFileName(partialFileName);

        try {
            // 下載檔案，拋錯不影響之後流程..
            fwp370xService.saveDownLoadWalletListFile(newestFileName);
        } catch (Exception e) {
            log.info(ExceptionUtils.getStackTrace(e));
        }
        try {
            // 解析檔案..
            fwp370xService.saveParsingFile(newestFileName);
        } catch (Exception e) {
            log.info(ExceptionUtils.getStackTrace(e));
        }
    }
    
    @PostMapping(value = "/exportIdmbFileBatch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportIdmbFileBatch(@RequestBody Fwp361wFormBean form, Alerter alerter) throws Exception {
        String dateName = null;
      //若畫面有輸入日期，則撈取該日，若為空則撈取昨天的資料(只撈取一天的資料，避免資料過多)
		if (StringUtils.isNotBlank(form.getExecBatchTime())) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat outSdf = new SimpleDateFormat("yyyyMMdd");
			Date d = (Date) sdf.parse(form.getExecBatchTime());
			dateName = outSdf.format(d);
			log.info(" if dateName="+dateName);
		}
     
        try {
			if (StringUtils.isNotBlank(dateName)) {
				exportICMB0001FileService.createExport(dateName);
				exportICMB0002FileService.createExport(dateName);
				exportICMB0003FileService.createExport(dateName);
				exportICMB0004FileService.createExport(dateName);
				exportICMB0005FileService.createExport(dateName);
				exportICMB0006FileService.createExport(dateName);
			} else {
				exportICMB0001FileService.createExport();
				exportICMB0002FileService.createExport();
				exportICMB0003FileService.createExport();
				exportICMB0004FileService.createExport();
				exportICMB0005FileService.createExport();
				exportICMB0006FileService.createExport();

			}
        	
        } catch (Exception e) {
            log.info(ExceptionUtils.getStackTrace(e));
        }
   
    }
}
