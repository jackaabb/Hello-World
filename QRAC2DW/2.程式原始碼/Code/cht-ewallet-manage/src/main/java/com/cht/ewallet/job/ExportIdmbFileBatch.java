package com.cht.ewallet.job;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cht.commons.base.StatusCode;
import com.cht.ewallet.constants.Code;
import com.cht.ewallet.entity.UspExecuteInfo;
import com.cht.ewallet.exception.EwalletMonitoredException;
import com.cht.ewallet.repository.UspExecuteInfoRepository;
import com.cht.ewallet.service.ExportIDMB0002FileService;
import com.cht.ewallet.service.ExportIDMB0003FileService;
import com.cht.ewallet.service.ExportIDMB0004FileService;
import com.cht.ewallet.service.ExportIDMB0005FileService;
import com.cht.ewallet.service.ExportICMB0001FileService;
import com.cht.ewallet.service.ExportICMB0002FileService;
import com.cht.ewallet.service.ExportICMB0003FileService;
import com.cht.ewallet.service.ExportICMB0004FileService;
import com.cht.ewallet.service.ExportICMB0005FileService;
import com.cht.ewallet.service.ExportICMB0006FileService;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;

/**
 * 匯出DW資料批次作業
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "ewallet.manage.schedule", name = "enabled", matchIfMissing = true)
public class ExportIdmbFileBatch {
	
	@Autowired
	private ExportIDMB0002FileService exportIDMB0002FileService;
	
	@Autowired
	private ExportIDMB0003FileService exportIDMB0003FileService;
	
	@Autowired
	private ExportIDMB0004FileService exportIDMB0004FileService;
	
	@Autowired
	private ExportIDMB0005FileService exportIDMB0005FileService;
	

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
	private UspExecuteInfoRepository uspExecuteInfoRepository;

	@Value("${ewallet.manage.ExportIdmbFileBatch.enabled:true}")
	private boolean scheduleEnabled;
	
	@Value("${ewallet.manage.ExportIcmbFileBatch.enabled:true}")
	private boolean icmbScheduleEnabled;
	
	private DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYYMMdd");

	@Scheduled(cron = "${ewallet.manage.fwp362x.cronExpression: 0 20 2 * * ?}")
	@SchedulerLock(name = "exportIDMB0002FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doIDMB0002() throws Exception {
		if (scheduleEnabled) {
			log.info("排程交易紀錄統計批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_transaction_master");			
			exportIDMB0002FileService.createExport();
			log.info("排程交易紀錄統計批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIdmbFileBatch 排程設定關閉");
		}
	}

	@Scheduled(cron = "${ewallet.manage.fwp363x.cronExpression: 0 30 2 * * ?}")
	@SchedulerLock(name = "exportIDMB0003FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doIDMB0003() throws Exception {
		if (scheduleEnabled) {
			log.info("排程金融卡綁定資料批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_member_debitcard");	
			exportIDMB0003FileService.createExport();
			log.info("排程金融卡綁定資料批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIdmbFileBatch 排程設定關閉");
		}
	}

	@Scheduled(cron = "${ewallet.manage.fwp364x.cronExpression: 0 40 2 * * ?}")
	@SchedulerLock(name = "exportIDMB0004FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doIDMB0004() throws Exception {
		if (scheduleEnabled) {
			log.info("排程信用卡綁定資料批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_member_creditcard");	
			exportIDMB0004FileService.createExport();
			log.info("排程信用卡綁定資料批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIdmbFileBatch 排程設定關閉");
		}
	}
	@Scheduled(cron = "${ewallet.manage.fwp365x.cronExpression: 0 50 2 * * ?}")
	@SchedulerLock(name = "exportIDMB0005FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doIDMB0005() throws Exception {
		if (scheduleEnabled) {
			log.info("排程功能設定統計批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_member_setting");	
			exportIDMB0005FileService.createExport();
			log.info("排程功能設定統計批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIdmbFileBatch 排程設定關閉");
		}
	}
	
	@Scheduled(cron = "${ewallet.manage.icmb0001.cronExpression: 0 0 3 * * ?}")
	@SchedulerLock(name = "exportICMB0001FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doICMB0001() throws Exception {
		if (icmbScheduleEnabled) {
			log.info("排程功能設定統計批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_seller_company");	
			exportICMB0001FileService.createExport();
			log.info("排程功能設定統計批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIcmbFileBatch 排程設定關閉");
		}
	}
	
	@Scheduled(cron = "${ewallet.manage.icmb0002.cronExpression: 0 10 3 * * ?}")
	@SchedulerLock(name = "exportICMB0002FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doICMB0002() throws Exception {
		if (icmbScheduleEnabled) {
			log.info("排程功能設定統計批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_seller_consumption_merchant");	
			exportICMB0002FileService.createExport();
			log.info("排程功能設定統計批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIcmbFileBatch 排程設定關閉");
		}
	}
	
	@Scheduled(cron = "${ewallet.manage.icmb0003.cronExpression: 0 20 3 * * ?}")
	@SchedulerLock(name = "exportICMB0003FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doICMB0003() throws Exception {
		if (icmbScheduleEnabled) {
			log.info("排程功能設定統計批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_seller_consumption_merchant_branch");	
			exportICMB0003FileService.createExport();
			log.info("排程功能設定統計批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIcmbFileBatch 排程設定關閉");
		}
	}
	
	@Scheduled(cron = "${ewallet.manage.icmb0004.cronExpression: 0 30 3 * * ?}")
	@SchedulerLock(name = "exportICMB0004FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doICMB0004() throws Exception {
		if (icmbScheduleEnabled) {
			log.info("排程功能設定統計批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_seller_transaction_master");	
			exportICMB0004FileService.createExport();
			log.info("排程功能設定統計批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIcmbFileBatch 排程設定關閉");
		}
	}
	
	@Scheduled(cron = "${ewallet.manage.icmb0005.cronExpression: 0 40 3 * * ?}")
	@SchedulerLock(name = "exportICMB0005FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doICMB0005() throws Exception {
		if (icmbScheduleEnabled) {
			log.info("排程功能設定統計批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_seller_transaction_purchase");	
			exportICMB0005FileService.createExport();
			log.info("排程功能設定統計批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIcmbFileBatch 排程設定關閉");
		}
	}
	
	
	@Scheduled(cron = "${ewallet.manage.icmb0006.cronExpression: 0 50 3 * * ?}")
	@SchedulerLock(name = "exportICMB0006FileBatch", lockAtLeastFor = 86340000, lockAtMostFor = 86340000)
	public void doICMB0006() throws Exception {
		if (icmbScheduleEnabled) {
			log.info("排程功能設定統計批次轉檔作業開始");
			checkStoreProcedure("usp_update_dw_seller_transaction_purchase_refund");	
			exportICMB0006FileService.createExport();
			log.info("排程功能設定統計批次轉檔作業結束");
		} else {
			log.info("ewallet manage ExportIcmbFileBatch 排程設定關閉");
		}
	}
	
	/**
	 * DW 資料來源，於store procedure 每日跑前一天的資料放置在 dw_開頭的table 因此，批次起來需判斷 store procedure
	 * 前一天的資料是否有正常轉檔。 若無，則拋IT 監控 INFO ，不執行產檔作業。
	 * 
	 * @param uspName
	 *            store procedure name
	 */
	private void checkStoreProcedure(String uspName) {

		String yesterday = LocalDateTime.now().minusDays(1).toString(formatter);
		UspExecuteInfo executeInfo = uspExecuteInfoRepository.findOne(uspName);
		if (executeInfo != null && !StringUtils.equals(yesterday, executeInfo.getUspDate())) {
			log.error("dw 轉檔store procedure :" + uspName + "無正常執行完畢! ");
			throw new EwalletMonitoredException(Code.DW_STORE_PROCEDURE_ERROR,
					"dw 轉檔store procedure :" + uspName + "無正常執行完畢! ", StatusCode.Level.ERROR);
		}
	}

}
