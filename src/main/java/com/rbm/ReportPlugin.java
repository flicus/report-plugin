package com.rbm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ReportPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.getTasks().create("createReport", CreateReport.class);
        project.getTasks().create("processReports", ProcessReports.class);

        project.getExtensions().add("reportProcessing", ReportProcessing.class);
    }
}
