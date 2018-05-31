package com.rbm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ProcessReports extends DefaultTask {

    private static final String record = "INSERT INTO REPORT_ENTITY (REPORTID, TYPEID, CODE, NAME, ISACTIVE, CLASSNAME, COMPILEDCLASS, NOTE, SRC, LANGUAGEID) VALUES (%d, 1, '%s', '%s', true, '%s', '', '', '%s', %d);";

    @TaskAction
    public void doAction() throws IOException {
        File output = new File("reports.sql");
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));


        int i = 0;
        Arrays
                .asList(getProject()
                        .getBuildDir()
                        .listFiles((dir, name) -> name.endsWith(".java")))
                .stream()
                .forEach(file -> {
                    System.out.println("### Processing: " + file.getName());
                    String report = getReportName(file);
                    // reportId, code, name, classname, src, languageid
                    writer.write(String.format(record, i, report, report, report, "", 0));
                    writer.newLine();

                });
        writer.flush();
        writer.close();
    }

    private String getReportName(File file) {
        return file.getName().replace(".java", "");
    }

    private String getReportSrc(File file) {
        StringBuilder sb = new StringBuilder();
//        file.li`
    }
}
