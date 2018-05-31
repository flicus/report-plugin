package com.rbm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ProcessReports extends DefaultTask {

    private static final String record = "INSERT INTO REPORT_ENTITY (REPORTID, TYPEID, CODE, NAME, ISACTIVE, CLASSNAME, COMPILEDCLASS, NOTE, SRC, LANGUAGEID) VALUES (%d, 1, '%s', '%s', true, '%s', '', '', '%s', %d);";

    @InputDirectory
    private File input;

    @OutputFile
    private File output;

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    @TaskAction
    public void doAction() throws IOException {
        if (output == null) {
            output = new File("reports.sql");
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        AtomicInteger counter = new AtomicInteger(0);

        getFiles(".java").forEach(file -> process(writer, counter, file, 0));
        getFiles(".kt").forEach(file -> process(writer, counter, file, 1));

        writer.flush();
        writer.close();
    }

    private void process(BufferedWriter writer, AtomicInteger id, File file, int type) {
        try {
            System.out.println("### Processing: " + file.getName());
            String report = getReportName(file);
            String reportSrc = getReportSrc(file);
            // reportId, code, name, classname, src, languageid
            writer.write(String.format(record, id.getAndIncrement(), report, report, report, reportSrc, type));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Stream<File> getFiles(String ext) {
        return Arrays.asList(getProject().getBuildDir().listFiles((dir, name) -> name.endsWith(ext))).stream();
    }

    private String getReportName(File file) {
        return file.getName().replace(".java", "");
    }

    private String getReportSrc(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        boolean startFound = false;
        boolean endFound = false;
        while (reader.ready()) {
            String line = reader.readLine();
            if (line.contains("### begin")) {
                startFound = true;
            } else if (line.contains("### end")) {
                endFound = true;
            } else {
                if (startFound && !endFound) {
                    sb.append(line);
                }
            }
        }
        return sb.toString();
    }
}
