package com.rbm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateReport extends DefaultTask {

    private File outputDirJava;
    private File outputDirKotlin;

    public File getOutputDirJava() {
        return outputDirJava;
    }

    public void setOutputDirJava(File outputDirJava) {
        this.outputDirJava = outputDirJava;
    }

    public File getOutputDirKotlin() {
        return outputDirKotlin;
    }

    public void setOutputDirKotlin(File outputDirKotlin) {
        this.outputDirKotlin = outputDirKotlin;
    }

    @TaskAction
    public void doAction() throws IOException {
        String reportName = getReportName();
        System.out.println("Report name: " + reportName);
        if (reportName == null) {
            System.out.println("Specify new report name with -Dreport.name=MyNewReportName parameter");
            System.out.println("Example: gradlew -Dreport.name=OISReport createReport");
            return;
        }
        String reportType = getReportType();
        System.out.println("Report type: " + reportType);
        if (reportType == null) {
            System.out.println("Specify new report type with -Dreport.type=kotlin parameter");
            System.out.println("Example: gradlew -Dreport.type=kotlin createReport");
            return;
        }

        Path p = null;
        switch (reportType.toLowerCase()) {
            case "java":
                p = Paths.get(outputDirJava.getAbsolutePath(), getReportName() + getTemplateExt());
                break;
            case "kotlin":
                p = Paths.get(outputDirKotlin.getAbsolutePath(), getReportName() + getTemplateExt());
                break;
        }

        Files.createDirectories(p.getParent());
        File file = Files.createFile(p).toFile();

        String classBody = buildTemplate();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(classBody);
        writer.flush();
        writer.close();
        System.out.println("Report template created: " + file.getAbsolutePath());
    }

    private String getReportName() {
        return System.getProperty("report.name");
    }

    private String getReportType() {
        return System.getProperty("report.type");
    }

    private String buildTemplate() {
        StringBuilder result = new StringBuilder();
        try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(getTemplateSrcName()))) {
            int c = 0;
            char[] charArray = new char[256];
            while ((c = reader.read(charArray)) >= 0) {
                result.append(charArray, 0, c);
            }
        } catch (Throwable e) {
            throw new IllegalStateException("buildTemplate:: caught: " + e.getMessage(), e);
        }
        String resultStr = String.format(result.toString(), getReportName(), "// ### begin of the report source code ### \n\n//todo: replace with report source code\n\n// ### end of the report source code ###");
        return resultStr;
    }

    private String getTemplateSrcName() {
        if (getReportType().toLowerCase().equals("kotlin")) {
            return "CoreKotlinReportTemplate.txt";
        } else if (getReportType().toLowerCase().equals("java")) {
            return "CoreReportTemplate.txt";
        }
        return null;
    }

    private String getTemplateExt() {
        if (getReportType().toLowerCase().equals("kotlin")) {
            return ".kt";
        } else if (getReportType().toLowerCase().equals("java")) {
            return ".java";
        }
        return null;
    }

}
