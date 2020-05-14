package com.temporal.persistence.util;
/* 
    Apricot Management Suite
    Copyright (C) 2020 Tarang Parikh
    
    Email : tp0265@gmail.com
    Project Home : https://github.com/tarangparikh/apricot
    
    Original Author : @author Tarang Parikh <tp0265@gmail.com>
    
*/

import com.beust.jcommander.IStringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListConverter implements IStringConverter<List<File>> {
    @Override
    public List<File> convert(String files) {
        String [] paths = files.split(",");
        List<File> fileList = new ArrayList<>();
        for(String path : paths){
            fileList.add(new File(path));
        }
        return fileList;
    }
}
