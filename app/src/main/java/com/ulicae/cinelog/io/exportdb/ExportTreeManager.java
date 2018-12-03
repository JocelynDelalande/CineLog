package com.ulicae.cinelog.io.exportdb;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.ulicae.cinelog.utils.FileUtilsWrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CineLog Copyright 2018 Pierre Rognon
 * <p>
 * <p>
 * This file is part of CineLog.
 * CineLog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * CineLog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with CineLog. If not, see <https://www.gnu.org/licenses/>.
 */
class ExportTreeManager {

    private FileUtilsWrapper fileUtilsWrapper;

    ExportTreeManager() {
        this(new FileUtilsWrapper());
    }

    ExportTreeManager(FileUtilsWrapper fileUtilsWrapper) {
        this.fileUtilsWrapper = fileUtilsWrapper;
    }

    void prepareTree() {
        File root = fileUtilsWrapper.getExternalStorageDirectory();

        createIfNotExist(root.getAbsolutePath() + "/CineLog/saves");
    }

    private void createIfNotExist(String path) {
        File file = fileUtilsWrapper.getFile(path);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
    }

    boolean isExportNeeded() {
        return !fileUtilsWrapper.getFile(buildExportFilePath()).exists();
    }

    FileWriter getNextExportFile() throws IOException {
        return fileUtilsWrapper.getFileWriter(new File(buildExportFilePath()));
    }

    void clean() {
        File root = fileUtilsWrapper.getExternalStorageDirectory();

        File saveRoot = fileUtilsWrapper.getFile(root.getAbsolutePath() + "/CineLog/saves");

        File[] saveFiles = saveRoot.listFiles();
        if(saveFiles.length > 10){
            File oldestFile = null;
            for (File saveFile : saveFiles) {
                if(oldestFile == null || oldestFile.lastModified() > saveFile.lastModified()){
                    oldestFile = saveFile;
                }
            }

            if(oldestFile != null){
                //noinspection ResultOfMethodCallIgnored
                oldestFile.delete();
            }
        }
    }

    @NonNull
    private String buildExportFilePath() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat todayDate = new SimpleDateFormat("yyyyMMdd");
        return fileUtilsWrapper.getExternalStorageDirectory().getAbsolutePath() + "/CineLog/saves/export" + todayDate.format(new Date()) + ".csv";
    }
}