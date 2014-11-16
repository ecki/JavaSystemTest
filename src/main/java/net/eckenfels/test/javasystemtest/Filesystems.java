/*
 * Filesystems.java
 *
 * created at 16.11.2014 by Eckenfel <b.eckenfels@seeburger.de>
 *
 * Copyright (c) SEEBURGER AG, Germany. All Rights Reserved.
 */
package net.eckenfels.test.javasystemtest;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;


public class Filesystems
{
    static final double MB = 1024*1024;
    public static void main(String[] args) throws IOException
    {
        FileSystem fs = FileSystems.getDefault();
        Iterable<FileStore> stores = fs.getFileStores();
        for(FileStore f : stores)
        {
            System.out.printf("%-40s %-10s %8.2fMB %8.2fMB%n", f.name() + " " + f.toString(), "("+f.type()+")", f.getUsableSpace()/MB, f.getTotalSpace()/MB);
        }
    }

}

