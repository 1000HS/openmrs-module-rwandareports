package org.openmrs.module.rwandareports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.rwandareports.renderer.MCHCustomWebRenderer;
import org.openmrs.module.rwandareports.util.Cohorts;
import org.openmrs.module.rwandareports.util.Indicators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class SetupMCHIndicatorReport {

    protected final Log log = LogFactory.getLog(getClass());

    // properties
    private Program anc;

    private Concept bpCategory = Context.getConceptService().getConceptByUuid("e7fc9067-ccf2-4fc4-b482-51c8c36c8553");
    private Concept preEclampsia = Context.getConceptService().getConceptByUuid("61320ab5-3aef-4e43-98a1-9a925fd8dc14");
    private Concept severePreEclampsia = Context.getConceptService().getConceptByUuid("316ef3e4-c2a6-4bdc-8438-9b4a6a2a7898");
    private Concept eclampsia = Context.getConceptService().getConceptByUuid("c312855e-542a-4817-8cd3-78adbe0f52e6");

    private Form ancEnrolmentForm = Context.getFormService().getForm(217);
    private Form ancFollowupForm = Context.getFormService().getForm(209);
    private Concept durationOfPregnancy = Context.getConceptService().getConcept(105483);

    //IMCI propertiesChildren consulted for fever / Enfants consultés  pour la fièvre
    private Form imciEnrolmentForm6d = Context.getFormService().getForm(Integer.parseInt(Context.getAdministrationService().getGlobalProperty("mch.IMNCI_0-7_Day")));
    private Form imciEnrolmentForm1wTo2M = Context.getFormService().getForm(Integer.parseInt(Context.getAdministrationService().getGlobalProperty("mch.IMNCI_1W-2M")));
    private Form imciEnrolmentForm2MTo5Y = Context.getFormService().getForm(Integer.parseInt(Context.getAdministrationService().getGlobalProperty("mch.IMNCI_2M-5Y")));
    private Form imciFollowUpForm = Context.getFormService().getForm(Integer.parseInt(Context.getAdministrationService().getGlobalProperty("mch.IMNCI_FollowUP")));


    private Concept dangerSigns = Context.getConceptService().getConceptByUuid("d5efc056-1c14-44bd-8b28-09ad4c742ec0");

    private Concept fever = Context.getConceptService().getConceptByUuid("3cf1898e-26fe-102b-80cb-0017a47871b2");
    private Concept difficultBreathing = Context.getConceptService().getConceptByUuid("abdce81b-bc26-488e-bb77-e026cb59f0dd");
    private Concept diarrhea = Context.getConceptService().getConceptByUuid("3ccc6a00-26fe-102b-80cb-0017a47871b2");
    private Concept eardiseases = Context.getConceptService().getConceptByUuid("d434e553-ef90-4c96-bf38-92af79ecb617");
    private Concept NoEarInfection = Context.getConceptService().getConceptByUuid("d8904a4f-3795-411f-bf71-148376f39dbb");
    private Concept yes = Context.getConceptService().getConceptByUuid("3cd6f600-26fe-102b-80cb-0017a47871b2");
    private Concept referPatientTo = Context.getConceptService().getConceptByUuid("6cf3d521-9035-4301-bbf9-baa4e87251cb");
    private Concept tt1 = Context.getConceptService().getConceptByUuid("3ccca1c8-26fe-102b-80cb-0017a47871b2");

    private Concept tt2 = Context.getConceptService().getConceptByUuid("06ae27b7-79c0-4d14-b658-c28b3c6653b8");
    private Concept tt3 = Context.getConceptService().getConceptByUuid("f0612154-baa6-4d8f-9ae2-e1dba972153e");
    private Concept tt4 = Context.getConceptService().getConceptByUuid("f8ee0ff6-cee8-49da-b032-a191d32536d0");
    private Concept tt5 = Context.getConceptService().getConceptByUuid("1a7aefe6-f5b3-41fe-a3f0-d7a236f290f7");


    private Concept ironAndFolicAcid = Context.getConceptService().getConceptByUuid("0bd8cc98-0aec-4fa4-89b2-68a0748a1c0e");
    private Concept insecticideTreatedBedNet = Context.getConceptService().getConceptByUuid("1594849e-f8db-43ec-9294-645ac0ec6e7d");
    private Concept muac = Context.getConceptService().getConceptByUuid("4326b04b-3158-417a-bb8d-ad022295b0f4");
    private Concept muaclt21 = Context.getConceptService().getConceptByUuid("4326b04b-3158-417a-bb8d-ad022295b0f4");
    private Concept clinicalSignForAnaemia = Context.getConceptService().getConceptByUuid("84ba9371-cd3b-4792-96ce-3720b9e5625c");

    private Concept anaemiaComment = Context.getConceptService().getConceptByUuid("605315b6-926f-422a-a04d-b630a8a59c9f");
    private Concept moderateAnemia = Context.getConceptService().getConceptByUuid("50252fc3-3baa-4238-9785-e2d5a07bb442");
    private Concept severeAnemia = Context.getConceptService().getConceptByUuid("249b4b7f-525b-4404-ae15-eca5ef1dff1b");

    private Concept syphyilis = Context.getConceptService().getConceptByUuid("3cceae50-26fe-102b-80cb-0017a47871b2");


    private Concept lookForSignsOfSymptomaticHIV = Context.getConceptService().getConceptByUuid("5c0bcd22-378f-442e-b0f5-cc6dcd207ba3");
    private Concept currentPneumonia = Context.getConceptService().getConceptByUuid("a303e6b2-f541-44e4-a0a2-2821310a95e4");


    private List<Concept> eclapsiaList = new ArrayList<Concept>();


    public void setup() throws Exception {

        setUpProperties();

        createReportDefinition();

    }

    public void delete() {

        ReportService rs = Context.getService(ReportService.class);
        for (ReportDesign rd : rs.getAllReportDesigns(false)) {
            if ("MCHWebRenderer".equals(rd.getName()) || "MCH_Excel".equals(rd.getName())) {
                rs.purgeReportDesign(rd);
            }
        }

        Helper.purgeReportDefinition("MCH Report");
    }

    // DQ Report by Site
    public ReportDefinition createReportDefinition() throws IOException {

        PeriodIndicatorReportDefinition rd = new PeriodIndicatorReportDefinition();
        //rd.removeParameter(ReportingConstants.START_DATE_PARAMETER);
        //rd.removeParameter(ReportingConstants.END_DATE_PARAMETER);
        //rd.removeParameter(ReportingConstants.LOCATION_PARAMETER);
        rd.addParameter(new Parameter("location", "Location", Location.class));
        rd.addParameter(new Parameter("startDate", "StartDate", Date.class));
        rd.addParameter(new Parameter("endDate", "EndDate", Date.class));

        rd.setName("MCH Report");


        rd.setupDataSetDefinition();

        rd.setBaseCohortDefinition(Cohorts
                        .createParameterizedLocationAndProgramCohort("At Location", anc),
                ParameterizableUtil
                        .createParameterMappings("location=${location}"));

        rd.addDataSetDefinition(createIndicatorsForReports(), ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},location=${location}"));

        Helper.saveReportDefinition(rd);

        ReportDesign monthlyDesign = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "MCH_Excel.xls", "MCH_Excel", null);
        Properties monthlyProps = new Properties();
        monthlyProps.put("repeatingSections", "sheet:1,dataset:mchDataSet");
        monthlyProps.put("sortWeight", "5000");
        monthlyDesign.setProperties(monthlyProps);
        Helper.saveReportDesign(monthlyDesign);

        createCustomWebRenderer(rd, "MCHWebRenderer");


        return rd;
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public CohortIndicatorDataSetDefinition createIndicatorsForReports() {
        CohortIndicatorDataSetDefinition mchDataSetDefinition = new CohortIndicatorDataSetDefinition();
        mchDataSetDefinition.setName("mchDataSet");
        mchDataSetDefinition.addParameter(new Parameter("startDate", "StartDate", Date.class));
        mchDataSetDefinition.addParameter(new Parameter("endDate", "EndDate", Date.class));
        mchDataSetDefinition.addParameter(new Parameter("location", "Location", Location.class));

        buildANCIndicators(mchDataSetDefinition);
        buildIntraIndicatorA(mchDataSetDefinition);
        buildIntraIndicatorB(mchDataSetDefinition);
        buildIntraIndicatorC(mchDataSetDefinition);
        buildPNCIndicator(mchDataSetDefinition);
        buildEPIIndicator(mchDataSetDefinition);
        buildIMCIIndicators(mchDataSetDefinition);
        //buildIMCIIndicator(mchDataSetDefinition);
        buildGMPIIndicator(mchDataSetDefinition);


        return mchDataSetDefinition;


    }


    private void setUpProperties() {

        //anc = gp.getProgram(GlobalPropertiesManagement.ANC_PROGRAM);
        anc = Context.getProgramWorkflowService().getProgram(25);

        eclapsiaList.add(severePreEclampsia);
        eclapsiaList.add(eclampsia);

    }

    private ReportDesign createCustomWebRenderer(ReportDefinition rd,
                                                 String name) throws IOException {
        final ReportDesign design = new ReportDesign();
        design.setName(name);
        design.setReportDefinition(rd);
        design.setRendererType(MCHCustomWebRenderer.class);
        ReportService rs = Context.getService(ReportService.class);
        return rs.saveReportDesign(design);
    }

    private CohortIndicatorDataSetDefinition buildIMCIIndicators(CohortIndicatorDataSetDefinition mchDataSetDefinition) {
//=========================================================
//              IMCI
//==========================================================

        //A. Children received in IMCI services/ Enfants reçus dans le service PCIME

        // 0- 6d
        SqlCohortDefinition patientsImciEnrolmentAt0To6d = new SqlCohortDefinition(
                "select patient_id from encounter where form_id=" + imciEnrolmentForm6d.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        patientsImciEnrolmentAt0To6d.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt0To6d.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt0To6dIndicator = Indicators
                .newCountIndicator(
                        "IMCIA1: Children received in IMCI services/ Enfants reçus dans le service PCIME 0-6d",
                        patientsImciEnrolmentAt0To6d, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIA1", "Children received in IMCI services/ Enfants reçus dans le service PCIME 0-6d",
                new Mapped(patientsImciEnrolmentAt0To6dIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 1W- 2M
        SqlCohortDefinition patientsImciEnrolmentAt1WTo2M = new SqlCohortDefinition(
                "select patient_id from encounter where form_id=" + imciEnrolmentForm1wTo2M.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        patientsImciEnrolmentAt1WTo2M.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt1WTo2M.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt1WTo2MIndicator = Indicators
                .newCountIndicator(
                        "IMCIA2: Children received in IMCI services/ Enfants reçus dans le service PCIME 1w-2m",
                        patientsImciEnrolmentAt1WTo2M, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIA2", "Children received in IMCI services/ Enfants reçus dans le service PCIME 1w-2m",
                new Mapped(patientsImciEnrolmentAt1WTo2MIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // 2M - 5Y
        SqlCohortDefinition patientsImciEnrolmentAt2MTo5Y = new SqlCohortDefinition(
                "select patient_id from encounter where form_id=" + imciEnrolmentForm2MTo5Y.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        patientsImciEnrolmentAt2MTo5Y.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt2MTo5Y.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt2MTo5YIndicator = Indicators
                .newCountIndicator(
                        "IMCIA3: Children received in IMCI services/ Enfants reçus dans le service PCIME 2m-5Y",
                        patientsImciEnrolmentAt2MTo5Y, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIA3", "Children received in IMCI services/ Enfants reçus dans le service PCIME 2m-5Y",
                new Mapped(patientsImciEnrolmentAt2MTo5YIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


// B. Children who had general danger signs / Enfants consultés ayant des signes généraux de danger

        // 0- 6d

        SqlCohortDefinition patientsImciEnrolmentAt0To6dWithDangerSigns = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and  e.form_id=" + imciEnrolmentForm6d.getFormId() + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + dangerSigns.getConceptId() + "");
        patientsImciEnrolmentAt0To6dWithDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt0To6dWithDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt0To6dWithDangerSignsIndicator = Indicators
                .newCountIndicator(
                        "IMCIB1: Children who had general danger signs / Enfants consultés ayant des signes généraux de danger 0-6d",
                        patientsImciEnrolmentAt0To6dWithDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIB1", "Children who had general danger signs / Enfants consultés ayant des signes généraux de danger 0-6d",
                new Mapped(patientsImciEnrolmentAt0To6dWithDangerSignsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 1W- 2M

        SqlCohortDefinition patientsImciEnrolmentAt1WTo2MWithDangerSigns = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and  e.form_id=" + imciEnrolmentForm1wTo2M.getFormId() + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + dangerSigns.getConceptId() + "");
        patientsImciEnrolmentAt1WTo2MWithDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt1WTo2MWithDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt1WTo2MWithDangerSignsIndicator = Indicators
                .newCountIndicator(
                        "IMCIB2: Children who had general danger signs / Enfants consultés ayant des signes généraux de danger 1W- 2M",
                        patientsImciEnrolmentAt1WTo2MWithDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIB2", "Children who had general danger signs / Enfants consultés ayant des signes généraux de danger 1W- 2M",
                new Mapped(patientsImciEnrolmentAt1WTo2MWithDangerSignsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 2M - 5Y

        SqlCohortDefinition patientsImciEnrolmentAt2MTo5YDangerSigns = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and  e.form_id=" + imciEnrolmentForm2MTo5Y.getFormId() + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + dangerSigns.getConceptId() + "");
        patientsImciEnrolmentAt2MTo5YDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt2MTo5YDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt2MTo5YDangerSignsIndicator = Indicators
                .newCountIndicator(
                        "IMCIB3: Children who had general danger signs / Enfants consultés ayant des signes généraux de danger 2M - 5YM",
                        patientsImciEnrolmentAt2MTo5YDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIB3", "Children who had general danger signs / Enfants consultés ayant des signes généraux de danger 2M - 5Y",
                new Mapped(patientsImciEnrolmentAt2MTo5YDangerSignsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


// C. Children consulted for fever / Enfants consultés  pour la fièvre

        // 0- 6d

        SqlCohortDefinition patientsImciEnrolmentAt0To6dWithFeverDangerSigns = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded=" + yes.getConceptId() + "  and  (e.form_id=" + imciEnrolmentForm6d.getFormId() + " or e.form_id=" + imciFollowUpForm.getFormId() + ") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + fever.getConceptId() + "");
        patientsImciEnrolmentAt0To6dWithFeverDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt0To6dWithFeverDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt0To6dWithFeverDangerSignsIndicator = Indicators
                .newCountIndicator(
                        "IMCIC1: Children consulted for fever / Enfants consultés  pour la fièvre 0-6d",
                        patientsImciEnrolmentAt0To6dWithFeverDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIC1", "Children consulted for fever / Enfants consultés  pour la fièvre 0-6d",
                new Mapped(patientsImciEnrolmentAt0To6dWithFeverDangerSignsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 1W- 2M

        SqlCohortDefinition patientsImciEnrolmentAt1WTo2MWithFeverDangerSigns = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded=" + yes.getConceptId() + "  and (e.form_id=" + imciEnrolmentForm1wTo2M.getFormId() + " or e.form_id=" + imciFollowUpForm.getFormId() + ") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + fever.getConceptId() + "");
        patientsImciEnrolmentAt1WTo2MWithFeverDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt1WTo2MWithFeverDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt1WTo2MWithFeverDangerSignsIndicator = Indicators
                .newCountIndicator(
                        "IMCIC2: Children consulted for fever / Enfants consultés  pour la fièvre 1W- 2M",
                        patientsImciEnrolmentAt1WTo2MWithFeverDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIC2", "Children consulted for fever / Enfants consultés  pour la fièvre 1W- 2M",
                new Mapped(patientsImciEnrolmentAt1WTo2MWithFeverDangerSignsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 2M - 5Y

        SqlCohortDefinition patientsImciEnrolmentAt2MTo5YFeverDangerSigns = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded=" + yes.getConceptId() + "  and  (e.form_id=" + imciEnrolmentForm2MTo5Y.getFormId() + " or e.form_id=" + imciFollowUpForm.getFormId() + ") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + fever.getConceptId() + "");
        patientsImciEnrolmentAt2MTo5YFeverDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt2MTo5YFeverDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt2MTo5YFeverDangerSignsIndicator = Indicators
                .newCountIndicator(
                        "IMCIC3: Children consulted for fever / Enfants consultés  pour la fièvre 2M - 5YM",
                        patientsImciEnrolmentAt2MTo5YFeverDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIC3", "Children consulted for fever / Enfants consultés  pour la fièvre2M - 5Y",
                new Mapped(patientsImciEnrolmentAt2MTo5YFeverDangerSignsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


// D. Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire
/*

		// 0- 6d

		SqlCohortDefinition patientsImciEnrolmentAt0To6dWithDifficultBreathingifficultBreathingDangerSigns = new SqlCohortDefinition(
				"select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded="+difficultBreathing.getConceptId()+"  and (e.form_id="+imciEnrolmentForm6d.getFormId()+" or e.form_id="+imciFollowUpForm.getFormId()+") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id="+dangerSigns.getConceptId()+"");
		patientsImciEnrolmentAt0To6dWithDifficultBreathingifficultBreathingDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
		patientsImciEnrolmentAt0To6dWithDifficultBreathingifficultBreathingDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

		CohortIndicator patientsImciEnrolmentAt0To6dWithDifficultBreathingifficultBreathingDangerSignsIndicator = Indicators
				.newCountIndicator(
						"IMCID1: Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire 0-6d",
						patientsImciEnrolmentAt0To6dWithDifficultBreathingifficultBreathingDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		ancDataSetDefinition.addColumn("IMCID1","Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire 0-6d",
				new Mapped(patientsImciEnrolmentAt0To6dWithDifficultBreathingifficultBreathingDangerSignsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");

		// 1W- 2M

		SqlCohortDefinition patientsImciEnrolmentAt1WTo2MWithDifficultBreathingDangerSigns = new SqlCohortDefinition(
				"select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded="+difficultBreathing.getConceptId()+"  and (e.form_id="+imciEnrolmentForm1wTo2M.getFormId()+" or e.form_id="+imciFollowUpForm.getFormId()+") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id="+dangerSigns.getConceptId()+"");
		patientsImciEnrolmentAt1WTo2MWithDifficultBreathingDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
		patientsImciEnrolmentAt1WTo2MWithDifficultBreathingDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

		CohortIndicator patientsImciEnrolmentAt1WTo2MWithDifficultBreathingDangerSignsIndicator = Indicators
				.newCountIndicator(
						"IMCD2: Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire 1W- 2M",
						patientsImciEnrolmentAt1WTo2MWithDifficultBreathingDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		ancDataSetDefinition.addColumn("IMCID2","Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire 1W- 2M",
				new Mapped(patientsImciEnrolmentAt1WTo2MWithDifficultBreathingDangerSignsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");

		// 2M - 5Y

		SqlCohortDefinition patientsImciEnrolmentAt2MTo5YDifficultBreathingDangerSigns = new SqlCohortDefinition(
				"select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded="+difficultBreathing.getConceptId()+"  and  (e.form_id="+imciEnrolmentForm2MTo5Y.getFormId()+" or e.form_id="+imciFollowUpForm.getFormId()+") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id="+dangerSigns.getConceptId()+"");
		patientsImciEnrolmentAt2MTo5YDifficultBreathingDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
		patientsImciEnrolmentAt2MTo5YDifficultBreathingDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

		CohortIndicator patientsImciEnrolmentAt2MTo5YDifficultBreathingDangerSignsIndicator = Indicators
				.newCountIndicator(
						"IMCID3: Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire 2M - 5YM",
						patientsImciEnrolmentAt2MTo5YDifficultBreathingDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		ancDataSetDefinition.addColumn("IMCD3","Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire 2M - 5Y",
				new Mapped(patientsImciEnrolmentAt2MTo5YDifficultBreathingDangerSignsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");


// E. Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée

		// 0- 6d

		SqlCohortDefinition patientsImciEnrolmentAt0To6dWithDiarrheaDangerSigns = new SqlCohortDefinition(
				"select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded="+diarrhea.getConceptId()+"  and  (e.form_id="+imciEnrolmentForm6d.getFormId()+" or e.form_id="+imciFollowUpForm.getFormId()+") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id="+dangerSigns.getConceptId()+"");
		patientsImciEnrolmentAt0To6dWithDiarrheaDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
		patientsImciEnrolmentAt0To6dWithDiarrheaDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

		CohortIndicator patientsImciEnrolmentAt0To6dWithDiarrheaDangerSignsIndicator = Indicators
				.newCountIndicator(
						"Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée 0-6d",
						patientsImciEnrolmentAt0To6dWithDiarrheaDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		ancDataSetDefinition.addColumn("IMICE1","Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée 0-6d",
				new Mapped(patientsImciEnrolmentAt0To6dWithDiarrheaDangerSignsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");

		// 1W- 2M

		SqlCohortDefinition patientsImciEnrolmentAt1WTo2MWithDiarrheaDangerSigns = new SqlCohortDefinition(
				"select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded="+diarrhea.getConceptId()+"  and (e.form_id="+imciEnrolmentForm1wTo2M.getFormId()+" or e.form_id="+imciFollowUpForm.getFormId()+") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id="+dangerSigns.getConceptId()+"");
		patientsImciEnrolmentAt1WTo2MWithDiarrheaDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
		patientsImciEnrolmentAt1WTo2MWithDiarrheaDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

		CohortIndicator patientsImciEnrolmentAt1WTo2MWithDiarrheaDangerSignsIndicator = Indicators
				.newCountIndicator(
						"Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée 1W- 2M",
						patientsImciEnrolmentAt1WTo2MWithDiarrheaDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		ancDataSetDefinition.addColumn("IMCIE2","Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée 1W- 2M",
				new Mapped(patientsImciEnrolmentAt1WTo2MWithDiarrheaDangerSignsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");

		// 2M - 5Y

		SqlCohortDefinition patientsImciEnrolmentAt2MTo5YDiarrheaDangerSigns = new SqlCohortDefinition(
				"select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded="+diarrhea.getConceptId()+"  and  (e.form_id="+imciEnrolmentForm2MTo5Y.getFormId()+" or e.form_id="+imciFollowUpForm.getFormId()+") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id="+dangerSigns.getConceptId()+"");
		patientsImciEnrolmentAt2MTo5YDiarrheaDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
		patientsImciEnrolmentAt2MTo5YDiarrheaDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

		CohortIndicator patientsImciEnrolmentAt2MTo5YDiarrheaDangerSignsIndicator = Indicators
				.newCountIndicator(
						"Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée 2M - 5YM",
						patientsImciEnrolmentAt2MTo5YDiarrheaDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

		ancDataSetDefinition.addColumn("IMCIE3","Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée 2M - 5Y",
				new Mapped(patientsImciEnrolmentAt2MTo5YDiarrheaDangerSignsIndicator,ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")),"");
*/

// F. Children consulted for  Ear diseases/ Maladies de l’Oreille

        // 0- 6d

        SqlCohortDefinition patientsImciEnrolmentAt0To6dWithEardiseasesDangerSigns = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded!=" + NoEarInfection.getConceptId() + "  and  (e.form_id=" + imciEnrolmentForm6d.getFormId() + " or e.form_id=" + imciFollowUpForm.getFormId() + ") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + eardiseases.getConceptId() + "");
        patientsImciEnrolmentAt0To6dWithEardiseasesDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt0To6dWithEardiseasesDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt0To6dWithEardiseasesDangerSignsIndicator = Indicators
                .newCountIndicator(
                        "Children consulted for  Ear diseases/ Maladies de l’Oreille 0-6d",
                        patientsImciEnrolmentAt0To6dWithEardiseasesDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIF1", "Children consulted for  Ear diseases/ Maladies de l’Oreille 0-6d",
                new Mapped(patientsImciEnrolmentAt0To6dWithEardiseasesDangerSignsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 1W- 2M

        SqlCohortDefinition patientsImciEnrolmentAt1WTo2MWithEardiseasesDangerSigns = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded!=" + NoEarInfection.getConceptId() + "  and (e.form_id=" + imciEnrolmentForm1wTo2M.getFormId() + " or e.form_id=" + imciFollowUpForm.getFormId() + ") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + eardiseases.getConceptId() + "");
        patientsImciEnrolmentAt1WTo2MWithEardiseasesDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt1WTo2MWithEardiseasesDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt1WTo2MWithEardiseasesDangerSignsIndicator = Indicators
                .newCountIndicator(
                        "Children consulted for  Ear diseases/ Maladies de l’Oreille 1W- 2M",
                        patientsImciEnrolmentAt1WTo2MWithEardiseasesDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIF2", "Children consulted for  Ear diseases/ Maladies de l’Oreille 1W- 2M",
                new Mapped(patientsImciEnrolmentAt1WTo2MWithEardiseasesDangerSignsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 2M - 5Y

        SqlCohortDefinition patientsImciEnrolmentAt2MTo5YEardiseasesDangerSigns = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded=" + NoEarInfection.getConceptId() + "  and  (e.form_id=" + imciEnrolmentForm2MTo5Y.getFormId() + " or e.form_id=" + imciFollowUpForm.getFormId() + ") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + eardiseases.getConceptId() + "");
        patientsImciEnrolmentAt2MTo5YEardiseasesDangerSigns.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt2MTo5YEardiseasesDangerSigns.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt2MTo5YEardiseasesDangerSignsIndicator = Indicators
                .newCountIndicator(
                        "Children consulted for  Ear diseases/ Maladies de l’Oreille 2M - 5YM",
                        patientsImciEnrolmentAt2MTo5YEardiseasesDangerSigns, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIF3", "Children consulted for  Ear diseases/ Maladies de l’Oreille 2M - 5Y",
                new Mapped(patientsImciEnrolmentAt2MTo5YEardiseasesDangerSignsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


// G. Children referred to Hospital / Enfants référés a l’ Hopital

        // 0- 6d

        SqlCohortDefinition patientsImciEnrolmentAt0To6dReferredToHospital = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id   and  e.form_id=" + imciEnrolmentForm6d.getFormId() + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + referPatientTo.getConceptId() + "");
        patientsImciEnrolmentAt0To6dReferredToHospital.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt0To6dReferredToHospital.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt0To6dReferredToHospitalIndicator = Indicators
                .newCountIndicator(
                        "Children referred to Hospital / Enfants référés a l’ Hopital 0-6d",
                        patientsImciEnrolmentAt0To6dReferredToHospital, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIG1", "Children referred to Hospital / Enfants référés a l’ Hopital 0-6d",
                new Mapped(patientsImciEnrolmentAt0To6dReferredToHospitalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 1W- 2M

        SqlCohortDefinition patientsImciEnrolmentAt1WTo2MReferredToHospital = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id  and e.form_id=" + imciEnrolmentForm1wTo2M.getFormId() + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + referPatientTo.getConceptId() + "");
        patientsImciEnrolmentAt1WTo2MReferredToHospital.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt1WTo2MReferredToHospital.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt1WTo2MReferredToHospitalIndicator = Indicators
                .newCountIndicator(
                        "Children consulted for  Ear diseases/ Maladies de l’Oreille 1W- 2M",
                        patientsImciEnrolmentAt1WTo2MReferredToHospital, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIG2", "Children consulted for  Ear diseases/ Maladies de l’Oreille 1W- 2M",
                new Mapped(patientsImciEnrolmentAt1WTo2MReferredToHospitalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 2M - 5Y

        SqlCohortDefinition patientsImciEnrolmentAt2MTo5YReferredToHospital = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id  and  e.form_id=" + imciEnrolmentForm2MTo5Y.getFormId() + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + referPatientTo.getConceptId() + "");
        patientsImciEnrolmentAt2MTo5YReferredToHospital.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt2MTo5YReferredToHospital.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt2MTo5YReferredToHospitalIndicator = Indicators
                .newCountIndicator(
                        "Children consulted for  Ear diseases/ Maladies de l’Oreille 2M - 5YM",
                        patientsImciEnrolmentAt2MTo5YReferredToHospital, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIG3", "Children consulted for  Ear diseases/ Maladies de l’Oreille 2M - 5Y",
                new Mapped(patientsImciEnrolmentAt2MTo5YReferredToHospitalIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//  H. Diagnoses / diagnostique:

        //H.2 Pneumonia / Pneumonie

        // 0- 6d

        SqlCohortDefinition patientsImciEnrolmentAt0To6dWithPneumoniaDiagnoses = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded!=" + currentPneumonia.getConceptId() + "  and  (e.form_id=" + imciEnrolmentForm6d.getFormId() + " or e.form_id=" + imciFollowUpForm.getFormId() + ") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + lookForSignsOfSymptomaticHIV.getConceptId() + "");
        patientsImciEnrolmentAt0To6dWithPneumoniaDiagnoses.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt0To6dWithPneumoniaDiagnoses.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt0To6dWithPneumoniaDiagnosesIndicator = Indicators
                .newCountIndicator(
                        "Diagnoses / diagnostique: Pneumonia / Pneumonie 0-6d",
                        patientsImciEnrolmentAt0To6dWithPneumoniaDiagnoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIH21", "Diagnoses / diagnostique: Pneumonia / Pneumonie 0-6d 0-6d",
                new Mapped(patientsImciEnrolmentAt0To6dWithPneumoniaDiagnosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 1W- 2M

        SqlCohortDefinition patientsImciEnrolmentAt1WTo2MWithPneumoniaDiagnoses = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded!=" + currentPneumonia.getConceptId() + "  and (e.form_id=" + imciEnrolmentForm1wTo2M.getFormId() + " or e.form_id=" + imciFollowUpForm.getFormId() + ") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + lookForSignsOfSymptomaticHIV.getConceptId() + "");
        patientsImciEnrolmentAt1WTo2MWithPneumoniaDiagnoses.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt1WTo2MWithPneumoniaDiagnoses.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt1WTo2MWithPneumoniaDiagnosesIndicator = Indicators
                .newCountIndicator(
                        "Diagnoses / diagnostique: Pneumonia / Pneumonie 0-6d 1W- 2M",
                        patientsImciEnrolmentAt1WTo2MWithPneumoniaDiagnoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIH22", "Diagnoses / diagnostique: Pneumonia / Pneumonie 0-6d 1W- 2M",
                new Mapped(patientsImciEnrolmentAt1WTo2MWithPneumoniaDiagnosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // 2M - 5Y

        SqlCohortDefinition patientsImciEnrolmentAt2MTo5YEarWithPneumoniaDiagnoses = new SqlCohortDefinition(
                "select patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and o.value_coded=" + currentPneumonia.getConceptId() + "  and  (e.form_id=" + imciEnrolmentForm2MTo5Y.getFormId() + " or e.form_id=" + imciFollowUpForm.getFormId() + ") and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.concept_id=" + lookForSignsOfSymptomaticHIV.getConceptId() + "");
        patientsImciEnrolmentAt2MTo5YEarWithPneumoniaDiagnoses.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsImciEnrolmentAt2MTo5YEarWithPneumoniaDiagnoses.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsImciEnrolmentAt2MTo5YEarWithPneumoniaDiagnosesIndicator = Indicators
                .newCountIndicator(
                        "Diagnoses / diagnostique: Pneumonia / Pneumonie 0-6d 2M - 5YM",
                        patientsImciEnrolmentAt2MTo5YEarWithPneumoniaDiagnoses, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("IMCIH23", "Diagnoses / diagnostique: Pneumonia / Pneumonie 0-6d 2M - 5Y",
                new Mapped(patientsImciEnrolmentAt2MTo5YEarWithPneumoniaDiagnosesIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        return mchDataSetDefinition;
    }

    private CohortIndicatorDataSetDefinition buildANCIndicators(CohortIndicatorDataSetDefinition mchDataSetDefinition) {
        //ANC 1

        SqlCohortDefinition patientsEnrolInANC = new SqlCohortDefinition(
                "select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        patientsEnrolInANC.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANC.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsInANCIndicator = Indicators
                .newCountIndicator(
                        "ANC1: ANC New Registrations/ CPN Nouvelles inscrites",
                        patientsEnrolInANC, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC1", "ANC New Registrations/ CPN Nouvelles inscrites",
                new Mapped(patientsInANCIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        //ANC 2

        AgeCohortDefinition under20 = Cohorts.createUnderAgeCohort("", 20, DurationUnit.YEARS);

        CompositionCohortDefinition patientsInANCIndicatorUnder20 = new CompositionCohortDefinition();
        patientsInANCIndicatorUnder20.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsInANCIndicatorUnder20.addParameter(new Parameter("endDate", "EndDate", Date.class));
        patientsInANCIndicatorUnder20.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
        patientsInANCIndicatorUnder20.getSearches().put(
                "1",
                new Mapped<CohortDefinition>(patientsEnrolInANC, ParameterizableUtil
                        .createParameterMappings("startDate=${startDate},endDate=${endDate}")));
        patientsInANCIndicatorUnder20.getSearches().put(
                "2",
                new Mapped<CohortDefinition>(under20, ParameterizableUtil
                        .createParameterMappings("effectiveDate=${effectiveDate}")));
        patientsInANCIndicatorUnder20.setCompositionString("1 and 2");

        CohortIndicator patientsInANCIndicatorUnder20Indicator = Indicators
                .newCountIndicator(
                        "ANC new registrations pregnancy under 20 years/ CPN Grossesses chez les femmes de moins de 20 ans",
                        patientsInANCIndicatorUnder20, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate},effectiveDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC2", "ANC new registrations pregnancy under 20 years/ CPN Grossesses chez les femmes de moins de 20 ans",
                new Mapped(patientsInANCIndicatorUnder20Indicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


//ANC 3

        SqlCohortDefinition patientsEnrolInANCWithlessThan12Weeks = new SqlCohortDefinition(
                "select e.patient_id from encounter e,obs o where e.encounter_id=o.encounter_id and e.form_id=" + ancEnrolmentForm.getFormId() + " and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate and e.voided=0 and o.voided=0 and o.concept_id= " + durationOfPregnancy.getConceptId() + " and o.value_numeric<12");
        patientsEnrolInANCWithlessThan12Weeks.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithlessThan12Weeks.addParameter(new Parameter("endDate", "EndDate", Date.class));


        CohortIndicator patientsEnrolInANCWithlessThan12WeeksIndicator = Indicators
                .newCountIndicator(
                        "ANC First standard visit (Within 12 Weeks)/ CPN Première visite standard (endeans 12 semaine()",
                        patientsEnrolInANCWithlessThan12Weeks, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC3", "ANC First standard visit (Within 12 Weeks)/ CPN Première visite standard (endeans 12 semaine()",
                new Mapped(patientsEnrolInANCWithlessThan12WeeksIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // Start ANC 4
        AgeCohortDefinition standardVisit = Cohorts.createUnderAgeCohort("", 20, DurationUnit.YEARS);


        //ANC 7


        SqlCohortDefinition patientsEnrolInANCWithTT1 = new SqlCohortDefinition(
                "select e.patient_id from encounter e, obs o where e.encounter_id=o.encounter_id and form_id=" + ancEnrolmentForm.getFormId() + " and concept_id=" + tt1.getConceptId() + " and o.value_datetime >= :startDate and o.value_datetime <= :endDate and o.voided=0;");
        patientsEnrolInANCWithTT1.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithTT1.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsEnrolInANCWithTT1Indicator = Indicators
                .newCountIndicator(
                        "ANC TT 1 given / CPN VAT1",
                        patientsEnrolInANCWithTT1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC7", "ANC TT 1 given / CPN VAT1",
                new Mapped(patientsEnrolInANCWithTT1Indicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        //ANC 8


        SqlCohortDefinition patientsEnrolInANCWithTT2ToTT5 = new SqlCohortDefinition(
                "select e.patient_id from encounter e, obs o where e.encounter_id=o.encounter_id and form_id=" + ancEnrolmentForm.getFormId() + " and concept_id in (" + tt2.getConceptId() + "," + tt3.getConceptId() + "," + tt4.getConceptId() + "," + tt5.getConceptId() + ") and o.value_datetime >= :startDate and o.value_datetime <= :endDate and o.voided=0;");
        patientsEnrolInANCWithTT2ToTT5.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithTT2ToTT5.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsEnrolInANCWithTT2ToTT5Indicator = Indicators
                .newCountIndicator(
                        "ANC TT 2 to 5 given / CPN VAT 2 à 5",
                        patientsEnrolInANCWithTT2ToTT5, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC8", "ANC TT 2 to 5 given / CPN VAT 2 à 5",
                new Mapped(patientsEnrolInANCWithTT2ToTT5Indicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        //ANC 9


        SqlCohortDefinition patientsEnrolInANCWithAllTT = new SqlCohortDefinition(
                "select e.patient_id from encounter e, obs o where e.encounter_id=o.encounter_id and form_id=" + ancEnrolmentForm.getFormId() + " and concept_id in (" + tt1.getConceptId() + "," + tt2.getConceptId() + "," + tt3.getConceptId() + "," + tt4.getConceptId() + "," + tt5.getConceptId() + ") and o.value_datetime >= :startDate and o.value_datetime <= :endDate and o.voided=0;");
        patientsEnrolInANCWithAllTT.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithAllTT.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsEnrolInANCWithAllTTIndicator = Indicators
                .newCountIndicator(
                        "ANC TT new registrations fully vaccinated / CPN VAT Nouvelles inscrites complètement vaccinées",
                        patientsEnrolInANCWithAllTT, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC9", "ANC TT new registrations fully vaccinated / CPN VAT Nouvelles inscrites complètement vaccinées",
                new Mapped(patientsEnrolInANCWithAllTTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        //ANC 10


        SqlCohortDefinition patientsEnrolInANCWithIronAndFolicAcid = new SqlCohortDefinition(
                "select e.patient_id from encounter e, obs o where e.encounter_id=o.encounter_id and form_id=" + ancEnrolmentForm.getFormId() + " and concept_id=" + ironAndFolicAcid.getConceptId() + " and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate and o.voided=0;");
        patientsEnrolInANCWithIronAndFolicAcid.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithIronAndFolicAcid.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsEnrolInANCWithIronAndFolicAcidIndicator = Indicators
                .newCountIndicator(
                        "ANC new registrations who received full course of Iron and Folic Acid supplements (90 tablets)/ CPN nouvelles inscrites qui ont reçu  90 Comprimés   de Fer et Acide Folique",
                        patientsEnrolInANCWithIronAndFolicAcid, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC10", "ANC new registrations who received full course of Iron and Folic Acid supplements (90 tablets)/ CPN nouvelles inscrites qui ont reçu  90 Comprimés   de Fer et Acide Folique",
                new Mapped(patientsEnrolInANCWithIronAndFolicAcidIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        //ANC 11


        SqlCohortDefinition patientsEnrolInANCWithInsecticideTreatedBedNet = new SqlCohortDefinition(
                "select e.patient_id from encounter e, obs o where e.encounter_id=o.encounter_id and form_id=" + ancEnrolmentForm.getFormId() + " and concept_id=" + insecticideTreatedBedNet.getConceptId() + " and o.value_coded=" + yes.getConceptId() + " and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate and o.voided=0;");
        patientsEnrolInANCWithInsecticideTreatedBedNet.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithInsecticideTreatedBedNet.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsEnrolInANCWithInsecticideTreatedBedNetIndicator = Indicators
                .newCountIndicator(
                        "ANC Insecticide Treated Bed nets distributed / CPN Moustiquaires Imprégnées d'Insecticide distribuées",
                        patientsEnrolInANCWithInsecticideTreatedBedNet, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC11", "ANC Insecticide Treated Bed nets distributed / CPN Moustiquaires Imprégnées d'Insecticide distribuées",
                new Mapped(patientsEnrolInANCWithInsecticideTreatedBedNetIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        //ANC 12


        SqlCohortDefinition patientsEnrolInANCWithMUAC = new SqlCohortDefinition(
                "select e.patient_id from encounter e, obs o where e.encounter_id=o.encounter_id and form_id=" + ancEnrolmentForm.getFormId() + " and concept_id=" + muac.getConceptId() + " and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate and o.voided=0;");
        patientsEnrolInANCWithMUAC.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithMUAC.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsEnrolInANCWithMUACIndicator = Indicators
                .newCountIndicator(
                        "ANC new registrations screened for malnutrition (MUAC) / CPN nouvelles inscrites dépistées pour la malnutrition (MUAC)",
                        patientsEnrolInANCWithMUAC, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC12", "ANC new registrations screened for malnutrition (MUAC) / CPN nouvelles inscrites dépistées pour la malnutrition (MUAC)",
                new Mapped(patientsEnrolInANCWithMUACIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        //ANC 13


        SqlCohortDefinition patientsEnrolInANCWithMUAClt21 = new SqlCohortDefinition(
                "select e.patient_id from encounter e, obs o where e.encounter_id=o.encounter_id and form_id=" + ancEnrolmentForm.getFormId() + " and concept_id=" + muaclt21.getConceptId() + " and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate and o.voided=0;");
        patientsEnrolInANCWithMUAClt21.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithMUAClt21.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsEnrolInANCWithMUAClt21Indicator = Indicators
                .newCountIndicator(
                        "ANC new registrations screened who were malnourished (MUAC < 21 cm) / CPN nouvelles inscrites chez lesquelles la malnutrition est détectée (MUAC < 21 cm)",
                        patientsEnrolInANCWithMUAClt21, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC13", "ANC new registrations screened who were malnourished (MUAC < 21 cm) / CPN nouvelles inscrites chez lesquelles la malnutrition est détectée (MUAC < 21 cm)",
                new Mapped(patientsEnrolInANCWithMUAClt21Indicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        //ANC 14


        SqlCohortDefinition patientsEnrolInANCWithClinicalSignForAnaemia = new SqlCohortDefinition(
                "select e.patient_id from encounter e, obs o where e.encounter_id=o.encounter_id and form_id=" + ancEnrolmentForm.getFormId() + " and concept_id=" + clinicalSignForAnaemia.getConceptId() + " and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate and o.voided=0;");
        patientsEnrolInANCWithClinicalSignForAnaemia.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithClinicalSignForAnaemia.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsEnrolInANCWithClinicalSignForAnaemiaIndicator = Indicators
                .newCountIndicator(
                        "ANC new registrations tested for anemia / CPN nouvelles inscrites testées pour l’anémie",
                        patientsEnrolInANCWithClinicalSignForAnaemia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC14", "ANC new registrations tested for anemia / CPN nouvelles inscrites testées pour l’anémie",
                new Mapped(patientsEnrolInANCWithClinicalSignForAnaemiaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        //ANC 15


        SqlCohortDefinition patientsEnrolInANCWithModerateOrSevereAnemia = new SqlCohortDefinition(
                "select e.patient_id from encounter e, obs o where e.encounter_id=o.encounter_id and form_id=" + ancEnrolmentForm.getFormId() + " and concept_id=" + anaemiaComment.getConceptId() + " and (o.value_coded=" + moderateAnemia.getConceptId() + " or o.value_coded=" + severeAnemia.getConceptId() + ") and o.obs_datetime >= :startDate and o.obs_datetime <= :endDate and o.voided=0");
        patientsEnrolInANCWithModerateOrSevereAnemia.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsEnrolInANCWithModerateOrSevereAnemia.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator patientsEnrolInANCWithModerateOrSevereAnemiaIndicator = Indicators
                .newCountIndicator(
                        "ANC new registrations with anemia Moderate and Severe (Hgb 7gr- 9.9gr/dl and <7gm/dl)",
                        patientsEnrolInANCWithModerateOrSevereAnemia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC15", "ANC new registrations with anemia Moderate and Severe (Hgb 7gr- 9.9gr/dl and <7gm/dl)",
                new Mapped(patientsEnrolInANCWithModerateOrSevereAnemiaIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

		/*SqlCohortDefinition patientsFollowupInANC = new SqlCohortDefinition(
				"select patient_id from encounter where form_id="+ancFollowupForm.getFormId()+" and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0 and count(*) >= 3");*/

        SqlCohortDefinition patientsFollowupInANC = new SqlCohortDefinition(
                "select patient_id from (select patient_id,count(patient_id) as times from encounter where form_id=" + ancFollowupForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0  group by patient_id) gc where gc.times>=3");
        patientsFollowupInANC.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientsFollowupInANC.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CompositionCohortDefinition patientInANCWithStandardVisits = new CompositionCohortDefinition();
        patientInANCWithStandardVisits.addParameter(new Parameter("startDate", "StartDate", Date.class));
        patientInANCWithStandardVisits.addParameter(new Parameter("endDate", "EndDate", Date.class));
        //patientInANCWithStandardVisits.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));

        patientInANCWithStandardVisits.getSearches().put("1",
                new Mapped<CohortDefinition>(patientsEnrolInANC, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));

        patientInANCWithStandardVisits.getSearches().put("2",
                new Mapped<CohortDefinition>(patientsFollowupInANC, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")));

        patientInANCWithStandardVisits.setCompositionString("1 and 2");

        CohortIndicator patientInANCWithStandardVisitsIndicator = Indicators
                .newCountIndicator(
                        "ANC 4 standard visits/CPN femmes  ayant fait 4 visites standard",
                        patientInANCWithStandardVisits, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        mchDataSetDefinition.addColumn("ANC4", "ANC 4 standard visits/CPN femmes  ayant fait 4 visites standard",
                new Mapped(patientInANCWithStandardVisitsIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
        // End ANC 4

        return mchDataSetDefinition;

    }

    private CohortIndicatorDataSetDefinition buildIntraIndicatorA(CohortIndicatorDataSetDefinition intraDataSetDefinition) {
        //INTRA 1
        SqlCohortDefinition scdTotalAbortion = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdTotalAbortion.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdTotalAbortion.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciTotalAbortion = Indicators.newCountIndicator("INTRA1: Total abortions", scdTotalAbortion, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA1", "Total abortions", new Mapped(ciTotalAbortion, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 2
        SqlCohortDefinition scdAntepartumHaemorrhage = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdAntepartumHaemorrhage.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdAntepartumHaemorrhage.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciAntepartumHaemorrhage = Indicators.newCountIndicator("INTRA2: Antepartum Haemorrhage (APH)/ Hémorragie prénatale", scdAntepartumHaemorrhage, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA2", "Antepartum Haemorrhage (APH)/ Hémorragie prénatale", new Mapped(ciAntepartumHaemorrhage, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // INTRA 3
        SqlCohortDefinition scdPostpartumHemorrhage = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPostpartumHemorrhage.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPostpartumHemorrhage.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPostpartumHemorrhage = Indicators.newCountIndicator("INTRA3: Post-partum hemorrhage (PPH) / Hémorragie du Post-partum (PPH)", scdPostpartumHemorrhage, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA3", "Post-partum hemorrhage (PPH) / Hémorragie du Post-partum (PPH)", new Mapped(ciPostpartumHemorrhage, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 4
        SqlCohortDefinition scdOtherPostpartumInfection = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdOtherPostpartumInfection.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdOtherPostpartumInfection.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciOtherPostpartumInfection = Indicators.newCountIndicator("INTRA4: Other Postpartum infection /Infection puerpérale (après accouchement par voie basse)", scdOtherPostpartumInfection, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA4", "Other Postpartum infection /Infection puerpérale (après accouchement par voie basse)", new Mapped(ciOtherPostpartumInfection, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 5
        SqlCohortDefinition scdObstructedLabor = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdObstructedLabor.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdObstructedLabor.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciObstructedLabor = Indicators.newCountIndicator("INTRA5: Prolonged or Obstructed labor (Travail prolongé (ou dystocie mécanique)", scdObstructedLabor, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA5", "Prolonged or Obstructed labor (Travail prolongé (ou dystocie mécanique)", new Mapped(ciObstructedLabor, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 6
        SqlCohortDefinition scdMildEclampsia = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdMildEclampsia.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdMildEclampsia.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciMildEclampsia = Indicators.newCountIndicator("INTRA6: Mild Eclampsia /Eclampsie", scdMildEclampsia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA6", "Mild Eclampsia /Eclampsie", new Mapped(ciMildEclampsia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 7
        SqlCohortDefinition scdSeverePreEclampsia = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdSeverePreEclampsia.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdSeverePreEclampsia.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciSeverePreEclampsia = Indicators.newCountIndicator("INTRA7: Severe Pre Eclampsia/Pre Eclampsie Sévère", scdSeverePreEclampsia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA7", "Severe Pre Eclampsia/Pre Eclampsie Sévère", new Mapped(ciSeverePreEclampsia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 8
        SqlCohortDefinition scdOtherDirectObstetricalComplications = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdOtherDirectObstetricalComplications.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdOtherDirectObstetricalComplications.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciOtherDirectObstetricalComplications = Indicators.newCountIndicator("INTRA8: Other direct Obstetrical complications/ Autres complications obstetricales directes", scdOtherDirectObstetricalComplications, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA8", "Other direct Obstetrical complications/ Autres complications obstetricales directes", new Mapped(ciOtherDirectObstetricalComplications, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 9
        SqlCohortDefinition scdAnemiaSevere = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdAnemiaSevere.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdAnemiaSevere.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciAnemiaSevere = Indicators.newCountIndicator("INTRA9: Anemia Severe (<7gm/dl)/ Anémie Sévère (<7gr/dl)", scdAnemiaSevere, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA9", "Anemia Severe (<7gm/dl)/ Anémie Sévère (<7gr/dl)", new Mapped(ciAnemiaSevere, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 10
        SqlCohortDefinition scdHOI = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdHOI.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdHOI.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciHOI = Indicators.newCountIndicator("INTRA10: HIV/Opportunistic Infections /VIH infections opportunistes", scdHOI, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA10", "HIV/Opportunistic Infections /VIH infections opportunistes", new Mapped(ciHOI, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 11
        SqlCohortDefinition scdOtherIndirectObstetricalComplications = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdOtherIndirectObstetricalComplications.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdOtherIndirectObstetricalComplications.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciOtherIndirectObstetricalComplications = Indicators.newCountIndicator("INTRA11: Other indirect Obstetrical complications/ Autres complications obstetricales indirectes", scdOtherIndirectObstetricalComplications, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA11", "Other indirect Obstetrical complications/ Autres complications obstetricales indirectes", new Mapped(ciOtherIndirectObstetricalComplications, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        return intraDataSetDefinition;
    }

    //TODO 7 Indicators
    private CohortIndicatorDataSetDefinition buildIntraIndicatorB(CohortIndicatorDataSetDefinition intraDataSetDefinition) {
        // INTRA 12
        SqlCohortDefinition scdIntravenousAntibiotics = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdIntravenousAntibiotics.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdIntravenousAntibiotics.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciIntravenousAntibiotics = Indicators.newCountIndicator("INTRA12: Intravenous Antibiotics to manage Obstetrical Infections / Cas ayant reçu les Antibiotiques intraveineuses pour traiter les infections puerpérales obstétricales", scdIntravenousAntibiotics, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA12", "Intravenous Antibiotics to manage Obstetrical Infections / Cas ayant reçu les Antibiotiques intraveineuses pour traiter les infections puerpérales obstétricales", new Mapped(ciIntravenousAntibiotics, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 13
        SqlCohortDefinition scdReceivedOxytocin = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdReceivedOxytocin.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdReceivedOxytocin.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciReceivedOxytocin = Indicators.newCountIndicator("INTRA13: Mother received parenteral uterotonic drugs (oxytocin) to manage PPH/ Femmes ayant reçu l’oxytocine pour traiter l’hémorragie du post partum", scdReceivedOxytocin, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA13", "Mother received parenteral uterotonic drugs (oxytocin) to manage PPH/ Femmes ayant reçu l’oxytocine pour traiter l’hémorragie du post partums", new Mapped(ciReceivedOxytocin, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // INTRA 14
        SqlCohortDefinition scdPlacentaRemoval = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPlacentaRemoval.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPlacentaRemoval.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPlacentaRemoval = Indicators.newCountIndicator("INTRA14: Manual removal of placenta/ Délivrance manuelle du placenta", scdPlacentaRemoval, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA14", "Manual removal of placenta/ Délivrance manuelle du placenta", new Mapped(ciPlacentaRemoval, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // INTRA 15
        SqlCohortDefinition scdPostAbortionCare = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPostAbortionCare.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPostAbortionCare.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPostAbortionCare = Indicators.newCountIndicator("INTRA15: Post-Abortion Care (Manual vacuum aspiration or curettage to remove retained products of conception)/ Soins Post-Avortement (Aspiration Manuelle ou curettage)", scdPostAbortionCare, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA15", "Post-Abortion Care (Manual vacuum aspiration or curettage to remove retained products of conception)/ Soins Post-Avortement (Aspiration Manuelle ou curettage)", new Mapped(ciPostAbortionCare, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // INTRA 16
        SqlCohortDefinition scdVacuumExtractionDelivery = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdVacuumExtractionDelivery.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdVacuumExtractionDelivery.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciVacuumExtractionDelivery = Indicators.newCountIndicator("INTRA16: Delivery by Vacuum extraction /Accouchement par ventouse obstétricale", scdVacuumExtractionDelivery, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA16", "Delivery by Vacuum extraction /Accouchement par ventouse obstétricale", new Mapped(ciVacuumExtractionDelivery, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // INTRA 17
        SqlCohortDefinition scdPreEclampsiaReceivingMg = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPreEclampsiaReceivingMg.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPreEclampsiaReceivingMg.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPreEclampsiaReceivingMg = Indicators.newCountIndicator("INTRA17: Pre and eclampsia cases receiving magnesium sulfate / Cas de (pré) éclampsie qui ont reçus le sulfate de magnésium", scdPreEclampsiaReceivingMg, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA17", "Pre and eclampsia cases receiving magnesium sulfate / Cas de (pré) éclampsie qui ont reçus le sulfate de magnésium", new Mapped(ciPreEclampsiaReceivingMg, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");


        // INTRA 18
        SqlCohortDefinition scdObstetricalComplications = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdObstetricalComplications.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdObstetricalComplications.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciObstetricalComplications = Indicators.newCountIndicator("INTRA18: Women with obstetrical complications during labor or after delivery referred to high level for emergency care / Les femmes avec complications obstétricales pendant le travail et après accouchement transferees á l’echelon superieur pour les soins d’urgence", scdObstetricalComplications, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA18", "Women with obstetrical complications during labor or after delivery referred to high level for emergency care / Les femmes avec complications obstétricales pendant le travail et après accouchement transferees á l’echelon superieur pour les soins d’urgence", new Mapped(ciObstetricalComplications, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        return intraDataSetDefinition;
    }

    // Intrapartum
    private CohortIndicatorDataSetDefinition buildIntraIndicatorC(CohortIndicatorDataSetDefinition intraDataSetDefinition) {
        // INTRA 19
        SqlCohortDefinition scdHCDeliveries = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdHCDeliveries.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdHCDeliveries.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciHCDeliveries = Indicators.newCountIndicator("INTRA19: Deliveries at health facility /Accouchements, total", scdHCDeliveries, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA19", "Deliveries at health facility /Accouchements, total", new Mapped(ciHCDeliveries, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 20
        SqlCohortDefinition scdPerinealTearComplications = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPerinealTearComplications.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPerinealTearComplications.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPerinealTearComplications = Indicators.newCountIndicator("INTRA20: Delivery complicated by perineal tear (second-,third,-fourth degree)/Accouchement compliqués par déchirure périnéale du 2-ème-3-ème et 4-ème dégré", scdPerinealTearComplications, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA20", "Delivery complicated by perineal tear (second-,third,-fourth degree)/Accouchement compliqués par déchirure périnéale du 2-ème-3-ème et 4-ème dégré", new Mapped(ciPerinealTearComplications, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 21
        SqlCohortDefinition scdCleftLip = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdCleftLip.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdCleftLip.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciCleftLip = Indicators.newCountIndicator("INTRA21: Cleft palate or Cleft lip/ Bec de lièvre ou Fente palatine", scdCleftLip, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA21", "Cleft palate or Cleft lip/ Bec de lièvre ou Fente palatine", new Mapped(ciCleftLip, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 22
        SqlCohortDefinition scdOtherCongenitalMalformation = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdOtherCongenitalMalformation.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdOtherCongenitalMalformation.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciOtherCongenitalMalformation = Indicators.newCountIndicator("INTRA22: Other congenital malformation/Autre malformation congénitale", scdOtherCongenitalMalformation, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA22", "Other congenital malformation/Autre malformation congénitale", new Mapped(ciOtherCongenitalMalformation, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 23
        SqlCohortDefinition scdDeliveriesUnder20 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdDeliveriesUnder20.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdDeliveriesUnder20.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciDeliveriesUnder20 = Indicators.newCountIndicator("INTRA23: Total Deliveries under 20 years Accouchement, femmes âgées moins de 20 ans", scdDeliveriesUnder20, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA23", "Total Deliveries under 20 years Accouchement, femmes âgées moins de 20 ans", new Mapped(ciDeliveriesUnder20, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 24
        SqlCohortDefinition scdDeliveriesUnder16 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdDeliveriesUnder16.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdDeliveriesUnder16.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciDeliveriesUnder16 = Indicators.newCountIndicator("INTRA24: Deliveries 15 years and under) / Accouchement, femmes âgées de 15 ans et moins", scdDeliveriesUnder16, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA24", "Deliveries 15 years and under) / Accouchement, femmes âgées de 15 ans et moins", new Mapped(ciDeliveriesUnder16, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 25
        SqlCohortDefinition scdMultipleDeliveries = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdMultipleDeliveries.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdMultipleDeliveries.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciMultipleDeliveries = Indicators.newCountIndicator("INTRA25: Multiple Pregnancies (women who delivered twins, triplets, etc.)/Grossesses multiples (Jumeaux, triplets, etc.)", scdMultipleDeliveries, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA25", "Multiple Pregnancies (women who delivered twins, triplets, etc.)/Grossesses multiples (Jumeaux, triplets, etc.)", new Mapped(ciMultipleDeliveries, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 26
        SqlCohortDefinition scdPrematureDeliveryConsultation = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPrematureDeliveryConsultation.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPrematureDeliveryConsultation.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPrematureDeliveryConsultation = Indicators.newCountIndicator("INTRA26: Women consulted for risk of premature delivery /Femmes qui ont consulté pour ménace d’accouchement prématuré", scdPrematureDeliveryConsultation, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA26", "Women consulted for risk of premature delivery /Femmes qui ont consulté pour ménace d’accouchement prématuré", new Mapped(ciPrematureDeliveryConsultation, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 27
        SqlCohortDefinition scdReceivedCorticosteroid = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdReceivedCorticosteroid.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdReceivedCorticosteroid.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciReceivedCorticosteroid = Indicators.newCountIndicator("INTRA27: Mother who received Corticosteroid in management of risk of premature delivery/Femmes avec ménace d’accouchement prématuré qui ayant recu les corticosteroides foetus", scdReceivedCorticosteroid, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA27", "Mother who received Corticosteroid in management of risk of premature delivery/Femmes avec ménace d’accouchement prématuré qui ayant recu les corticosteroides foetus", new Mapped(ciReceivedCorticosteroid, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 28
        SqlCohortDefinition scdConsultedWPPROM = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdConsultedWPPROM.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdConsultedWPPROM.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciConsultedWPPROM = Indicators.newCountIndicator("INTRA28: Women consulted with Preterm Premature rupture membranes (PPROM) /Les femmes qui ont consulté avec rupture premature des membranes sur grossesse non a terme", scdConsultedWPPROM, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA28", "Women consulted with Preterm Premature rupture membranes (PPROM) /Les femmes qui ont consulté avec rupture premature des membranes sur grossesse non a terme", new Mapped(ciConsultedWPPROM, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 29
        SqlCohortDefinition scdConsultedWPPROMReceivedProphylactic = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdConsultedWPPROMReceivedProphylactic.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdConsultedWPPROMReceivedProphylactic.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciConsultedWPPROMReceivedProphylactic = Indicators.newCountIndicator("INTRA29: Women consulted with Preterm Premature rupture membranes who received prophylactic antibiotics/Les femmes qui ont consulté avec rupture premature des membranes sur grossesse non a terme qui ont recu les antibiotiques", scdConsultedWPPROMReceivedProphylactic, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA29", "Women consulted with Preterm Premature rupture membranes who received prophylactic antibiotics/Les femmes qui ont consulté avec rupture premature des membranes sur grossesse non a terme qui ont recu les antibiotiques", new Mapped(ciConsultedWPPROMReceivedProphylactic, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 30
        SqlCohortDefinition scdReceivedOxytocinAfterBirth = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdReceivedOxytocinAfterBirth.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdReceivedOxytocinAfterBirth.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciReceivedOxytocinAfterBirth = Indicators.newCountIndicator("INTRA30: Women who received oxytocin immediately after birth for active management of third stage of labor / Les femmes qui ont recu l’Oxytocin après l’accouchement pour la prise en charge active du troisième stade du travail", scdReceivedOxytocinAfterBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA30", "Women who received oxytocin immediately after birth for active management of third stage of labor / Les femmes qui ont recu l’Oxytocin après l’accouchement pour la prise en charge active du troisième stade du travail", new Mapped(ciReceivedOxytocinAfterBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 31
        SqlCohortDefinition scdDeathDuringLabor = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdDeathDuringLabor.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdDeathDuringLabor.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciDeathDuringLabor = Indicators.newCountIndicator("INTRA31: Maternal deaths during labor, delivery and 24 hours after delivery / Décès maternel pendant le travail d’accouchement ou pendant 24 après accouchement", scdDeathDuringLabor, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA31", "Maternal deaths during labor, delivery and 24 hours after delivery / Décès maternel pendant le travail d’accouchement ou pendant 24 après accouchement", new Mapped(ciDeathDuringLabor, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 32
        SqlCohortDefinition scdReferredWhileInLabor = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdReferredWhileInLabor.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdReferredWhileInLabor.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciReferredWhileInLabor = Indicators.newCountIndicator("INTRA32: Mothers in labor referred to higher level for delivery /Femmes en travail qui ont été transférées niveau supérieur pour accouchement", scdReferredWhileInLabor, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA32", "Mothers in labor referred to higher level for delivery /Femmes en travail qui ont été transférées niveau supérieur pour accouchement", new Mapped(ciReferredWhileInLabor, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 33
        SqlCohortDefinition scdLiveBirths = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdLiveBirths.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdLiveBirths.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciLiveBirths = Indicators.newCountIndicator("INTRA33: Births, live/ Naissances vivantes", scdLiveBirths, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA33", "Births, live/ Naissances vivantes", new Mapped(ciLiveBirths, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 34
        SqlCohortDefinition scdBirthWeightGT2500 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdBirthWeightGT2500.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdBirthWeightGT2500.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciBirthWeightGT2500 = Indicators.newCountIndicator("INTRA34: Birth weight <2500gr (alive,) / Poids à la naissance < 2500 gr chez les nouveaux né vivant", scdBirthWeightGT2500, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA34", "Birth weight <2500gr (alive,) / Poids à la naissance < 2500 gr chez les nouveaux né vivant", new Mapped(ciBirthWeightGT2500, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 35
        SqlCohortDefinition scdAlivePremature22to37Weeks = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdAlivePremature22to37Weeks.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdAlivePremature22to37Weeks.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciAlivePremature22to37Weeks = Indicators.newCountIndicator("INTRA35: Premature newborn (alive, 22-37 weeks) /  les nouveaux né vivant qui sont prématurés (22-37 semaines)", scdAlivePremature22to37Weeks, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA35", "Premature newborn (alive, 22-37 weeks) /  les nouveaux né vivant qui sont prématurés (22-37 semaines)", new Mapped(ciAlivePremature22to37Weeks, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 36
        SqlCohortDefinition scdAliveBirthWeightGE2000GR = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdAliveBirthWeightGE2000GR.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdAliveBirthWeightGE2000GR.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciAliveBirthWeightGE2000GR = Indicators.newCountIndicator("INTRA36: Birth Weight <=2000gr(alive newborns ) Poids à la naissance 2000 gr et moins ( nouveaux nés vivants)", scdAliveBirthWeightGE2000GR, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA36", "Birth Weight <=2000gr(alive newborns ) Poids à la naissance 2000 gr et moins ( nouveaux nés vivants)", new Mapped(ciAliveBirthWeightGE2000GR, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 37
        SqlCohortDefinition scdStillBirthsFreshGE28WeeksOrGE1000GR = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdStillBirthsFreshGE28WeeksOrGE1000GR.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdStillBirthsFreshGE28WeeksOrGE1000GR.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciStillBirthsFreshGE28WeeksOrGE1000GR = Indicators.newCountIndicator("INTRA37: Still births fresh (>=28 weeks or >=1000grams) all/ Tous les morts- nés frais pesant 1000 gr au moins ou grossesses de 28 semaines au moins", scdStillBirthsFreshGE28WeeksOrGE1000GR, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA37", "Still births fresh (>=28 weeks or >=1000grams) all/ Tous les morts- nés frais pesant 1000 gr au moins ou grossesses de 28 semaines au moins", new Mapped(ciStillBirthsFreshGE28WeeksOrGE1000GR, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 38
        SqlCohortDefinition scdStillBirthsMaceratedGE28WeeksOrGE1000GR = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdStillBirthsMaceratedGE28WeeksOrGE1000GR.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdStillBirthsMaceratedGE28WeeksOrGE1000GR.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciStillBirthsMaceratedGE28WeeksOrGE1000GR = Indicators.newCountIndicator("INTRA38: Still births macerated ≥28 weeks or ≥1000 grams)/ Morts- nés macérés pesant 1000 gr au moins ou grossesses de 28 semaines au moins)", scdStillBirthsMaceratedGE28WeeksOrGE1000GR, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA38", "Still births macerated ≥28 weeks or ≥1000 grams)/ Morts- nés macérés pesant 1000 gr au moins ou grossesses de 28 semaines au moins)", new Mapped(ciStillBirthsMaceratedGE28WeeksOrGE1000GR, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 39
        SqlCohortDefinition scdStillBirthsMaceratedGE2500GR = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdStillBirthsMaceratedGE2500GR.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdStillBirthsMaceratedGE2500GR.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciStillBirthsMaceratedGE2500GR = Indicators.newCountIndicator("INTRA39: Stillbirths fresh (≥2500 grams/ Morts- nés frais pesant 2500gr au moins", scdStillBirthsMaceratedGE2500GR, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA39", "Stillbirths fresh (≥2500 grams/ Morts- nés frais pesant 2500gr au moins", new Mapped(ciStillBirthsMaceratedGE2500GR, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 40
        SqlCohortDefinition scdDeathsAtBirth = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdDeathsAtBirth.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdDeathsAtBirth.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciDeathsAtBirth = Indicators.newCountIndicator("INTRA40: Deaths at birth of live born babies ( within 30 minutes)/ Décès a la naissance pour les bebes nes vivants (endéans 30 minutes de la naissance)", scdDeathsAtBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA40", "Deaths at birth of live born babies ( within 30 minutes)/ Décès a la naissance pour les bebes nes vivants (endéans 30 minutes de la naissance)", new Mapped(ciDeathsAtBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 41
        SqlCohortDefinition scdNewBornsBreastfedIn1HrOfDelivery = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdNewBornsBreastfedIn1HrOfDelivery.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdNewBornsBreastfedIn1HrOfDelivery.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciNewBornsBreastfedIn1HrOfDelivery = Indicators.newCountIndicator("INTRA41: Newborns breastfed within 1 hour of delivery/Nouveaux-nés mis au sein/allaité endéans la première heure après naissance", scdNewBornsBreastfedIn1HrOfDelivery, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA41", "Newborns breastfed within 1 hour of delivery/Nouveaux-nés mis au sein/allaité endéans la première heure après naissance", new Mapped(ciNewBornsBreastfedIn1HrOfDelivery, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 42
        SqlCohortDefinition scdLiveNewbornsWNBreathCry = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdLiveNewbornsWNBreathCry.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdLiveNewbornsWNBreathCry.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciLiveNewbornsWNBreathCry = Indicators.newCountIndicator("INTRA42: Live Newborns who didn’t cry/breath at birth/Nouveau- nés vivants qui n’ont pas crie (pleuré)/respiré a la naissance", scdLiveNewbornsWNBreathCry, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA42", "Live Newborns who didn’t cry/breath at birth/Nouveau- nés vivants qui n’ont pas crie (pleuré)/respiré a la naissance", new Mapped(ciLiveNewbornsWNBreathCry, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // INTRA 43
        SqlCohortDefinition scdNewbornsWNBreathCry = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdNewbornsWNBreathCry.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdNewbornsWNBreathCry.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciNewbornsWNBreathCry = Indicators.newCountIndicator("INTRA43: Newborns alive who didn’t cry/breath at birth and were resuscitated successfully (cry/breath within 5 minutes, APGAR score >5 at 5min)/ Les nouveaux nes vivant réanimés avec succès (Ceux qui n’avaient pas crié/respiré a la naissance mais qui ont pu crier/respirer endéans 5 minutes après la réanimation, APGAR >5 a 5min)", scdNewbornsWNBreathCry, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        intraDataSetDefinition.addColumn("INTRA43", "Newborns alive who didn’t cry/breath at birth and were resuscitated successfully (cry/breath within 5 minutes, APGAR score >5 at 5min)/ Les nouveaux nes vivant réanimés avec succès (Ceux qui n’avaient pas crié/respiré a la naissance mais qui ont pu crier/respirer endéans 5 minutes après la réanimation, APGAR >5 a 5min)", new Mapped(ciNewbornsWNBreathCry, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
        return intraDataSetDefinition;
    }

    // PNC
    private CohortIndicatorDataSetDefinition buildPNCIndicator(CohortIndicatorDataSetDefinition pncDataSetDefinition) {
        // PNC 1
        SqlCohortDefinition scdVisitsIn24Hrs = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdVisitsIn24Hrs.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdVisitsIn24Hrs.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciVisitsIn24Hrs = Indicators.newCountIndicator("PNC1: Visits within 24 Hours of birth / CPoN 1 endéans 24 heures", scdVisitsIn24Hrs, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC1", "Visits within 24 Hours of birth / CPoN 1 endéans 24 heures", new Mapped(ciVisitsIn24Hrs, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // PNC 2
        SqlCohortDefinition scdScreenForAnemiaOnPNC1 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdScreenForAnemiaOnPNC1.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdScreenForAnemiaOnPNC1.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciScreenForAnemiaOnPNC1 = Indicators.newCountIndicator("PNC2: Mothers screened for anemia during PNC 1 Visit Mères depistées pour anémie pendant la CPoN 1", scdScreenForAnemiaOnPNC1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC2", "Mothers screened for anemia during PNC 1 Visit Mères depistées pour anémie pendant la CPoN 1", new Mapped(ciScreenForAnemiaOnPNC1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // PNC 3
        SqlCohortDefinition scdReceivedFolicAcid = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdReceivedFolicAcid.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdReceivedFolicAcid.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciReceivedFolicAcid = Indicators.newCountIndicator("PNC3: Mothers received Iron/Folic Acid during PNC1 Visit/ Meres ayant reçu le fer/acide folique pendant la CPoN1", scdReceivedFolicAcid, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC3", "Mothers received Iron/Folic Acid during PNC1 Visit/ Meres ayant reçu le fer/acide folique pendant la CPoN1", new Mapped(ciReceivedFolicAcid, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // PNC 4
        SqlCohortDefinition scdVisitsAt3rdDayAfterBirth = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdVisitsAt3rdDayAfterBirth.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdVisitsAt3rdDayAfterBirth.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciVisitsAt3rdDayAfterBirth = Indicators.newCountIndicator("PNC4: PNC 2 visits at 3rd Day after birth /CPoN 2 au 3eme jour après la naissance", scdVisitsAt3rdDayAfterBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC4", "PNC 2 visits at 3rd Day after birth /CPoN 2 au 3eme jour après la naissance", new Mapped(ciVisitsAt3rdDayAfterBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // PNC 5
        SqlCohortDefinition scdVisitsAtBW7n14thDayAFBirth = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdVisitsAtBW7n14thDayAFBirth.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdVisitsAtBW7n14thDayAFBirth.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciVisitsAtBW7n14thDayAFBirth = Indicators.newCountIndicator("PNC5: PNC 3 visits between 7th and 14th Day after birth /CPoN3 entre le 7eme et le 14-eme jour après la naissance", scdVisitsAtBW7n14thDayAFBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC5", "PNC 3 visits between 7th and 14th Day after birth /CPoN3 entre le 7eme et le 14-eme jour après la naissance", new Mapped(ciVisitsAtBW7n14thDayAFBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // PNC 6
        SqlCohortDefinition scdVisitsAt6WeeksAfterBirth = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdVisitsAt6WeeksAfterBirth.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdVisitsAt6WeeksAfterBirth.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciVisitsAt6WeeksAfterBirth = Indicators.newCountIndicator("PNC6: PNC 4 Visit at 6 weeks (42 days) after birth/CPoN4 a 6 semaines (42-eme jour) après la naissance", scdVisitsAt6WeeksAfterBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC6", "PNC 4 Visit at 6 weeks (42 days) after birth/CPoN4 a 6 semaines (42-eme jour) après la naissance", new Mapped(ciVisitsAt6WeeksAfterBirth, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // PNC 7
        SqlCohortDefinition scdScreenedByMUAConPNC4 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdScreenedByMUAConPNC4.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdScreenedByMUAConPNC4.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciScreenedByMUAConPNC4 = Indicators.newCountIndicator("PNC7: Mothers screened by MUAC for malnutrition during PNC4 Visit / Les mères depistées pour malnutrition avec MUAC pendant la CPoN 4", scdScreenedByMUAConPNC4, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC7", "Mothers screened by MUAC for malnutrition during PNC4 Visit / Les mères depistées pour malnutrition avec MUAC pendant la CPoN 4", new Mapped(ciScreenedByMUAConPNC4, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // PNC 8
        SqlCohortDefinition scdMUACLE21OnPNC4 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdMUACLE21OnPNC4.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdMUACLE21OnPNC4.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciMUACLE21OnPNC4 = Indicators.newCountIndicator("PNC8: Mothers malnourished (MUAC < 21 cm) during PNC4 Visit/ Les mères depistées malnourries pendant la CPoN 4(MUAC < 21 cm)", scdMUACLE21OnPNC4, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC8", "Mothers malnourished (MUAC < 21 cm) during PNC4 Visit/ Les mères depistées malnourries pendant la CPoN 4(MUAC < 21 cm)", new Mapped(ciMUACLE21OnPNC4, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // PNC 9
        SqlCohortDefinition scdScreenedForAnemiaOnPNC4 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdScreenedForAnemiaOnPNC4.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdScreenedForAnemiaOnPNC4.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciScreenedForAnemiaOnPNC4 = Indicators.newCountIndicator("PNC9: Mothers screened for anemia during PNC 4 Visit/Les mères depistées pour anémie pendant la CPoN4", scdScreenedForAnemiaOnPNC4, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC9", "Mothers screened for anemia during PNC 4 Visit/Les mères depistées pour anémie pendant la CPoN4", new Mapped(ciScreenedForAnemiaOnPNC4, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // PNC 10
        SqlCohortDefinition scdWithAnemiaOnPNC1and4 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdWithAnemiaOnPNC1and4.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdWithAnemiaOnPNC1and4.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciWithAnemiaOnPNC1and4 = Indicators.newCountIndicator("PNC10: Mother with anemia (Hb<9.5 Gr/Dl) detected during PNC Visits (PNC1 and PNC4) /Les mères detectées anemiques durant les CPoN 1 et CPoN4)", scdWithAnemiaOnPNC1and4, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        pncDataSetDefinition.addColumn("PNC10", "Mother with anemia (Hb<9.5 Gr/Dl) detected during PNC Visits (PNC1 and PNC4) /Les mères detectées anemiques durant les CPoN 1 et CPoN4)", new Mapped(ciWithAnemiaOnPNC1and4, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
        return pncDataSetDefinition;
    }

    //Immunization
    private CohortIndicatorDataSetDefinition buildEPIIndicator(CohortIndicatorDataSetDefinition epiDataSetDefinition) {
        // EPI 1
        SqlCohortDefinition scdBCG = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdBCG.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdBCG.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciBCG = Indicators.newCountIndicator("EPI1: BCG", scdBCG, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI1", "BCG", new Mapped(ciBCG, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 2
        SqlCohortDefinition scdP0 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdP0.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdP0.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciP0 = Indicators.newCountIndicator("EPI2: Polio-Zero (P0)", scdP0, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI2", "Polio-Zero (P0)", new Mapped(ciP0, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 3
        SqlCohortDefinition scdOPV1 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdOPV1.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdOPV1.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciOPV1 = Indicators.newCountIndicator("EPI3: Polio-1 (OPV1)", scdOPV1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI3", "Polio-1 (OPV1)", new Mapped(ciOPV1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 4
        SqlCohortDefinition scdOPV2 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdOPV2.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdOPV2.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciOPV2 = Indicators.newCountIndicator("EPI4: Polio-2 (OPV2)", scdOPV2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI4", "Polio-2 (OPV2)", new Mapped(ciOPV2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 5
        SqlCohortDefinition scdOPV3 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdOPV3.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdOPV3.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciOPV3 = Indicators.newCountIndicator("EPI5: Polio-3 (OPV3)", scdOPV3, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI5", "Polio-3 (OPV3)", new Mapped(ciOPV3, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 6
        SqlCohortDefinition scdIPV = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdIPV.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdIPV.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciIPV = Indicators.newCountIndicator("EPI6: IPV", scdIPV, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI6", "IPV", new Mapped(ciIPV, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 7
        SqlCohortDefinition scdHib1 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdHib1.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdHib1.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciHib1 = Indicators.newCountIndicator("EPI7: DTP-HepB-Hib1", scdHib1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI7", "DTP-HepB-Hib1", new Mapped(ciHib1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 8
        SqlCohortDefinition scdHib2 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdHib2.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdHib2.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciHib2 = Indicators.newCountIndicator("EPI8: DTP-HepB-Hib2", scdHib2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI8", "DTP-HepB-Hib2", new Mapped(ciHib2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 9
        SqlCohortDefinition scdHib3 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdHib3.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdHib3.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciHib3 = Indicators.newCountIndicator("EPI9: DTP-HepB-Hib3", scdHib3, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI9", "DTP-HepB-Hib3", new Mapped(ciHib3, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 10
        SqlCohortDefinition scdPneumococus1 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPneumococus1.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPneumococus1.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPneumococus1 = Indicators.newCountIndicator("EPI10: Pneumococus 1", scdPneumococus1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI10", "Pneumococus 1", new Mapped(ciPneumococus1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 11
        SqlCohortDefinition scdPneumococus2 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPneumococus2.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPneumococus2.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPneumococus2 = Indicators.newCountIndicator("EPI11: Pneumococus 2", scdPneumococus2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI11", "Pneumococus 2", new Mapped(ciPneumococus2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 12
        SqlCohortDefinition scdPneumococus3 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPneumococus3.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPneumococus3.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPneumococus3 = Indicators.newCountIndicator("EPI12: Pneumococus 3", scdPneumococus3, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI12", "Pneumococus 3", new Mapped(ciPneumococus3, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 13
        SqlCohortDefinition scdRota1 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdRota1.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdRota1.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciRota1 = Indicators.newCountIndicator("EPI13: Rotavirus 1", scdRota1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI13", "Rotavirus 1", new Mapped(ciRota1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI 14
        SqlCohortDefinition scdRota2 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdRota2.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdRota2.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciRota2 = Indicators.newCountIndicator("EPI14: Rotavirus 2", scdRota2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI14", "Rotavirus 2", new Mapped(ciRota2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI15
        SqlCohortDefinition scdMR1 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdMR1.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdMR1.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciMR1 = Indicators.newCountIndicator("EPI15: Measles&Rubella (MR)1", scdMR1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI15", "Measles&Rubella (MR)1", new Mapped(ciMR1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI16
        SqlCohortDefinition scdMR2 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdMR2.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdMR2.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciMR2 = Indicators.newCountIndicator("EPI16: Measles&Rubella (MR)2", scdMR2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI16", "Measles&Rubella (MR)2", new Mapped(ciMR2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI17
        SqlCohortDefinition scdHPV1 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdHPV1.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdHPV1.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciHPV1 = Indicators.newCountIndicator("EPI17: HPV 1", scdHPV1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI17", "HPV 1", new Mapped(ciHPV1, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // EPI18
        SqlCohortDefinition scdHPV2 = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdHPV2.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdHPV2.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciHPV2 = Indicators.newCountIndicator("EPI18: HPV 1", scdHPV2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        epiDataSetDefinition.addColumn("EPI18", "HPV 1", new Mapped(ciHPV2, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        return epiDataSetDefinition;
    }

    //TODO 16 Indicators
    //Integrated Management of Childhood Illnesses
    private CohortIndicatorDataSetDefinition buildIMCIIndicator(CohortIndicatorDataSetDefinition imciDataSetDefinition) {
        // IMCI1
        SqlCohortDefinition scdreceivedChild = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdreceivedChild.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdreceivedChild.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator cireceivedChild = Indicators.newCountIndicator("IMCI1: Children received in IMCI services/ Enfants reçus dans le service PCIME", scdreceivedChild, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI1", "Children received in IMCI services/ Enfants reçus dans le service PCIME", new Mapped(cireceivedChild, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI2
        SqlCohortDefinition scdChildWGnrlDanger = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdChildWGnrlDanger.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdChildWGnrlDanger.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciChildWGnrlDanger = Indicators.newCountIndicator("IMCI2: Children who had general danger signs / Enfants consultés ayant des signes généraux de danger", scdChildWGnrlDanger, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI2", "Children who had general danger signs / Enfants consultés ayant des signes généraux de danger", new Mapped(ciChildWGnrlDanger, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI3
        SqlCohortDefinition scdChildConsultedFever = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdChildConsultedFever.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdChildConsultedFever.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciChildConsultedFever = Indicators.newCountIndicator("IMCI3: Children consulted for fever / Enfants consultés  pour la fièvre", scdChildConsultedFever, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI3", "Children consulted for fever / Enfants consultés  pour la fièvre", new Mapped(ciChildConsultedFever, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI4
        SqlCohortDefinition scdChildConsultedCough = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdChildConsultedCough.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdChildConsultedCough.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciChildConsultedCough = Indicators.newCountIndicator("IMCI4: Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire", scdChildConsultedCough, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI4", "Children consulted for Cough or difficult breathing / Enfants consultés pour la toux ou  difficulté respiratoire", new Mapped(ciChildConsultedCough, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI5
        SqlCohortDefinition scdChildConsultedDiarrhea = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdChildConsultedDiarrhea.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdChildConsultedDiarrhea.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciChildConsultedDiarrhea = Indicators.newCountIndicator("IMCI5: Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée", scdChildConsultedDiarrhea, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI5", "Children consulted for Diarrhea diseases / Enfants consultés pour la Diarrhée", new Mapped(ciChildConsultedDiarrhea, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI6
        SqlCohortDefinition scdChildConsultedEar = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdChildConsultedEar.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdChildConsultedEar.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciChildConsultedEar = Indicators.newCountIndicator("IMCI6: Children consulted for  Ear diseases/ Maladies de l’Oreille", scdChildConsultedEar, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI6", "Children consulted for  Ear diseases/ Maladies de l’Oreille", new Mapped(ciChildConsultedEar, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI7
        SqlCohortDefinition scdChildrenReferredtoHosp = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdChildrenReferredtoHosp.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdChildrenReferredtoHosp.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciChildrenReferredtoHosp = Indicators.newCountIndicator("IMCI7: Children referred to Hospital / Enfants référés a l’ Hopital ", scdChildrenReferredtoHosp, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI7", "Children referred to Hospital / Enfants référés a l’ Hopital ", new Mapped(ciChildrenReferredtoHosp, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        //Diagnoses
        // IMCI8
        SqlCohortDefinition scdBacterialInfection = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdBacterialInfection.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdBacterialInfection.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciBacterialInfection = Indicators.newCountIndicator("IMCI8: Bacterial infection or severe disease / Infection Bactérienne grave ou maladies grave", scdBacterialInfection, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI8", "Bacterial infection or severe disease / Infection Bactérienne grave ou maladies grave", new Mapped(ciBacterialInfection, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI9
        SqlCohortDefinition scdPneumonia = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPneumonia.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPneumonia.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPneumonia = Indicators.newCountIndicator("IMCI9: Pneumonia / Pneumonie", scdPneumonia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI9", "Pneumonia / Pneumonie", new Mapped(ciPneumonia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI10
        SqlCohortDefinition scdOtherRespiratoryDiseases = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdOtherRespiratoryDiseases.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdOtherRespiratoryDiseases.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciOtherRespiratoryDiseases = Indicators.newCountIndicator("IMCI10: Other respiratory diseases/Autre maladies des voies respiratoires", scdOtherRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI10", "Other respiratory diseases/Autre maladies des voies respiratoires", new Mapped(ciOtherRespiratoryDiseases, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI11
        SqlCohortDefinition scdVerySevereFebrile = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdVerySevereFebrile.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdVerySevereFebrile.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciVerySevereFebrile = Indicators.newCountIndicator("IMCI11: Very severe Febrile disease / Maladie fébrile très grave", scdVerySevereFebrile, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI11", "Very severe Febrile disease / Maladie fébrile très grave", new Mapped(ciVerySevereFebrile, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI12
        SqlCohortDefinition scdSevereAnemia = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdSevereAnemia.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdSevereAnemia.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciSevereAnemia = Indicators.newCountIndicator("IMCI12: Severe anemia / Anémie sévère", scdSevereAnemia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI12", "Severe anemia / Anémie sévère", new Mapped(ciSevereAnemia, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI13
        SqlCohortDefinition scdMalnutrition = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdMalnutrition.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdMalnutrition.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciMalnutrition = Indicators.newCountIndicator("IMCI13: Malnutrition / Malnutrition", scdMalnutrition, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI13", "Malnutrition / Malnutrition", new Mapped(ciMalnutrition, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI14
        SqlCohortDefinition scdSkinInfections = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdSkinInfections.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdSkinInfections.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciSkinInfections = Indicators.newCountIndicator("IMCI14: Skin Infections (all forms) / Infection Cutanées (toutes les formes)", scdSkinInfections, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI14", "Skin Infections (all forms) / Infection Cutanées (toutes les formes)", new Mapped(ciSkinInfections, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI15
        SqlCohortDefinition scdIntestinalParasites = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdIntestinalParasites.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdIntestinalParasites.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciIntestinalParasites = Indicators.newCountIndicator("IMCI15: Intestinal parasites / Parasitoses intestinales", scdIntestinalParasites, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI15", "Intestinal parasites / Parasitoses intestinales", new Mapped(ciIntestinalParasites, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // IMCI16
        SqlCohortDefinition scdEyeProblems = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdEyeProblems.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdEyeProblems.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciEyeProblems = Indicators.newCountIndicator("IMCI16: Eye problems/ Problème des yeux", scdEyeProblems, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        imciDataSetDefinition.addColumn("IMCI16", "Eye problems/ Problème des yeux", new Mapped(ciEyeProblems, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
        return imciDataSetDefinition;
    }

    //TODO 8 Indicators
    //Nutrition Screening
    private CohortIndicatorDataSetDefinition buildGMPIIndicator(CohortIndicatorDataSetDefinition gmpDataSetDefinition) {
        // GMP1
        SqlCohortDefinition scdScreened4Malnutrition = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdScreened4Malnutrition.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdScreened4Malnutrition.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciScreened4Malnutrition = Indicators.newCountIndicator("GMP1: Screened for malnutrition/ Dépistage de la malnutrition", scdScreened4Malnutrition, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gmpDataSetDefinition.addColumn("GMP1", "Screened for malnutrition/ Dépistage de la malnutrition", new Mapped(ciScreened4Malnutrition, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GMP2
        SqlCohortDefinition scdMalnourished = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdMalnourished.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdMalnourished.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciMalnourished = Indicators.newCountIndicator("GMP2: Malnourished total: Malnouris (total)", scdMalnourished, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gmpDataSetDefinition.addColumn("GMP2", "Malnourished total: Malnouris (total)", new Mapped(ciMalnourished, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GMP3
        SqlCohortDefinition scdMalnutritionAcuteSevere = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdMalnutritionAcuteSevere.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdMalnutritionAcuteSevere.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciMalnutritionAcuteSevere = Indicators.newCountIndicator("GMP3: Malnutrition acute severe/ Malnutrition aigüe sévère(sans complications)", scdMalnutritionAcuteSevere, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gmpDataSetDefinition.addColumn("GMP3", "Malnutrition acute severe/ Malnutrition aigüe sévère(sans complications)", new Mapped(ciMalnutritionAcuteSevere, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GMP4
        SqlCohortDefinition scdMalnutritionAcuteModerate = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdMalnutritionAcuteModerate.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdMalnutritionAcuteModerate.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciMalnutritionAcuteModerate = Indicators.newCountIndicator("GMP4: Malnutrition acute moderate/ Malnutrition aigüe modérée(sans complications)", scdMalnutritionAcuteModerate, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gmpDataSetDefinition.addColumn("GMP4", "Malnutrition acute moderate/ Malnutrition aigüe modérée(sans complications)", new Mapped(ciMalnutritionAcuteModerate, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GMP5
        SqlCohortDefinition scdUnderweight = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdUnderweight.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdUnderweight.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciUnderweight = Indicators.newCountIndicator("GMP5: Underweight/Insuffisance pondérale", scdUnderweight, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gmpDataSetDefinition.addColumn("GMP5", "Underweight/Insuffisance pondérale", new Mapped(ciUnderweight, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GMP6
        SqlCohortDefinition scdChronicMalnutrition = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdChronicMalnutrition.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdChronicMalnutrition.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciChronicMalnutrition = Indicators.newCountIndicator("GMP6: Malnutrition chronic (stunting)/ Malnutrition chronique", scdChronicMalnutrition, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gmpDataSetDefinition.addColumn("GMP6", "Malnutrition chronic (stunting)/ Malnutrition chronique", new Mapped(ciChronicMalnutrition, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GMP7
        SqlCohortDefinition scdReferredtoMalnutritionOPD = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdReferredtoMalnutritionOPD.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdReferredtoMalnutritionOPD.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciReferredtoMalnutritionOPD = Indicators.newCountIndicator("GMP7: Referred to outpatient malnutrition program/ Référé au programme de la malnutrition (ambulatoire)", scdReferredtoMalnutritionOPD, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gmpDataSetDefinition.addColumn("GMP7", "Referred to outpatient malnutrition program/ Référé au programme de la malnutrition (ambulatoire)", new Mapped(ciReferredtoMalnutritionOPD, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GMP8
        SqlCohortDefinition scdReferredtoMalnutritionIPD = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdReferredtoMalnutritionIPD.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdReferredtoMalnutritionIPD.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciReferredtoMalnutritionIPD = Indicators.newCountIndicator("GMP8: Referred to inpatient malnutrition program (Hospital)/ Référé au programme de PEC de la malnutrition (Hôpital)", scdReferredtoMalnutritionIPD, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gmpDataSetDefinition.addColumn("GMP8", "Referred to inpatient malnutrition program (Hospital)/ Référé au programme de PEC de la malnutrition (Hôpital)", new Mapped(ciReferredtoMalnutritionIPD, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
        return gmpDataSetDefinition;
    }

    //TODO 13 Indicators
    //Gender Based Violence
    private CohortIndicatorDataSetDefinition buildGBVIndicator(CohortIndicatorDataSetDefinition gbvDataSetDefinition) {
        // GBV1
        SqlCohortDefinition scdSexViolenceSymptoms = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdSexViolenceSymptoms.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdSexViolenceSymptoms.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciSexViolenceSymptoms = Indicators.newCountIndicator("GBV1: GBV victims with symptoms of sexual violence (new cases)", scdSexViolenceSymptoms, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV1", "GBV victims with symptoms of sexual violence (new cases)", new Mapped(ciSexViolenceSymptoms, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV2
        SqlCohortDefinition scdPhysViolenceSymptoms = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdPhysViolenceSymptoms.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdPhysViolenceSymptoms.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciPhysViolenceSymptoms = Indicators.newCountIndicator("GBV2: GBV victims with symptoms of physical violence (new cases)", scdPhysViolenceSymptoms, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV2", "GBV victims with symptoms of physical violence (new cases)", new Mapped(ciPhysViolenceSymptoms, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV3
        SqlCohortDefinition scdEmotionalViolenceSymptoms = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdEmotionalViolenceSymptoms.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdEmotionalViolenceSymptoms.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciEmotionalViolenceSymptoms = Indicators.newCountIndicator("GBV3: GBV victims with symptoms of emotional violence (new cases)", scdEmotionalViolenceSymptoms, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV3", "GBV victims with symptoms of emotional violence (new cases)", new Mapped(ciEmotionalViolenceSymptoms, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV4
        SqlCohortDefinition scdEconomicViolenceSymptoms = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdEconomicViolenceSymptoms.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdEconomicViolenceSymptoms.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciEconomicViolenceSymptoms = Indicators.newCountIndicator("GBV4: GBV victims with symptoms of economic violence (new cases)", scdEconomicViolenceSymptoms, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV4", "GBV victims with symptoms of economic violence (new cases)", new Mapped(ciEconomicViolenceSymptoms, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV5
        SqlCohortDefinition scdRef2HighLevelHF4Care = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdRef2HighLevelHF4Care.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdRef2HighLevelHF4Care.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciRef2HighLevelHF4Care = Indicators.newCountIndicator("GBV5: GBV victims referred for care to higher level health facility", scdRef2HighLevelHF4Care, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV5", "GBV victims referred for care to higher level health facility", new Mapped(ciRef2HighLevelHF4Care, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV6
        SqlCohortDefinition scdRef2HFByPolice = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdRef2HFByPolice.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdRef2HFByPolice.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciRef2HFByPolice = Indicators.newCountIndicator("GBV6: GBV victims referred to this facility by police", scdRef2HFByPolice, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV6", "GBV victims referred to this facility by police", new Mapped(ciRef2HFByPolice, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV7
        SqlCohortDefinition scdRef2HFByCHW = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdRef2HFByCHW.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdRef2HFByCHW.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciRef2HFByCHW = Indicators.newCountIndicator("GBV7: GBV victims referred to this facility by community health workers", scdRef2HFByCHW, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV7", "GBV victims referred to this facility by community health workers", new Mapped(ciRef2HFByCHW, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV8
        SqlCohortDefinition scdHIVPosVictim3MonthsAfterExpo = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdHIVPosVictim3MonthsAfterExpo.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdHIVPosVictim3MonthsAfterExpo.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciHIVPosVictim3MonthsAfterExpo = Indicators.newCountIndicator("GBV8: GBV victims HIV+ sero-conversion 3 months after exposure", scdHIVPosVictim3MonthsAfterExpo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV8", "GBV victims HIV+ sero-conversion 3 months after exposure", new Mapped(ciHIVPosVictim3MonthsAfterExpo, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV9
        SqlCohortDefinition scdIrreversibleDisabilitiesDue2GBV = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdIrreversibleDisabilitiesDue2GBV.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdIrreversibleDisabilitiesDue2GBV.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciIrreversibleDisabilitiesDue2GBV = Indicators.newCountIndicator("GBV9: GBV victims with irreversible disabilities due to GBV", scdIrreversibleDisabilitiesDue2GBV, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV9", "GBV victims with irreversible disabilities due to GBV", new Mapped(ciIrreversibleDisabilitiesDue2GBV, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV10
        SqlCohortDefinition scdDeathsDue2GBV = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdDeathsDue2GBV.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdDeathsDue2GBV.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciDeathsDue2GBV = Indicators.newCountIndicator("GBV10: GBV victim deaths", scdDeathsDue2GBV, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV10", "GBV victim deaths", new Mapped(ciDeathsDue2GBV, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV11
        SqlCohortDefinition scdFourWeeksAfterExposure = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdFourWeeksAfterExposure.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdFourWeeksAfterExposure.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciFourWeeksAfterExposure = Indicators.newCountIndicator("GBV11: GBV victims pregnant 4 weeks after exposure", scdFourWeeksAfterExposure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV11", "GBV victims pregnant 4 weeks after exposure", new Mapped(ciFourWeeksAfterExposure, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV12
        SqlCohortDefinition scdReceivedEmergencyContraceptionIn72Hrs = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdReceivedEmergencyContraceptionIn72Hrs.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdReceivedEmergencyContraceptionIn72Hrs.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciReceivedEmergencyContraceptionIn72Hrs = Indicators.newCountIndicator("GBV12: GBV victims received emergency contraception within 72 hours", scdReceivedEmergencyContraceptionIn72Hrs, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV12", "GBV victims received emergency contraception within 72 hours", new Mapped(ciReceivedEmergencyContraceptionIn72Hrs, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");

        // GBV13
        SqlCohortDefinition scdReceivedPrepIn72Hrs = new SqlCohortDefinition("select patient_id from encounter where form_id=" + ancEnrolmentForm.getFormId() + " and encounter_datetime>= :startDate and encounter_datetime<= :endDate and voided=0");
        scdReceivedPrepIn72Hrs.addParameter(new Parameter("startDate", "StartDate", Date.class));
        scdReceivedPrepIn72Hrs.addParameter(new Parameter("endDate", "EndDate", Date.class));

        CohortIndicator ciReceivedPrepIn72Hrs = Indicators.newCountIndicator("GBV13: GBV victims received post exposure HIV prophylaxis within 72 hours", scdReceivedPrepIn72Hrs, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}"));

        gbvDataSetDefinition.addColumn("GBV13", "GBV victims received post exposure HIV prophylaxis within 72 hours", new Mapped(ciReceivedPrepIn72Hrs, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
        
        return gbvDataSetDefinition;
    }

    //TODO 13 Indicators
    //Family Planning
    private CohortIndicatorDataSetDefinition buildFPIndicator(CohortIndicatorDataSetDefinition fpDataSetDefinition) {

        return fpDataSetDefinition;
    }

}
