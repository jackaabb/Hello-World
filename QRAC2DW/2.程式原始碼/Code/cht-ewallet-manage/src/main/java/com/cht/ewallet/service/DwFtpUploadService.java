package com.cht.ewallet.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * DW 雙 FTP 上傳服務
 * 將產製的 .D / .H 檔案同時上傳至兩組 FTP 主機
 */
@Slf4j
@Service
public class DwFtpUploadService {

    // ── 第一組 FTP 設定 ──────────────────────────────────────────
    @Value("${com.megabank.ftp.dw.host:localhost}")
    private String ftp1Host;

    @Value("${com.megabank.ftp.dw.port:21}")
    private int ftp1Port;

    @Value("${com.megabank.ftp.dw.user:ftpucb1}")
    private String ftp1User;

    @Value("${com.megabank.ftp.dw.pwd:ftpucb1}")
    private String ftp1Pwd;

    @Value("${com.megabank.ftp.dw.remotePath:ftpout}")
    private String ftp1RemotePath;

    // ── 第二組 FTP 設定 ──────────────────────────────────────────
    @Value("${com.megabank.ftp.dw2.host:localhost}")
    private String ftp2Host;

    @Value("${com.megabank.ftp.dw2.port:21}")
    private int ftp2Port;

    @Value("${com.megabank.ftp.dw2.user:ftpucb2}")
    private String ftp2User;

    @Value("${com.megabank.ftp.dw2.pwd:ftpucb2}")
    private String ftp2Pwd;

    @Value("${com.megabank.ftp.dw2.remotePath:ftpout2}")
    private String ftp2RemotePath;

    // ── 共用逾時設定 ─────────────────────────────────────────────
    @Value("${com.megabank.ftp.connectionTimeout:120}")
    private int connectionTimeout;

    @Value("${com.megabank.ftp.dataTimeout:60}")
    private int dataTimeout;

    @Value("${ewallet.ftp.upload.enabled:true}")
    private boolean uploadEnabled;

    /**
     * 將指定本地目錄內的檔案同時上傳至兩組 FTP。
     * 任一組失敗時記錄 ERROR log，但不中斷另一組上傳。
     *
     * @param localDir   本地檔案所在目錄（含日期子目錄）
     * @param fileNames  要上傳的檔案名稱陣列（如 ICMB0001.D, ICMB0001.H）
     */
    public void uploadToBoth(String localDir, String[] fileNames) {
        if (!uploadEnabled) {
            log.info("DwFtpUploadService: ewallet.ftp.upload.enabled=false，跳過 FTP 上傳");
            return;
        }

        log.info("DwFtpUploadService: 開始上傳 {} 個檔案至兩組 FTP", fileNames.length);

        upload("FTP1", ftp1Host, ftp1Port, ftp1User, ftp1Pwd, ftp1RemotePath, localDir, fileNames);
        upload("FTP2", ftp2Host, ftp2Port, ftp2User, ftp2Pwd, ftp2RemotePath, localDir, fileNames);

        log.info("DwFtpUploadService: 兩組 FTP 上傳完成");
    }

    private void upload(String label,
                        String host, int port, String user, String pwd, String remotePath,
                        String localDir, String[] fileNames) {
        FTPClient ftp = new FTPClient();
        try {
            ftp.setConnectTimeout(connectionTimeout * 1000);
            ftp.setDataTimeout(dataTimeout * 1000);

            log.info("{}: 連線至 {}:{}", label, host, port);
            ftp.connect(host, port);
            ftp.login(user, pwd);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();

            // 切換至遠端目錄（不存在時嘗試建立）
            if (!ftp.changeWorkingDirectory(remotePath)) {
                ftp.makeDirectory(remotePath);
                ftp.changeWorkingDirectory(remotePath);
            }

            for (String fileName : fileNames) {
                File localFile = new File(localDir, fileName);
                if (!localFile.exists()) {
                    log.warn("{}: 本地檔案不存在，略過 {}", label, localFile.getAbsolutePath());
                    continue;
                }
                try (InputStream is = new FileInputStream(localFile)) {
                    boolean ok = ftp.storeFile(fileName, is);
                    if (ok) {
                        log.info("{}: 上傳成功 → {}/{}", label, remotePath, fileName);
                    } else {
                        log.error("{}: 上傳失敗 → {}/{} (reply={})", label, remotePath, fileName, ftp.getReplyString());
                    }
                }
            }

        } catch (IOException e) {
            log.error("{}: FTP 上傳發生例外 host={} path={} error={}", label, host, remotePath, e.getMessage(), e);
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
